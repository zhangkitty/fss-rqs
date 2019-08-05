package com.znv.fssrqs.service.hbase;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HbaseConfig;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.kafka.ProducerBase;
import com.znv.fssrqs.util.PhoenixConnectionPool;
import com.znv.fssrqs.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongzelong on  2019/6/20 10:19.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
@Service
public class PhoenixService {
    @Autowired
    private PersonService personListService;
    @Autowired
    private HistoryDataService historyDataService;
    @Autowired
    private AlarmDataService alarmDataService;
    @Autowired
    private CameraLibService cameraLibService;
    @Autowired
    private LibConfigService libConfigService;
    @Autowired
    private RelationShipService relationShipService;
    private PhoenixConnectionPool connectionPool;
    private static ProducerBase producer = new ProducerBase();
    private static Map<String, float[]> points = new ConcurrentHashMap<String, float[]>(2);

    /**
     * 构造方法
     */
    public PhoenixService(@Autowired HdfsConfigManager hdfsConfigManager, @Autowired HbaseConfig hbaseConfig) throws Exception {
        connectionPool = new PhoenixConnectionPool(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.PHOENIX_DRIVER), hbaseConfig.getJdbcUrl(), "", "");
        connectionPool.createPool();
        //初始化kafka
        Properties props = HdfsConfigManager.getKafkaProducerProps();
        producer.initWithConfig(props);
        producer.setMsgTypeParam(HdfsConfigManager.getString(CommonConstant.NotifyKafka.NOTIFY_TOPIC_MSGTYPE),
                HdfsConfigManager.getString(CommonConstant.NotifyKafka.ZOOKEEPER_ADDR),
                HdfsConfigManager.getInt(CommonConstant.NotifyKafka.NOTIFY_PARTITION_NUM),
                HdfsConfigManager.getInt(CommonConstant.NotifyKafka.NOTIFY_REPLICATION_NUM));
        Properties pop = HdfsConfigManager.getProperties();
        points = PropertiesUtil.getFeaturePoints(pop);
    }

    public static Map<String, float[]> getPoints() {
        return points;
    }

    /**
     * @return 获取kafka通知producer, 通知kafka统一从这里调用
     */
    public static ProducerBase getProducer() {
        return producer;
    }

    /**
     * 获取图片数据
     *
     * @param data
     */
    public JSONObject getPicture(JSONObject data) {
        JSONObject result = new JSONObject();
        Connection conn = null;
        String id = data.getString("id");
        String tableName = data.getString("table_name");
        try {
            conn = connectionPool.getConnection();
            if (CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                //获取名单库图片
                result = personListService.getPersonPicture(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_PERSON_CARD_PICTURE.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                //获取名单库身份证图片
                result = personListService.getCardPicture(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_HISTORY.equalsIgnoreCase(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.HISTORY_DATA_TABLE_NAME))) {
                //获取历史表图片
                result = historyDataService.getHistoryPicture(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_ALARM.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.ALARM_TABLE_NAME))) {
                // 获取告警表图片
                result = alarmDataService.getAlarmPicture(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_HISTORY_BY_INDEX.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.HISTORY_DATA_TABLE_NAME))) {
                //按照index获取历史表图片
                result = historyDataService.getHistoryPictureByIndex(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_HISTORY_SUPER_SEARCH_PICTURE.equalsIgnoreCase(id)) {
                //超级检索获取图片
                result = historyDataService.getHistoryPictureForSuperSearch(data, conn);
            } else {
                throw new RuntimeException("数据库表不存在:" + tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("访问hbase数据库失败:", e);
        } finally {
            if (null != conn) {
                connectionPool.returnConnection(conn);
            }
        }
        return result;
    }

    /**
     * @param data
     * @return
     */
    public void deleteLibId(JSONObject data) {
        JSONObject result = new JSONObject();
        Connection conn = null;
        String id = data.getString("id");
        try {
            conn = connectionPool.getConnection();
            if (id.equals(CommonConstant.PhoenixProtocolId.QUERY_CAMERA_LIB)) {
                cameraLibService.deleteCameraLib(data, conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException("get connPool failed", e);
        } finally {
            if (Objects.nonNull(conn)) {
                connectionPool.returnConnection(conn);
            }
        }
    }


    public JSONObject count(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        Connection conn = null;
        String tableName = jsonObject.getString("table_name");
        String id = jsonObject.getString("id");
        JSONObject countData = jsonObject.getJSONObject("data");
        try {
            conn = connectionPool.getConnection();
            if (CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                result = personListService.getPersonListCount(countData, conn);
            } else {
                throw new RuntimeException("hbase数据表不存在:" + tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("访问hbase数据库失败:", e);
        } finally {
            if (Objects.nonNull(conn)) {
                connectionPool.returnConnection(conn);
            }
        }
        return result;
    }

    public JSONObject getAll(JSONObject data) {
        JSONObject result = new JSONObject();
        Connection conn = null;
        String tableName = data.getString("table_name");
        String id = data.getString("id");
        try {
            conn = connectionPool.getConnection();
            if (CommonConstant.PhoenixProtocolId.QUERY_CAMERA_LIB.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.CAMERA_LIB_TABLE_NAME))) {
                List<JSONObject> cameraLibs = cameraLibService.getCameraLibAll(conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_LIB_CONFIG.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.LIB_CONFIG_TABLE_NAME))) {
                result = libConfigService.getLibConfigAll(conn);
            } else {
                throw new RuntimeException("hbase数据表不存在");
            }
        } catch (SQLException e) {
            throw new RuntimeException("访问hbase数据表不存在:", e);
        } finally {
            if (null != conn) {
                connectionPool.returnConnection(conn);
            }
        }
        return result;
    }

    /**
     * @param insertData
     * @return
     */
    public JSONObject searchPersonList(JSONObject insertData) {
        JSONObject result = new JSONObject();
        Connection conn = null;
        String tableName = insertData.getString("table_name");
        String id = insertData.getString("id");
        JSONArray data = insertData.getJSONArray("data");
        try {
            conn = connectionPool.getConnection();
            if (CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                //按照lib_id + person_id 查询名单库所有信息
                result = personListService.getPersonInfo(data, conn);
            } else {
                throw new RuntimeException("hbase数据表不存在:" + tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("访问hbase数据库失败:", e);
        } finally {
            if (Objects.nonNull(conn)) {
                connectionPool.returnConnection(conn);
            }
        }
        return result;

    }

    /**
     * @param insertData 数据信息,添加数据
     * @return JSONObject
     */
    public JSONObject insert(JSONObject insertData) {
        JSONObject result = new JSONObject();
        Connection conn = null;
        String tableName = insertData.getString("table_name");
        String id = insertData.getString("id");
        JSONObject data = insertData.getJSONObject("data");
        try {
            conn = connectionPool.getConnection();
            if (CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                //单条数据写入名单库 or 批量新增
                result = personListService.insert(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_CAMERA_LIB.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.CAMERA_LIB_TABLE_NAME))) {
                //数据写入布控表
                result = cameraLibService.insert(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_REALTIONSHIP.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.RELATIONSHIP_TABLE_NAME))) {
                //数据写入人员关系表
                result = relationShipService.insert(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_ALARM.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.ALARM_TABLE_NAME))) {
                //数据写入告警表（告警确认）
                alarmDataService.insert(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_LIB_CONFIG.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.LIB_CONFIG_TABLE_NAME))) {
                //数据写入配置表
                libConfigService.insert(data, conn);
            } else {
                throw new RuntimeException("访问hbase数据表不存在:" + tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("访问hbase数据库失败:", e);
        } finally {
            if (Objects.nonNull(conn)) {
                connectionPool.returnConnection(conn);
            }
        }
        return result;
    }

    public JSONObject update(JSONObject updateData) {
        JSONObject result = new JSONObject();
        Connection conn = null;
        String tableName = updateData.getString("table_name");
        String id = updateData.getString("id");
        JSONObject data = updateData.getJSONObject("data");
        try {
            conn = connectionPool.getConnection();
            if (CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                //修改名单库
                result = personListService.update(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_CAMERA_LIB.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.CAMERA_LIB_TABLE_NAME))) {
                //修改布控表
                result = cameraLibService.upsert(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.BATCH_MODIFY_PERSON_LIST_FLAG.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                //批量修改民单库flag
                result = personListService.batchModifyFlag(data, conn);
            } else {
                result = insert(updateData);
            }
        } catch (SQLException e) {
            throw new RuntimeException("访问hbase数据库失败:", e);
        } finally {
            if (Objects.nonNull(conn)) {
                connectionPool.returnConnection(conn);
            }
        }
        return result;
    }

    /**
     * 删除数据
     */
    public void delete(JSONObject deleteData) {
        JSONObject result = new JSONObject();
        Connection conn = null;
        String tableName = deleteData.getString("table_name");
        String id = deleteData.getString("id");
        JSONObject data = deleteData.getJSONObject("data");
        try {
            conn = connectionPool.getConnection();
            if (CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                personListService.delete(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_CAMERA_LIB.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.CAMERA_LIB_TABLE_NAME))) {
                //删除布控表中数据
                cameraLibService.delete(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_REALTIONSHIP.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.RELATIONSHIP_TABLE_NAME))) {
                //删除关系表数据
                relationShipService.delete(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_LIB_CONFIG.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.LIB_CONFIG_TABLE_NAME))) {
                //删除配置表
                libConfigService.delete(data, conn);
            } else {
                throw new RuntimeException("操作hbase表不存在:" + tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("访问hbase数据库失败:", e);
        } finally {
            if (null != conn) {
                connectionPool.returnConnection(conn);
            }
        }
    }

    /**
     * 查询数据
     *
     * @param queryData 查询协议
     */
    public JSONObject query(JSONObject queryData) {
        JSONObject result = new JSONObject();
        Connection conn = null;
        String tableName = queryData.getString("table_name");
        String id = queryData.getString("id");
        JSONObject data = queryData;
        try {
            conn = connectionPool.getConnection();
            if (CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST.equalsIgnoreCase(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                //名单库结构化信息查询,需要过滤掉已经被删除的记录
                result = personListService.query(data, conn, true);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_STATIC_PERSON_LIST.equalsIgnoreCase(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME))) {
                //名单库静态图片搜索
                result = personListService.personListStaticSearch(data, conn, true);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_CAMERA_LIB.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.CAMERA_LIB_TABLE_NAME))) {
                //查询布控表
                result = cameraLibService.query(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_REALTIONSHIP.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.RELATIONSHIP_TABLE_NAME))) {
                //查询人员关系表
                result = relationShipService.query(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_HISTORY.equalsIgnoreCase(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.HISTORY_DATA_TABLE_NAME))) {
                //查询历史表
                result = historyDataService.query(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_ALARM.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.ALARM_TABLE_NAME))) {
                //查询告警表
                result = alarmDataService.query(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_LIB_CONFIG.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.LIB_CONFIG_TABLE_NAME))) {
                //查询配置表
                result = libConfigService.query(data, conn);
            } else if (CommonConstant.PhoenixProtocolId.QUERY_ALARM_EXPORT_DATA.equals(id) && tableName.equalsIgnoreCase(HdfsConfigManager.getString(CommonConstant.PhoenixProperties.ALARM_TABLE_NAME))) {
                //告警表查询结果导出
                result = alarmDataService.getAlarmExportData(data, conn);
            } else {
                throw new RuntimeException("hbase数据表不存在:" + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("", e);
        } finally {
            if (Objects.nonNull(conn)) {
                connectionPool.returnConnection(conn);
            }
        }
        return result;
    }

    /**
     * 释放资源
     */
    public void close() throws Exception {
        connectionPool.closeConnectionPool();
        producer.close();
    }
}
