package com.znv.fssrqs.config;

import com.znv.fssrqs.constant.CommonConstant;
import lombok.Data;

/**
 * Created by dongzelong on  2019/6/25 10:50.
 *
 * @author dongzelong
 * @version 1.0
 * @Description ES相关配置信息
 */
@Data
public class EsBaseConfig {
    private String scheme;
    private String esIp;
    private String esClusterName;
    private String esExactSearchResult;
    private String esServerIp;
    private String esHttpPort;
    private String esIndexHistoryName;
    private String esIndexHistoryPrefix;
    private String esIndexHistoryType;
    private String humTemplateName;
    private String ariTemplateName;
    private String fastTemplateName;
    private String indexPersonListName;
    private String indexPersonListType;
    private String indexAlarmName;
    private String indexAlarmType;
    //名单列表信息
    private String personListTemplateName;
    private String personListCountTemplateName;
    private String historyPersonCountTemplateName;
    private String alarmPersonCountTemplateName;
    private String alarmSearchTemplateName;
    private String personListGroupTemplateName;
    private String templateTraceAnalysisSearch;
    private String templateHistoryCameraTakephCount;
    private String templateNightOutSearch;
    private String esIndexNightHistoryName;
    private String esIndexNightHistoryType;
    private String indexLogType;
    private String indexLogName;
    private final String LOPQ_MODEL_FILE = "/lopq/lopq_model_V1.0_D512_C36.lopq";
    private boolean isInit = false;


    private static class EsBaseConfigHolder {
        private static EsBaseConfig esBaseConfig = new EsBaseConfig();
    }

    public static EsBaseConfig getInstance() {
        return EsBaseConfigHolder.esBaseConfig;
    }

    public synchronized void init() {
        if (!isInit) {
            //ES集群名称
            esClusterName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_CLUSTER_NAME);
            //精确检索索引
            esExactSearchResult = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_EXACT_SEARCH_RESULT);
            //Es地址
            esServerIp = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SERVER_IP);
            //Es端口号
            esHttpPort = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_HTTP_PORT);
            //ES历史信息
            esIndexHistoryName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_INDEX_HISTORY_NAME);
            //历史表
            esIndexHistoryPrefix = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_INDEX_HISTORY_PREFIX);
            //es历史类型
            esIndexHistoryType = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_INDEX_HISTORY_TYPE);
            //人流量统计查询模板
            humTemplateName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_FLOWCOUNT_ID);
            //人脸搜索查询模板
            ariTemplateName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_FACESEARCH_ID);
            //极速检索模板
            fastTemplateName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_FASTFEATURE_ID);
            //人员表索引
            indexPersonListName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_PERSON_LIST_NAME);
            //人员表索引类型
            indexPersonListType = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_PERSON_LIST_TYPE);
            //告警索引
            indexAlarmName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_ALARM_NAME);
            //告警类型
            indexAlarmType = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_ALARM_TYPE);
            //人员名单查询模板
            personListTemplateName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_PERSONLIST_ID);
            //人员名单统计模板
            personListCountTemplateName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_PERSONLIST_COUNT_ID);
            //历史人员统计模板
            historyPersonCountTemplateName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_HISTORY_PERSON_COUNT_ID);
            //告警人员统计模板
            alarmPersonCountTemplateName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_ALARM_PERSON_COUNT_ID);
            //战果统计模板
            alarmSearchTemplateName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_ALARM_SEARCH_ID);
            //用户组
            personListGroupTemplateName = "template_person_list_group";
            //行为轨迹分析模板
            templateTraceAnalysisSearch = "template_trace_analysis_search";
            //历史抓拍信息
            templateHistoryCameraTakephCount = "template_history_camera_takeph_count";
            //频繁夜出
            templateNightOutSearch = "template_night_out_search";
            //人脸聚类
            esIndexNightHistoryName = "fused_src_data_nightowl*";
            esIndexNightHistoryType = "fused";
            //日志索引
            indexLogName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_LOG_NAME);
            indexLogType = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_LOG_TYPE);
            scheme = esServerIp.split(":")[0];
            esIp = esServerIp.split("//")[1];
        }
    }
}
