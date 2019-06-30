package com.znv.fssrqs.constant;

/**
 * Created by dongzelong on  2019/6/18 10:12.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class CommonConstant {
    public final static String PACKAGE_PATH_NAME = "com.znv.fssrqs" ;

    public static class HkUri {
        public static final String ARTEMIS_PROTOCAL = "https://";
        public static final String ARTEMIS_PATH = "/artemis";
        public static final String QUERY_PERSON = "/api/fms/v2/human/findStaticHuman";
        public static final String ADD_PERSON = "/api/fms/v2/staticlist/addRecord";
        public static final String DEL_PERSON = "/api/fms/v2/staticlist/deleteRecord";
        public static final String MODITY_PERSON = "/api/fms/v2/staticlist/modifyRecord";
        public static final String QUERY_LIB = "/api/fms/v2/listLib/findListLib";
        public static final String ADD_LIB = "/api/fms/v2/listLib/addListLib";
        public static final String DEL_LIB = "/api/fms/v2/listLib/deleteListLib";
    }

    public static class HkSdkErrorCode {
        public static final int SUCCESS = 0;
        public static final int ERROR = -1;
    }

    public static class PhoenixProperties {
        public static final String PERSON_LIST_TABLE_NAME = "fss.phoenix.table.blacklist.name";
        public static final String HISTORY_DATA_TABLE_NAME = "fss.phoenix.table.history.name";
        //大图表名
        public static final String BIGPICTURE_TABLE_NAME = "fss.phoenix.table.bigpic.name";
        public static final String LIB_CONFIG_TABLE_NAME = "fss.phoenix.table.libconfig.name";
        public static final String CAMERA_LIB_TABLE_NAME = "fss.phoenix.table.cameralib.name";
        public static final String RELATIONSHIP_TABLE_NAME = "fss.phoenix.table.relationship.name";
        public static final String ALARM_TABLE_NAME = "fss.phoenix.table.alarm.name";
        public static final String PHOENIX_DRIVER = "pheonix.driver";
        //单个摄像头最大字库匹配量
        public static final String CAMERA_MAX_NUM = "fss.sdk.phoenix.camera.max.size";
        // 初始化连接池数量
        public static final String CONNECTION_INITIAL_SIZE = "pheonix.initialSize";
        // 连接池最大数量
        public static final String CONNECTION_MAX_SIZE = "pheonix.maxActive";
        public static final String PERSON_LIST_MAX_NUM = "fss.sdk.phoenix.personlist.max.num";
    }

    public static class PhoenixProtocolId {
        public static final String QUERY_PERSON_LIST = "31001";
        public static final String QUERY_STATIC_PERSON_LIST = "31002";
        public static final String QUERY_CAMERA_LIB = "31003";
        public static final String QUERY_REALTIONSHIP = "31004";
        public static final String QUERY_HISTORY = "31005";
        public static final String QUERY_ALARM = "31006";
        public static final String QUERY_LIB_CONFIG = "31007";
        public static final String QUERY_HISTORY_BY_INDEX = "12001";
        public static final String QUERY_PERSON_CARD_PICTURE = "31008";
        public static final String QUERY_ALARM_EXPORT_DATA = "31009";
        public static final String QUERY_HISTORY_SUPER_SEARCH_PICTURE = "31010";
        public static final String BATCH_MODIFY_PERSON_LIST_FLAG = "31011";

        public static final String SDK_CONNECTION_INITIAL_SIZE = "fss.sdk.pheonix.initialSize";
        public static final String SDK_CONNECTION_MAX_SIZE = "fss.sdk.pheonix.maxActive";
    }

    public static class NotifyKafka {
        public static final String NOTIFY_TOPIC_MSGTYPE = "fss.kafka.topic.blacklistchange.msgtype";
        public static final String ZOOKEEPER_ADDR = "zookeeper.connect";
        public static final String NOTIFY_PARTITION_NUM = "fss.kafka.topic.blacklistchange.partition.num";
        public static final String NOTIFY_REPLICATION_NUM = "fss.kafka.topic.blacklistchange.replication.num";
    }

    /**
     * 商汤服务器配置
     */
    public static class SenseTime {
        //商汤比对算法归一下数组
        public static final String SENSETIME_FEATURE_SRC = "sensetime.feature.srcPoints";
        public static final String SENSETIME_FEATURE_DST = "sensetime.feature.dstPoints";
    }

    /**
     * Es相关配置
     */
    public static class ElasticSearch {
        //多索引分类搜索添加
        public static final String ES_CLUSTER_NAME = "es.cluster.name";
        public static final String INDEX_EXACT_SEARCH_RESULT = "fss.es.index.exact.search.result";
        public static final String ES_SERVER_IP = "es.server.ip";
        public static final String ES_HTTP_PORT = "es.http.port";
        public static final String ES_INDEX_HISTORY_NAME = "fss.es.search.history.alias";
        public static final String ES_INDEX_HISTORY_TYPE = "fss.es.index.history.type";
        public static final String ES_INDEX_HISTORY_PREFIX = "fss.es.index.history.prefix";
        public static final String ES_SEARCH_TEMPLATE_FACESEARCH_ID = "fss.es.search.template.facesearch.id";
        public static final String ES_SEARCH_TEMPLATE_FLOWCOUNT_ID = "fss.es.search.template.flowCount.id";
        public static final String ES_SEARCH_TEMPLATE_FASTFEATURE_ID = "fss.es.search.template.fastsearch.id";
        public static final String ES_SEARCH_TEMPLATE_PERSONLIST_COUNT_ID = "fss.es.search.template.personlist.count.id";
        public static final String ES_SEARCH_TEMPLATE_HISTORY_PERSON_COUNT_ID = "fss.es.search.template.historyperson.count.id";
        public static final String ES_SEARCH_TEMPLATE_ALARM_PERSON_COUNT_ID = "fss.es.search.template.alarmperson.count.id";
        public static final String INDEX_PERSON_LIST_NAME = "fss.es.index.person.list.name";
        public static final String INDEX_PERSON_LIST_TYPE = "fss.es.index.person.list.type";
        public static final String ES_SEARCH_TEMPLATE_PERSONLIST_ID = "fss.es.search.template.personlist.id";
        public static final String ES_SEARCH_POOL_NUM = "fss.es.search.pool.num";
        public static final String INDEX_ALARM_NAME = "fss.es.index.alarm.name";
        public static final String INDEX_ALARM_TYPE = "fss.es.index.alarm.type";
        public static final String ES_SEARCH_TEMPLATE_ALARM_SEARCH_ID = "fss.es.search.template.alarmsearch.id";
        public static final String ES_SEARCH_TEMPLATE_PERSONLIST_GROUP_ID = "fss.es.search.template.personlist.group.id";
        public static final String INDEX_LOG_NAME = "fss.es.index.log.name";
        public static final String INDEX_LOG_TYPE = "fss.es.index.log.type";
    }

    public static class FdfsConfig {
        public static final String SMALL_PIC_URL = "GetSmallPic";
        public static final String BIG_PIC_URL = "GetBigBgPic";
    }

    public static class ChongQingLib {
        public static final String RESIDENT = "38";
        public static final String SECOND_GENERATION_ID_CARD = "18";
        public static final String RUN_CRIMINAL = "20";
        public static final String RUN_CRIMINAL_CHONGQING = "22";
        public static final String BASE_TERRORIST = "25";
        public static final String MAJOR_TERRORIST = "29";
    }
}
