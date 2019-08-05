package com.znv.fssrqs.service.hbase;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.common.PhoenixSqlClient;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.util.FeatureCompUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by dongzelong on  2019/6/20 14:38.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
@Slf4j
public class AlarmDataService extends PhoenixSqlClient {
    private String tableName = HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.ALARM_TABLE_NAME);
    private static final String OPTIME = "op_time";
    private static final String ALARMTYPE = "alarm_type";
    private static final String LIBID = "lib_id";
    private static final String PERSONID = "person_id";
    private static final String CONFIMTIME = "confirm_time";
    private static final int SIMTHRESHOLD = 50;
    private static final int EXPORTSIZE = 10000;

    public void insert(JSONObject insertData, Connection phoenixConn) {
        if (insertData.containsKey(ALARMTYPE) && null != insertData.getInteger(ALARMTYPE)
                && insertData.containsKey(OPTIME) && !insertData.getString(OPTIME).isEmpty()
                && insertData.containsKey(PERSONID) && !insertData.getString(PERSONID).isEmpty()
                && insertData.containsKey(LIBID) && null != insertData.getInteger(LIBID)) {
            //获取person_id
            String personIdStr = insertData.getString(PERSONID);
            insertData.put(PERSONID, personIdStr);
            //修改确认时间
            long currentTime = System.currentTimeMillis();
            Date timeDate = new Date(currentTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String confirmTime = sdf.format(timeDate);
            insertData.put(CONFIMTIME, confirmTime);
            super.insert(insertData, tableName, phoenixConn);
        } else {
            throw new RuntimeException("参数非法,请检查");
        }
    }

    public JSONObject query(JSONObject searchData, Connection phoenixConn) throws Exception {
        if (phoenixConn == null) {
            throw new Exception("phoenixConn is null");
        }
        JSONObject result = new JSONObject();
        List<JSONObject> objList = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();
        // 首次查询先查询总条数和总页数，翻页查询不需要再次查询总条数和总页数
        int count = searchData.getInteger("count");
        int totalPage = searchData.getInteger("total_page");
        // 每页显示的条数
        if (searchData.containsKey("page_size") && null != searchData.getInteger("page_size")) {
            int pageSize = searchData.getInteger("page_size");
            // 当传入的每页行数为0时默认每页10条
            if (pageSize <= 0) {
                pageSize = 10;
            }
            searchData.put("page_size", pageSize);
        }

        if (count == -1 && totalPage == -1) {
            JSONObject total = getCountAndPage(searchData, phoenixConn);
            count = total.getIntValue("count");
            totalPage = total.getIntValue("total_page");
        }

        //没有符合条件的记录，直接返回
        if (count == 0) {
            result.put("Data", objList);
            result.put("Time", System.currentTimeMillis() - currentTimeMillis);
            result.put("Count", count);
            result.put("TotalPage", totalPage);
            result.put("ID", CommonConstant.PhoenixProtocolId.QUERY_ALARM);
        } else {
            StringBuilder sql = new StringBuilder();
            int pageNo = searchData.getIntValue("page_no");
            int pageSize = searchData.getIntValue("page_size");
            JSONObject queryTerm = null;
            if (searchData.containsKey("query_term")) {
                queryTerm = searchData.getJSONObject("query_term");
            }
            //按图片查询需要用到udf，做特殊处理
            int sim = -1;
            String feature = "";
            boolean isSearchFeature = false;
            if (queryTerm != null && queryTerm.containsKey("feature") && !queryTerm.getString("feature").isEmpty()) {
                isSearchFeature = true;
                feature = queryTerm.getString("feature");
                queryTerm.remove("feature");

                // 添加对相似度阈值的判断
                sim = SIMTHRESHOLD;
                if (null != queryTerm.getInteger("sim") && queryTerm.getIntValue("sim") > SIMTHRESHOLD) {
                    sim = queryTerm.getIntValue("sim");
                }

                queryTerm.remove("sim");
            }

            FeatureCompUtil fc = new FeatureCompUtil();
            fc.setFeaturePoints(PhoenixService.getPoints());
            float simThresholdFloat = fc.reversalNormalize(sim / 100.00F);
            // 多值查询条件
            JSONObject queryMulti = null;
            if (searchData.containsKey("query_multi")) {
                queryMulti = searchData.getJSONObject("query_multi");
            }

            // 范围条件
            JSONObject queryRange = null;
            String orderType = "";
            String orderField = "";
            if (searchData.containsKey("query_range")) {
                queryRange = searchData.getJSONObject("query_range");
                orderField = queryRange.getString("order_field");
                orderType = queryRange.getString("order_type");
            }

            // 优化方案
            sql.append("select ");
            // 只返回告警表中需要的字段
            for (String key : schema.keySet()) {
                // 去掉key中的列族信息
                if (key.contains(".")) {
                    key = key.split("\\.")[1];
                }
                sql.append(key)./* append(" as ").append(key). */append(",");
            }

            if (isSearchFeature) {
                // 查询结果添加查询图片与实时图片间的相似度
                sql.append("FeatureComp(rt_feature,?) as sim");
            } else {
                // 去掉多余的逗号
                int idx1 = sql.lastIndexOf(",");
                if (idx1 != -1) {
                    sql.delete(idx1, sql.length());
                }
            }
            sql.append(" from ").append(tableName).append(" WHERE");
            boolean isAddAnd = false;
            // 单值条件
            if (queryTerm != null) {
                boolean isDeleteAnd = false;
                for (Object key : queryTerm.keySet()) {
                    sql.append(" ").append(key.toString() + " = ? AND ");
                    isAddAnd = true;
                    isDeleteAnd = true;
                }
                if (isDeleteAnd) {
                    int index = sql.lastIndexOf("AND");
                    if (index != -1) {
                        sql.delete(index, sql.length());
                    }
                }
            }

            // 多值条件
            if (queryMulti != null) {
                if (isAddAnd && queryMulti.size() != 0) {
                    sql.append(" AND ");
                }
                boolean isDeleteAnd = false;
                for (Object key : queryMulti.keySet()) {
                    Object value = queryMulti.get(key);
                    sql.append(" ").append(key).append(" IN (");
                    if (value instanceof ArrayList) {
                        for (Object ele : (ArrayList) value) {
                            sql.append("?,");
                        }
                        int index = sql.lastIndexOf(",");
                        if (index != -1) {
                            sql.delete(index, sql.length());
                        }
                        sql.append(")");
                    }
                    sql.append(" AND ");

                    isAddAnd = true;
                    isDeleteAnd = true;
                }
                if (isDeleteAnd) {
                    int index = sql.lastIndexOf("AND");
                    if (index != -1) {
                        sql.delete(index, sql.length());
                    }
                }
            }
            if (queryRange.size() != 0) {
                if (isAddAnd) {
                    sql.append(" AND ");
                }
                sql.append(" op_time BETWEEN '").append(queryRange.getString("start_time")).append("' AND '")
                        .append(queryRange.getString("end_time")).append("'");
                isAddAnd = true;
            }

            // 按图片查询,按照比对相似度排序，默认相似度倒排序
            if (isSearchFeature /* !feature.equals("") */) {
                if (isAddAnd) {
                    sql.append(" AND ");
                }
                // sql.append(" FeatureComp(rt_feature,?) >= ").append(sim).append(" ");
                sql.append(" FeatureComp(rt_feature,?) >= ").append(simThresholdFloat).append(" "); // [lq-modify-2018-05-21]

                // 支持按sim 和 告警时间排序
                if ("0".equals(orderField)) {
                    sql.append(" order by FeatureComp(rt_feature,?) ");
                } else {
                    sql.append(" order by op_time ");
                }

            } else if ("0".equals(orderField)) {
                // 不按图片查询，按照时间排序
                sql.append(" order by similarity ");
            } else /* if ("1".equals(orderField)) */ {
                sql.append(" order by op_time ");
            }
            if ("0".equals(orderType)) {
                sql.append(" desc ");
            } else {
                sql.append(" asc ");
            }

            sql.append(" limit ").append(pageSize).append(" offset ").append((pageNo - 1) * pageSize);
            int i = 1;
            ResultSet rs = null;
            try (PreparedStatement stat = phoenixConn.prepareStatement(sql.toString())) {
                // 按图片查询（返回字段）
                if (isSearchFeature) {
                    byte[] byteFeature = Base64.getDecoder().decode(feature);
                    stat.setObject(i, byteFeature);
                    i++;
                }
                // 单值条件
                if (queryTerm != null) {
                    for (Object key : queryTerm.keySet()) {
                        stat.setObject(i, queryTerm.get(key));
                        i++;
                    }
                }

                // 多值条件
                if (queryMulti != null) {
                    for (Object key : queryMulti.keySet()) {
                        Object value = queryMulti.get(key);
                        if (value instanceof ArrayList) {
                            for (Object ele : (ArrayList) value) {
                                stat.setObject(i, ele);
                                i++;
                            }
                        }
                    }
                }

                // 按图片查询（查询条件）
                if (isSearchFeature) {
                    byte[] byteFeature = Base64.getDecoder().decode(feature);
                    stat.setObject(i, byteFeature);
                    i++;

                    if ("0".equals(orderField)) {
                        stat.setObject(i, byteFeature);
                        i++;
                    }
                }

                rs = stat.executeQuery();

                while (rs.next()) {
                    JSONObject record = new JSONObject();
                    ResultSetMetaData rsMetaData = rs.getMetaData();
                    int columnCount = rsMetaData.getColumnCount();
                    for (int column = 0; column < columnCount; column++) {
                        String field = rsMetaData.getColumnLabel(column + 1).toLowerCase();
                        String[] fieldNames = field.split("_");
                        StringBuffer sb = new StringBuffer();
                        for (String fieldName : fieldNames) {
                            sb.append(fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
                        }
                        record.put(sb.toString(), rs.getObject(field));
                    }

                    if (record.containsKey("sim") && null != record.get("sim")) {
                        float simFlloat = fc.Normalize(Float.parseFloat(record.get("sim").toString()));
                        record.put("Sim", simFlloat);
                        record.remove("sim");
                    }
                    objList.add(record);
                }
            } catch (Exception e) {
                log.error("query history failed {}", e);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                        log.error("alarmSearchClient query release resource error: ", e);
                    }
                }
            }

            result.put("Data", objList);
            result.put("Time", System.currentTimeMillis() - currentTimeMillis);
            result.put("TotalPage", totalPage);
            result.put("Count", count);
            result.put("ID", CommonConstant.PhoenixProtocolId.QUERY_ALARM);
        }

        return result;
    }

    private JSONObject getCountAndPage(JSONObject searchData, Connection phoenixConn) throws Exception {
        StringBuilder sql = new StringBuilder();
        JSONObject result = new JSONObject();
        int pageSize = 0;

        // 每页显示的条数
        if (searchData.containsKey("page_size") && null != searchData.getInteger("page_size")) {
            pageSize = searchData.getInteger("page_size");
            // 当传入的每页行数为0时默认每页10条
            if (pageSize <= 0) {
                pageSize = 10;
            }
        }

        // 单值查询条件
        JSONObject queryTerm = null;
        if (searchData.containsKey("query_term")) {
            queryTerm = searchData.getJSONObject("query_term");
        }

        // 按图片查询需要用到udf，做特殊处理
        int sim = -1;
        String feature = "";
        if (queryTerm != null && queryTerm.containsKey("feature")) {
            feature = queryTerm.getString("feature");
            // queryTerm.remove("feature");

            sim = SIMTHRESHOLD;
            if (null != queryTerm.getInteger("sim") && queryTerm.getIntValue("sim") > SIMTHRESHOLD) {
                sim = queryTerm.getIntValue("sim");
            }
        }

        FeatureCompUtil fc = new FeatureCompUtil();
        fc.setFeaturePoints(PhoenixService.getPoints());
        float simThresholdFloat = fc.reversalNormalize(sim / 100.00F);

        // 多值查询条件
        JSONObject queryMulti = null;
        if (searchData.containsKey("query_multi")) {
            queryMulti = searchData.getJSONObject("query_multi");
        }

        JSONObject queryRange = null;
        if (searchData.containsKey("query_range")) {
            queryRange = searchData.getJSONObject("query_range");
        }

        sql.append("select count(1) from ").append(tableName).append(" WHERE");

        boolean isAddAnd = false;
        // 单值条件
        if (queryTerm != null) {
            boolean isDeleteAnd = false;
            for (Object key : queryTerm.keySet()) {
                if (!key.toString().equals("sim") && !key.toString().equals("feature")) {
                    sql.append(" ").append(key.toString() + " = ? AND");
                    isAddAnd = true;
                    isDeleteAnd = true;
                }
            }
            if (isDeleteAnd) {
                int index = sql.lastIndexOf("AND");
                if (index != -1) {
                    sql.delete(index, sql.length());
                }
            }
        }

        // 多值条件
        if (queryMulti != null) {
            if (isAddAnd && queryMulti.size() != 0) {
                sql.append(" AND ");
            }
            boolean isDeleteAnd = false;
            for (Object key : queryMulti.keySet()) {
                Object value = queryMulti.get(key);
                sql.append(" ").append(key).append(" IN (");
                if (value instanceof ArrayList) {
                    for (Object ele : (ArrayList) value) {
                        sql.append("?,");
                    }
                    int index = sql.lastIndexOf(",");
                    if (index != -1) {
                        sql.delete(index, sql.length());
                    }
                    sql.append(")");
                }
                sql.append(" AND ");
                isAddAnd = true;
                isDeleteAnd = true;
            }
            if (isDeleteAnd) {
                int index = sql.lastIndexOf("AND");
                if (index != -1) {
                    sql.delete(index, sql.length());
                }
            }
        }

        if (queryRange.size() != 0) {
            if (isAddAnd) {
                sql.append(" AND ");
            }
            sql.append(" op_time BETWEEN '").append(queryRange.getString("start_time")).append("' AND '")
                    .append(queryRange.getString("end_time")).append("'");
            isAddAnd = true;
        }

        //按图片查询
        if (!feature.equals("")) {
            if (isAddAnd) {
                sql.append(" AND ");
            }
            sql.append(" FeatureComp(rt_feature,?) >= ").append(simThresholdFloat).append(" ");
        }
        int i = 1;
        ResultSet rs = null;
        try (PreparedStatement stat = phoenixConn.prepareStatement(sql.toString())) {
            // 单值条件
            if (queryTerm != null) {
                for (Object key : queryTerm.keySet()) {
                    // 按图片查询做特殊处理
                    if (!key.toString().equals("sim") && !key.toString().equals("feature")) {
                        stat.setObject(i, queryTerm.get(key));
                        i++;
                    }
                }
            }

            // 多值条件
            if (queryMulti != null) {
                for (Object key : queryMulti.keySet()) {
                    Object value = queryMulti.get(key);
                    if (value instanceof ArrayList) {
                        for (Object ele : (ArrayList) value) {
                            stat.setObject(i, ele);
                            i++;
                        }
                    }
                }
            }

            // 按图片查询
            if (!feature.equals("")) {
                stat.setObject(i, Base64.getDecoder().decode(feature));
            }

            rs = stat.executeQuery();
            while (rs.next()) {
                int count = rs.getInt(1);
                result.put("count", count);
                result.put("total_page", count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
            }
        } catch (Exception e) {
            log.error("alarmSearchClient getCountAndPage error:", e);

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.error("alarmSearchClient getCountAndPage release resource error:", e);
                }
            }
        }

        return result;
    }

    /**
     * 获取告警图片
     *
     * @param queryData
     * @param phoenixConn
     * @return
     */
    public JSONObject getAlarmPicture(JSONObject queryData, Connection phoenixConn) {
        JSONObject result = new JSONObject();
        if (queryData.containsKey(ALARMTYPE) && null != queryData.getInteger(ALARMTYPE)
                && queryData.containsKey(LIBID) && null != queryData.getInteger(LIBID) && queryData.containsKey(OPTIME)
                && !queryData.getString(OPTIME).isEmpty() && queryData.containsKey(PERSONID)
                && !queryData.getString(PERSONID).isEmpty()) {
            int alarmType = queryData.getInteger(ALARMTYPE);
            int libId = queryData.getInteger(LIBID);
            String opTime = queryData.getString(OPTIME);
            String personId = queryData.getString(PERSONID);
            String sql = String.format(" select rt_image_data3 from %s where ", tableName);
            sql = sql + String.format(" %s = %d and %s = %d", ALARMTYPE, alarmType, LIBID, libId);
            sql = sql + String.format(" and %s = '%s' and %s = '%s'", OPTIME, opTime, PERSONID, personId);
            ResultSet rs = null;
            try (PreparedStatement stat = phoenixConn.prepareStatement(sql)) {
                rs = stat.executeQuery();
                while (rs.next()) {
                    ResultSetMetaData rsMetaData = rs.getMetaData();
                    int columnCount = rsMetaData.getColumnCount();
                    for (int column = 0; column < columnCount; column++) {
                        String field = rsMetaData.getColumnLabel(column + 1);
                        result.put(field.toLowerCase(), rs.getObject(field));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("访问HBase数据库失败:", e);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                        log.error(" alarmSearchClient getAlarmPicture release resource error:", e);
                    }
                }
            }
        } else {
            throw new RuntimeException("参数无效");
        }
        return result;

    }

    public JSONObject getAlarmExportData(JSONObject searchData, Connection phoenixConn) throws Exception {
        if (phoenixConn == null) {
            throw new Exception("phoenixConn is null");
        }

        JSONObject result = new JSONObject();
        List<JSONObject> objList = new ArrayList<>();

        long t1 = System.currentTimeMillis();
        int pageSize = EXPORTSIZE;

        StringBuilder sql = new StringBuilder();

        JSONObject queryTerm = null;
        if (searchData.containsKey("query_term")) {
            queryTerm = searchData.getJSONObject("query_term");
        }

        // 按图片查询需要用到udf，做特殊处理
        int sim = -1;
        String feature = "";
        boolean isSearchFeature = false;
        if (queryTerm != null && queryTerm.containsKey("feature") && !queryTerm.getString("feature").isEmpty()) {
            isSearchFeature = true;
            feature = queryTerm.getString("feature");
            queryTerm.remove("feature");

            // 添加对相似度阈值的判断
            sim = SIMTHRESHOLD;
            if (null != queryTerm.getInteger("sim") && queryTerm.getIntValue("sim") > SIMTHRESHOLD) {
                sim = queryTerm.getIntValue("sim");
            }

            queryTerm.remove("sim");
        }

        FeatureCompUtil fc = new FeatureCompUtil();
        fc.setFeaturePoints(PhoenixService.getPoints());
        float simThresholdFloat = fc.reversalNormalize(sim / 100.00F);

        // 多值查询条件
        JSONObject queryMulti = null;
        if (searchData.containsKey("query_multi")) {
            queryMulti = searchData.getJSONObject("query_multi");
        }

        // 范围条件
        JSONObject queryRange = null;
        String orderType = "";
        String orderField = "";
        if (searchData.containsKey("query_range")) {
            queryRange = searchData.getJSONObject("query_range");
            orderField = queryRange.getString("order_field");
            orderType = queryRange.getString("order_type");
        }

        // 优化方案
        sql.append("select ");
        // 只返回告警表中需要的字段
        for (String key : exportSchema.keySet()) {
            // 去掉key中的列族信息
            if (key.contains(".")) {
                key = key.split("\\.")[1];
            }
            sql.append(key)./* append(" as ").append(key). */append(",");
        }

        if (isSearchFeature) {
            // 查询结果添加查询图片与实时图片间的相似度
            sql.append("FeatureComp(rt_feature,?) as sim");
        } else {
            // 去掉多余的逗号
            int idx1 = sql.lastIndexOf(",");
            if (idx1 != -1) {
                sql.delete(idx1, sql.length());
            }
        }

        sql.append(" from ").append(tableName).append(" WHERE");

        boolean isAddAnd = false;
        // 单值条件
        if (queryTerm != null) {
            boolean isDeleteAnd = false;
            for (Object key : queryTerm.keySet()) {
                sql.append(" ").append(key.toString() + " = ? AND ");
                isAddAnd = true;
                isDeleteAnd = true;
            }
            if (isDeleteAnd) {
                int index = sql.lastIndexOf("AND");
                if (index != -1) {
                    sql.delete(index, sql.length());
                }
            }
        }

        // 多值条件
        if (queryMulti != null) {
            if (isAddAnd && queryMulti.size() != 0) {
                sql.append(" AND ");
            }
            boolean isDeleteAnd = false;
            for (Object key : queryMulti.keySet()) {
                Object value = queryMulti.get(key);
                sql.append(" ").append(key).append(" IN (");
                if (value instanceof ArrayList) {
                    for (Object ele : (ArrayList) value) {
                        sql.append("?,");
                    }
                    int index = sql.lastIndexOf(",");
                    if (index != -1) {
                        sql.delete(index, sql.length());
                    }
                    sql.append(")");
                }
                sql.append(" AND ");

                isAddAnd = true;
                isDeleteAnd = true;
            }
            if (isDeleteAnd) {
                int index = sql.lastIndexOf("AND");
                if (index != -1) {
                    sql.delete(index, sql.length());
                }
            }
        }
        if (queryRange.size() != 0) {
            if (isAddAnd) {
                sql.append(" AND ");
            }
            sql.append(" op_time BETWEEN '").append(queryRange.getString("start_time")).append("' AND '")
                    .append(queryRange.getString("end_time")).append("'");
            isAddAnd = true;
        }

        // 按图片查询,按照比对相似度排序，默认相似度倒排序
        if (isSearchFeature) {
            if (isAddAnd) {
                sql.append(" AND ");
            }
            sql.append(" FeatureComp(rt_feature,?) >= ").append(simThresholdFloat).append(" "); // [lq-modify-2018-05-21]

            // 支持按sim 和 告警时间排序
            if ("0".equals(orderField)) {
                sql.append(" order by FeatureComp(rt_feature,?) ");
            } else {
                sql.append(" order by op_time ");
            }

        } else if ("0".equals(orderField)) {
            // 不按图片查询，按照时间排序
            sql.append(" order by similarity ");
        } else {
            sql.append(" order by op_time ");
        }
        if ("0".equals(orderType)) {
            sql.append(" desc ");
        } else {
            sql.append(" asc ");
        }

        sql.append(" limit ").append(pageSize);
        int i = 1;
        ResultSet rs = null;
        try (PreparedStatement stat = phoenixConn.prepareStatement(sql.toString())) {
            // 按图片查询（返回字段）
            if (isSearchFeature) {
                byte[] byteFeature = Base64.getDecoder().decode(feature);
                stat.setObject(i, byteFeature);
                i++;
            }
            // 单值条件
            if (queryTerm != null) {
                for (Object key : queryTerm.keySet()) {
                    stat.setObject(i, queryTerm.get(key));
                    i++;
                }
            }

            // 多值条件
            if (queryMulti != null) {
                for (Object key : queryMulti.keySet()) {
                    Object value = queryMulti.get(key);
                    if (value instanceof ArrayList) {
                        for (Object ele : (ArrayList) value) {
                            stat.setObject(i, ele);
                            i++;
                        }
                    }
                }
            }

            // 按图片查询（查询条件）
            if (isSearchFeature /* !feature.equals("") */) {
                byte[] byteFeature = Base64.getDecoder().decode(feature);
                stat.setObject(i, byteFeature);
                i++;

                if ("0".equals(orderField)) {
                    stat.setObject(i, byteFeature);
                    i++;
                }
            }

            rs = stat.executeQuery();

            while (rs.next()) {
                JSONObject record = new JSONObject();
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData.getColumnCount();
                for (int column = 0; column < columnCount; column++) {
                    String field = rsMetaData.getColumnLabel(column + 1).toLowerCase();
                    record.put(field, rs.getObject(field));
                }

                if (record.containsKey("sim") && null != record.get("sim")) {
                    float simFlloat = fc.Normalize(Float.parseFloat(record.get("sim").toString()));
                    record.put("sim", simFlloat);
                }
                objList.add(record);
            }
        } catch (Exception e) {
            log.error("访问hbase数据库失败:", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.error("close exception:", e);
                }
            }
        }

        result.put("data", objList);
        result.put("time", System.currentTimeMillis() - t1);
        result.put("id", CommonConstant.PhoenixProtocolId.QUERY_ALARM_EXPORT_DATA);
        return result;
    }

    private static HashMap<String, String> exportSchema = new HashMap();

    static {
        exportSchema.put("alarm_type", "unsigned_int not null");
        exportSchema.put("op_time", "VARCHAR not null");
        exportSchema.put("lib_id", "unsigned_int not null");
        exportSchema.put("person_id", "VARCHAR not null");
        // exportSchema.put("attr.enter_time", "VARCHAR");
        exportSchema.put("attr.person_name", "VARCHAR");
        exportSchema.put("attr.camera_id", "VARCHAR");
        exportSchema.put("attr.camera_name", "VARCHAR");
        exportSchema.put("attr.similarity", "unsigned_float");
        exportSchema.put("attr.control_event_id", "VARCHAR");
        exportSchema.put("attr.alarm_duration", "unsigned_long"); // 老人模块需要
        exportSchema.put("attr.big_picture_uuid", "VARCHAR"); // 大图UUID
        exportSchema.put("attr.img_url", "VARCHAR"); // 小图UUID
    }

    // 表结构 注释掉部分查询不需要返回的字段
    private static HashMap<String, String> schema = new HashMap();

    static {
        schema.put("alarm_type", "unsigned_int not null");
        schema.put("op_time", "VARCHAR not null");
        schema.put("lib_id", "unsigned_int not null");
        schema.put("person_id", "VARCHAR not null");
        schema.put("attr.enter_time", "VARCHAR");
        schema.put("attr.person_name", "VARCHAR");
        schema.put("attr.camera_id", "VARCHAR");
        schema.put("attr.camera_name", "VARCHAR");
        schema.put("attr.similarity", "unsigned_float");
        schema.put("attr.control_event_id", "VARCHAR");
        schema.put("attr.alarm_duration", "unsigned_long"); // 老人模块需要
        schema.put("attr.big_picture_uuid", "VARCHAR"); // 大图UUID
        schema.put("attr.img_url", "VARCHAR"); // 小图UUID
        // schema.put("pics.rt_image_data","VARBINARY");
    }

    /**
     * 获取表结构
     */
    public HashMap<String, String> getAlarmSchema() {
        return schema;
    }
}
