package com.znv.fssrqs.constant;

import java.util.concurrent.atomic.AtomicInteger;

public class FnmsConsts {
    public static class StatisticsModeIds {
        public static final String PERSONLIB_MODE_ID = "13005"; //名单库数据统计
        public static final String CAPTURE_MODE_ID = "13006"; //抓拍数据统计
        public static final String ALARM_MODE_ID = "13007"; //告警数据统计

        public static final String DEVICE_MODE_ID = "10001"; //资源总览
        public static final String AUTHTASK_MODE_ID = "10002"; //监控点位建设总量
        public static final String FACECOMPARE_MODE_ID = "10003"; //静态检索
        public static final String URL_IMG = "get_fss_personimage";//名单库
        public static final String URL_MAPPING = "get_fss_idcardimage";//名单库
        public static final String QUERY_ID = "13003";//亿级检索
        public static final String ERROR_CODE = "0";//亿级检索
        public static final String REPORT_ID = "13001";//属性检索
        public static final String ID = "12001";//以脸搜脸
        public static final String ALARM_LIST_EXPORT_ID = "13008";//案事件
        public static final String PEER_INFO_QUERY_ID = "12008";
        public static final String PEER_QUERY_ID = "12005";
        public static final String E_CODE = "100000";
        public static final String START_ACTION = "200";//获取流处理进度
        public static final String STOP_ACTION = "120";//流处理暂停
        public static final String F_QUERY_ID = "13011";//极速检索
        public static final String E_QUERY_ID = "13010";//精确检索
        public static final String E_MTERY_ID = "13012";//地图轨迹
        public static final String Ab_Re_Info_id = "13013";//异常关系
        public static final String Ab_Re_Analyses_id = "13015";//异常轨迹
        public static final String Night_Out_id = "12012";//频繁上网和夜出
        public static final String Ab_behavior = "13016";//异常行为
        public static final String track_analysis = "13018";//行踪分析
        public static final String FREQUENT_NIGHT_OUT = "13019";
        public static final int DAY = 0; //天
        public static final int WEEK = 1; //周
        public static final int MONTH = 2; //月
    }

    public static class ReturnCode {
        public static final int SUCCESS = 0;
        public static final int FAIL = -1;
    }

    public static class MemeryKey {
        public static final String STATISTICS = "statistics-key";
    }

    public static final AtomicInteger libTimeType = new AtomicInteger(); //子库动态告警时间类型

    public static final AtomicInteger cameraTimeType = new AtomicInteger(); //摄像头动态告警时间类型

    public static final AtomicInteger searchTimeType = new AtomicInteger(); //静态检索时间类型

    public static final AtomicInteger hisTimeType = new AtomicInteger(); //综合分析时间类型

}
