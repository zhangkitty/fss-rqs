package com.znv.fssrqs.service.hbase;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.common.PhoenixSqlClient;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.kafka.ProducerBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by dongzelong on  2019/6/20 16:28.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
@Slf4j
public class LibConfigService extends PhoenixSqlClient {
    private static ProducerBase producer = new ProducerBase();
    private String tableName = HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.LIB_CONFIG_TABLE_NAME);
    private static final String LIBID = "lib_id";
    private static final String LIBNAME = "lib_name";


    static {
        init();
    }

    /**
     * @param phoenixConn
     * @return
     */
    public JSONObject getLibConfigAll(Connection phoenixConn) {
        JSONObject result = new JSONObject();
        String sql = String.format(" select * from %s ", tableName);

        try {
            List<JSONObject> list = super.query(sql, phoenixConn);
            result.put("data", list);
        } catch (Exception e) {
            throw new RuntimeException("获取静态库失败", e);
        }
        return result;
    }

    /**
     * @param data        libid必填,libname必填
     * @param phoenixConn
     */
    public void insert(JSONObject data, Connection phoenixConn) {
        try {
            super.insert(data, tableName, phoenixConn);
        } catch (Exception e) {
            throw new RuntimeException("插入数据失败:", e);
        }
    }

    /**
     * @param data
     * @param phoenixConn
     * @return
     */
    public void delete(JSONObject data, Connection phoenixConn) {
        if (data.containsKey(LIBID) && null != data.getInteger(LIBID)) {
            int libId = data.getInteger(LIBID);
            String sql = String.format("delete from %s where %s = %d", tableName, LIBID, libId);
            try {
                super.executeSql(sql, phoenixConn);
            } catch (Exception e) {
                throw new RuntimeException("删除静态库失败:", e);
            }
        }
    }

    /**
     * @param libId
     * @param phoenixConn
     * @return result类型{"errorCode":"success","data":"jsonobject"}
     */
    public JSONObject queryLibName(int libId, Connection phoenixConn) {
        JSONObject result = new JSONObject();
        StringBuilder sql = new StringBuilder();
        // 组查询语句
        sql.append("select ").append(LIBID).append(",").append(LIBNAME).append(" from ").append(tableName)
                .append(" where ").append(LIBID).append(" = ").append(libId);
        ResultSet rs = null;
        try (PreparedStatement stat = phoenixConn.prepareStatement(sql.toString())) {
            rs = stat.executeQuery();
            while (rs.next()) {
                result.put(LIBID, rs.getInt(LIBID));
                result.put(LIBNAME, rs.getString(LIBNAME));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询静态库失败:", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.error("error occur when querying data by lib_id release resource ", e);
                }
            }
        }
        return result;
    }


    public JSONObject query(JSONObject query, Connection phoenixConn) {
        JSONObject result = new JSONObject();
        if (query.containsKey("count") && query.containsKey("total_page") && query.containsKey("page_no")
                && query.containsKey("page_size") && null != query.getInteger("count")
                && null != query.getInteger("total_page") && null != query.getInteger("page_no")
                && null != query.getInteger("page_size")) {
            // 判断必传的字段是否都存在

            List<JSONObject> resList = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            // 首次查询先查询总条数和总页数，翻页查询不需要再次查询总条数和总页数
            int count = query.getInteger("count");
            int totalPage = query.getInteger("total_page");

            // 每页显示的条数
            if (query.containsKey("page_size") && null != query.getInteger("page_size")) {
                int pageSize = query.getInteger("page_size");
                // 当传入的每页行数为0时默认每页10条
                if (pageSize <= 0) {
                    pageSize = 10;
                }
                query.put("page_size", pageSize);
            }

            try {
                if (-1 == count && -1 == totalPage) {
                    // 首次查询先计算符合条件的总记录数
                    JSONObject total = organizeQuerySql(query, phoenixConn, 1);
                    count = total.getIntValue("count");
                    totalPage = total.getIntValue("total_page");
                }

                if (0 == count) {
                    result.put("data", resList);
                    result.put("time", System.currentTimeMillis() - startTime);
                    result.put("count", count);
                    result.put("total_page", totalPage);
                    result.put("id", CommonConstant.PhoenixProtocolId.QUERY_LIB_CONFIG);
                } else {
                    //查询结果中有值
                    result = organizeQuerySql(query, phoenixConn, 0);
                    result.put("time", System.currentTimeMillis() - startTime);
                    result.put("count", count);
                    result.put("total_page", totalPage);
                    result.put("id", CommonConstant.PhoenixProtocolId.QUERY_LIB_CONFIG);
                }
            } catch (Exception e) {
                throw new RuntimeException("访问hbase数据库失败:", e);
            }
        } else {
            throw new RuntimeException("参数无效");
        }
        return result;
    }

    /**
     * @param query
     * @param phoenixConn
     * @param flag
     * @return
     */
    private JSONObject organizeQuerySql(JSONObject query, Connection phoenixConn, int flag) {
        // flag，0-组装数据查询sql，1-组装数据统计sql
        JSONObject result = new JSONObject();
        List<JSONObject> resList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        int pageNo = query.getIntValue("page_no");
        int pageSize = query.getIntValue("page_size");

        // 单值查询条件
        JSONObject queryTerm = null;
        if (query.containsKey("query_term")) {
            queryTerm = query.getJSONObject("query_term");
        }
        // 多值查询条件
        JSONObject queryMulti = null;
        if (query.containsKey("query_multi")) {
            queryMulti = query.getJSONObject("query_multi");
        }

        // flag，0-组装数据查询sql，1-组装数据统计sql
        if (flag == 0) {
            sql.append("select * from ").append(tableName).append(" WHERE");
        } else {
            sql.append("select count(1) from ").append(tableName).append(" WHERE");
        }

        // 单值查询条件
        boolean hasQueryTerm = false;
        if (null != queryTerm && !queryTerm.isEmpty()) {
            hasQueryTerm = true;
            for (String key : queryTerm.keySet()) {
                sql.append(" ").append(key + " = ? AND");
            }
            sql.delete(sql.lastIndexOf("AND"), sql.length());
        }

        // 多值查询条件
        boolean hasMulti = false;
        if (null != queryMulti && !queryMulti.isEmpty()) {
            hasMulti = true;
            if (hasQueryTerm) {
                sql.append(" AND ");
            }
            for (String key : queryMulti.keySet()) {
                Object value = queryMulti.get(key);
                if (value instanceof ArrayList) { //
                    sql.append(" ").append(key).append(" IN (");
                    for (Object ele : (ArrayList) value) {
                        sql.append("?,");
                    }
                    sql.delete(sql.lastIndexOf(","), sql.length());
                    sql.append(")");
                }
                sql.append(" AND ");
            }
            sql.delete(sql.lastIndexOf("AND"), sql.length());
        }

        // 设置查询偏移量 from ,size
        if (0 == flag) {
            sql.append(" order by lib_id");
            sql.append(" limit ").append(pageSize);
            sql.append(" offset ").append((pageNo - 1) * pageSize);

        }

        int i = 1;
        ResultSet rs = null;
        try (PreparedStatement stat = phoenixConn.prepareStatement(sql.toString())) {
            // 模糊查询的内容已经拼到sql语句中了
            // 单值查询条件
            if (hasQueryTerm) {
                for (String key : queryTerm.keySet()) {
                    stat.setObject(i, queryTerm.get(key));
                    i++;
                }
            }
            // 多值查询条件
            if (hasMulti) {
                for (String key : queryMulti.keySet()) {
                    Object value = queryMulti.get(key);
                    if (value instanceof ArrayList) {
                        for (Object ele : (ArrayList) value) {
                            stat.setObject(i, ele);
                            i++;
                        }
                    }
                }
            }

            rs = stat.executeQuery();
            if (flag == 0) { // 翻页查询
                while (rs.next()) {
                    JSONObject record = new JSONObject();
                    ResultSetMetaData rsMetaData = rs.getMetaData();
                    int columnCount = rsMetaData.getColumnCount();
                    for (int column = 0; column < columnCount; column++) {
                        String field = rsMetaData.getColumnLabel(column + 1);
                        record.put(field.toLowerCase(), rs.getObject(field));
                    }
                    resList.add(record);
                }
                result.put("data", resList);
            } else { // 首次查询
                while (rs.next()) {
                    int count = rs.getInt(1);
                    result.put("count", count);
                    result.put("total_page", count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("query lib config failed", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.error("query lib config release resource error ", e);
                }
            }
        }

        return result;
    }

    /**
     * 初始化kafka
     */
    private static void init() {
        try {
            Properties props = HdfsConfigManager.getKafkaProducerProps();
            producer.initWithConfig(props);
            producer.setMsgTypeParam(
                    HdfsConfigManager.getString(CommonConstant.NotifyKafka.NOTIFY_TOPIC_MSGTYPE),
                    HdfsConfigManager.getString(CommonConstant.NotifyKafka.ZOOKEEPER_ADDR),
                    HdfsConfigManager.getInt(CommonConstant.NotifyKafka.NOTIFY_PARTITION_NUM),
                    HdfsConfigManager.getInt(CommonConstant.NotifyKafka.NOTIFY_REPLICATION_NUM));
        } catch (Exception e) {
            log.error("init kafka failed {}", e);
        }
    }

    /**
     * @return 获取表名
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 发送到kafka
     */
    private void sendToKafka(int primaryId) {
        JSONObject notifyMsg = new JSONObject();
        notifyMsg.put("msg_type", HdfsConfigManager.getString(CommonConstant.NotifyKafka.NOTIFY_TOPIC_MSGTYPE));
        notifyMsg.put("table_name", tableName);
        notifyMsg.put("primary_id", primaryId);
        notifyMsg.put("reference_id", null);
    }

    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}
