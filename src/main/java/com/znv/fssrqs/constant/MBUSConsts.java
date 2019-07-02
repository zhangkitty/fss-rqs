package com.znv.fssrqs.constant;

public interface MBUSConsts {
    /**
     * 人脸告警数据
     */
    public static final int FACE_DATA_ALARM = 2;
    
    /**
     * 人脸非告警数据
     */
    public static final int FACE_DATA_NO_ALARM = 1;
    
    /**
     * 告警抓拍
     */
    public static final int FACE_DATA_CAPTURE = 3;
    
    public static final String OK = "ok";
    
    public static final int IS_REPORT_BIG_PIC = 1;
    
    /**
     * 订阅参数名
     * @author 0049002743
     */
    public static class SubscribeParams {
        /**
         * 页面名称
         */
        public static final String FACE_PAGE_NAME = "page_name";
        public static final String FACE_EVENT_TYPE = "event_type";
        /**
         * 页面订阅类型
         */
        public static final String FACE_PAGE_SUBSCRIBE_TYPE = "subscribe_type";
        /**
         * 页面订阅值
         */
        public static final String FACE_PAGE_SUBSCRIBE_VALUE = "subscribe_value";
    }   
    
}
