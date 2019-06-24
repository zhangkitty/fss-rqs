package com.znv.fssrqs.service.hbase;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.common.PhoenixSqlClient;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.kafka.ProducerBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by dongzelong on  2019/6/20 15:57.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
@Slf4j
public class CameraLibService extends PhoenixSqlClient {
    private String tableName = HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.CAMERA_LIB_TABLE_NAME);
    private int cameraMaxNum = HdfsConfigManager.getInt(CommonConstant.PhoenixProperties.CAMERA_MAX_NUM);
    private static final String CAMERAID = "camera_id";
    private static final String LIBID = "lib_id";
    private static final String CAMERANAME = "camera_name";
    private static final String LIBNAME = "lib_name";
    private static final String CONLST = "control_start_time";
    private static final String CONLET = "control_end_time";
    private static ProducerBase producer = new ProducerBase();
    private static final String ORIGINALCAMERA = "original_camera_id";

    static {
        init();
    }

    /**
     * 删除布控信息
     *
     * @param data
     * @param phoenixConn
     * @return
     */
    public void deleteCameraLib(JSONObject data, Connection phoenixConn) {
        if (data.containsKey(LIBID) && null != data.getInteger(LIBID)) {
            int libId = data.getInteger(LIBID);
            String sql = String.format(" delete from %s where %s = %d", tableName, LIBID, libId);
            try {
                super.executeSql(sql, phoenixConn);
            } catch (Exception e) {
                throw new RuntimeException("删除布控信息失败:", e);
            }
            sendToKafka(libId);
        }
    }

    /**
     * 获取所有布控信息
     *
     * @param phoenixConn
     */
    public List<JSONObject> getCameraLibAll(Connection phoenixConn) {
        String sql = String.format(" select * from %s ", tableName);
        try {
            List<JSONObject> resultObject = super.query(sql, phoenixConn);
            return resultObject;
        } catch (Exception e) {
            throw new RuntimeException("查询布控信息表失败", e);
        }
    }

    /**
     * @param data        cameraid、cameraname、libid、libname必填，CST、CET
     * @param phoenixConn
     * @return
     */
    // 首先需要判断整条信息是否发生修改，存在不做任何修改
    // 任何字段发生修改，则刷写数据
    public JSONObject insert(JSONObject data, Connection phoenixConn) {
        JSONObject result = new JSONObject();
        String insertCameraId = "";
        int insertLibId = 0;
        // 布控表与配置表分开操作
        if (data.containsKey(CAMERAID) && data.containsKey(CAMERANAME) && !data.getString(CAMERAID).isEmpty() && !data.getString(CAMERANAME).isEmpty()) {
            // 判断输入的CAMERAID,LIBID,CAMERANAME是否正确
            StringBuilder sql = new StringBuilder();
            insertCameraId = data.getString(CAMERAID);
            insertLibId = data.getIntValue(LIBID);
            sql.append("select distinct lib_id  from ").append(tableName).append(" where ").append(CAMERAID)
                    .append(" = '").append(insertCameraId).append("'");
            List<Integer> resList = new ArrayList<Integer>();
            ResultSet rs = null;
            try (PreparedStatement stat = phoenixConn.prepareStatement(sql.toString())) {
                rs = stat.executeQuery();
                while (rs.next()) {
                    resList.add(rs.getInt(LIBID));
                }
                // 单个摄像头下最多配5个子库
                // 当布控表中的已经配置了5个子库时需要判断新增加的子库是否已经存在
                // 如果存在则修改，不存在则提示alarm
                if (resList.size() > cameraMaxNum) {
                    throw new RuntimeException("摄像机布控库含有太多摄像机:" + tableName);
                } else if (resList.size() == cameraMaxNum && !resList.contains(insertLibId)) {
                    throw new RuntimeException("摄像机布控库含有太多摄像机:" + tableName);
                } else {
                    super.insert(organizeCameraInsertData(data), tableName, phoenixConn);
                }
            } catch (SQLException e) {
                throw new RuntimeException("", e);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                        log.error("error occur when insert data release resource ", e);
                    }
                }
            }
        }

        sendToKafka(insertCameraId, insertLibId);
        return result;
    }

    public JSONObject upsert(JSONObject data, Connection phoenixConn) {
        JSONObject result;
        JSONObject deleteData = data.getJSONObject("data");
        result = insert(deleteData, phoenixConn);
        if (data.containsKey(ORIGINALCAMERA) && !data.getString(ORIGINALCAMERA).isEmpty()) {
            String oldCameraId = data.getString(ORIGINALCAMERA);
            String newCameraId = deleteData.getString(CAMERAID);
            if (!newCameraId.equals(oldCameraId)) {
                // 修改了camera_id，则先删除原camera_id对应的记录
                deleteData.put(CAMERAID, oldCameraId);
                delete(deleteData, phoenixConn);
            }
        }
        return result;
    }

    /**
     * @param data        单条删除，data中字段：cameraid,libid,必填
     * @param phoenixConn
     * @return
     */
    public void delete(JSONObject data, Connection phoenixConn) {
        String sql = "";
        String deleteCameraId = "";
        int deleteLibId = 0;
        if (data.containsKey(CAMERAID) && data.containsKey(LIBID) && !data.getString(CAMERAID).isEmpty()
                && null != data.getInteger(LIBID)) {
            // 判断输入的主键是否正确
            deleteCameraId = data.getString(CAMERAID);
            deleteLibId = data.getIntValue(LIBID);
            sql = String.format("delete from %s where ( %s = '%s' ) and ( %s = %d )", tableName, CAMERAID, deleteCameraId, LIBID, deleteLibId);
            try {
                super.executeSql(sql, phoenixConn);
            } catch (Exception e) {
                throw new RuntimeException("删除数据失败:", e);
            }
            sendToKafka(deleteCameraId, deleteLibId);
        } else {
            throw new RuntimeException("无效参数");
        }
    }

    /**
     * @param query       查询条件中不能有libname
     * @param phoenixConn
     * @return
     */
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
                    // 查询结果为空
                    result.put("data", resList);
                    result.put("time", System.currentTimeMillis() - startTime);
                    result.put("count", count);
                    result.put("total_page", totalPage);
                    result.put("id", CommonConstant.PhoenixProtocolId.QUERY_CAMERA_LIB);
                } else {
                    // 查询结果中有值
                    result = organizeQuerySql(query, phoenixConn, 0);
                    result.put("time", System.currentTimeMillis() - startTime);
                    result.put("count", count);
                    result.put("total_page", totalPage);
                    result.put("id", CommonConstant.PhoenixProtocolId.QUERY_CAMERA_LIB);
                }
            } catch (Exception e) {
                throw new RuntimeException("访问数据库失败:" + tableName, e);
            }
        } else {
            throw new RuntimeException("无效参数");
        }
        return result;
    }

    /**
     * @param query
     * @param phoenixConn
     * @param flag
     * @return
     */
    /*
     * 组查询条件，并查询配置表拼完整的查询结果记录
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
        // like查询条件，字符串模糊匹配
        JSONObject queryLike = null;
        if (query.containsKey("query_like")) {
            queryLike = query.getJSONObject("query_like");
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

        // like查询条件 模糊匹配
        if (queryLike != null && !queryLike.isEmpty()) {
            if (hasQueryTerm || hasMulti) {
                sql.append(" AND ");
            }
            for (String key : queryLike.keySet()) {
                sql.append(" ").append(key).append(" like '%").append(queryLike.getString(key)).append("%' AND");
            }
            sql.delete(sql.lastIndexOf("AND"), sql.length());
        }

        // 设置查询偏移量 from ,size
        if (0 == flag) {
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
            log.error("query cameralib error ", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.error("query cameralib release resource error ", e);
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
            producer.setMsgTypeParam(HdfsConfigManager.getString(CommonConstant.NotifyKafka.NOTIFY_TOPIC_MSGTYPE),
                    HdfsConfigManager.getString(CommonConstant.NotifyKafka.ZOOKEEPER_ADDR),
                    HdfsConfigManager.getInt(CommonConstant.NotifyKafka.NOTIFY_PARTITION_NUM),
                    HdfsConfigManager.getInt(CommonConstant.NotifyKafka.NOTIFY_REPLICATION_NUM));
        } catch (Exception e) {
            log.error("init kafka... error ", e);
        }
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
        long currentTime = System.currentTimeMillis();
        java.util.Date timeDate = new java.util.Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = sdf.format(timeDate);
        notifyMsg.put("send_time", timeStr);
        boolean ret = producer.sendData(notifyMsg);
        log.info("send to kafka return {}", ret);
        System.out.println("CameraLibClient-ret:" + ret + ",send_time:" + timeStr);
    }

    private void sendToKafka(String primaryId, int referenceId) {
        JSONObject notifyMsg = new JSONObject();
        notifyMsg.put("msg_type", HdfsConfigManager.getString(CommonConstant.NotifyKafka.NOTIFY_TOPIC_MSGTYPE));
        notifyMsg.put("table_name", tableName);
        notifyMsg.put("primary_id", primaryId);
        notifyMsg.put("reference_id", referenceId);
        long currentTime = System.currentTimeMillis();
        java.util.Date timeDate = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = sdf.format(timeDate);
        notifyMsg.put("send_time", timeStr);
        boolean ret = producer.sendData(notifyMsg);
        log.info("send to kafka return {}", ret);
        System.out.println("CameraLibClient-ret:" + ret + ",send_time:" + timeStr);
    }

    private JSONObject organizeCameraInsertData(JSONObject input) {
        JSONObject output = new JSONObject();
        output.put(CAMERAID, input.getString(CAMERAID));
        output.put(CAMERANAME, input.getString(CAMERANAME));
        output.put(LIBID, input.getIntValue(LIBID));
        output.put(CONLST, input.getString(CONLST));
        output.put(CONLET, input.getString(CONLET));
        return output;
    }

    private JSONObject organizeConfigInsetData(JSONObject input) {
        JSONObject output = new JSONObject();
        output.put(LIBID, input.getIntValue(LIBID));
        output.put(LIBNAME, input.getString(LIBNAME));
        return output;
    }

    // 判断数组中是否存在指定元素
    public static boolean contain(List<Integer> inputList, int targetValue) {
        boolean isContains = false;
        for (int value : inputList) {
            if (targetValue == value) {
                isContains = true;
            }
        }
        return isContains;
    }

    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}
