package com.znv.fssrqs.common;

public class Consts {

    public final static String PACKAGE_PATH_NAME = "com.znv.fssrqs" ;

    // 商汤比对算法归一下数组
    // 人脸特征值
    public static final float[] SENSETIME_FEATURE_SRC = {-1.0f,0.4f, 0.42f, 0.44f, 0.48f, 0.53f, 0.58f, 1.0f};
    public static final float[] SENSETIME_FEATURE_DST =  {0.0f, 0.4f, 0.5f, 0.6f, 0.7f, 0.85f, 0.95f, 1.0f};

    public static class HKURI {
        public static final String ARTEMIS_PROTOCAL = "https://";
        public static final String ARTEMIS_PATH = "/artemis";
        public static final String QUERY_PERSON = "/api/fms/v2/human/findStaticHuman";
        public static final String ADD_PERSON = "/api/fms/v2/staticlist/addRecord";
        public static final String DEL_PERSON = "/api/fms/v2/staticlist/deleteRecord";
        public static final String MODITY_PERSON = "/api/fms/v2/staticlist/modifyRecord";
    }

    public static class HkSdkErrCode {
        public static final int SUCCESS = 0;
        public static final int ERROR = -1;
    }

    public static class FinalKeyCode {
        public static final String SERVICE_ADDR = "service_addr";
        public static final String PRIVATE_SERVICE_ADDR = "private_service_addr";
        public static final String HTTP_PORT = "http_port";
        public static final String LOGIN_STATE = "login_state";
        public static final String CREATE_TASK_FAILED = "create task failed";
        public static final String DELETE_TASK_FAILED = "deletetask failed";
        public static final String TASK_ID = "taskID";
        public static final String TASK_ID_KEY = "task_id";
        public static final String REAL_TASK_ID = "real_task_id";
        public static final String IMAGE_DATA = "imageData";
        public static final String ERR_CODE = "errCode";
        public static final String ERR_REASON = "errReason";
        public static final String SUCCESS = "success";
        public static final String RESULT = "result";
        public static final String FEATURE = "feature";
        public static final String TOTAL_NUM = "totalNum";
        public static final String USED_NUM = "usedNum";
        public static final String CANUSED_NUM = "canusedNum";
        public static final String REQUEST = "request";
        public static final String REPORT_SERVICE = "reportService";
        public static final String SESSIONID = "sessionId";
        public static final String SESSION_ID = "session_id";
        public static final String SUCCESS_KEY = "SUCCESS";
        public static final String COUNT = "count";
        public static final String IMG_URL = "imgUrl";
        public static final String GET_SMALL_PIC = "GetSmallPic";
        public static final String RELATIONSHIP_DATA = "relationshipData";
        public static final String PARAMS = "params";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String TOTAL_COUNT = "total_count";
        public static final String CAMERA_ID = "camera_id";
        public static final String CAMERA_NAME = "camera_name";
        public static final String CAMERA_TYPE = "camera_type";
        public static final String BUCKETS = "buckets";
        public static final String DOC_COUNT = "doc_count";
        public static final String GROUP_BY_TIME = "group_by_time";
        public static final String KEY_AS_STRING = "key_as_string";
        public static final String GROUP_BY_AGE = "group_by_age";
        public static final String GROUP_BY_GENDER = "group_by_gender";
        public static final String IMG_URL_KEY = "img_url";
        public static final String RT_IMAGE_DATA = "rt_image_data";
        public static final String PERSON_IMG = "person_img";
        public static final String QUERY_MULTI = "query_multi";
        public static final String QUERY_TERM = "query_term";
        public static final String QUERY_RANGE = "query_range";
        public static final String PERSON_ID = "person_id";
        public static final String LIB_ID = "lib_id";
        public static final String LIB_NAME = "lib_name";
        public static final String FORMAT_KEY = "%s&%s";
        public static final String FORMAT_KEY_3 = "%s&%s&%s";
        public static final String TABLE_NAME = "table_name";
        public static final String TABLE_VALUE = "fss.phoenix.table.cameralib.name";
        public static final String PERSONLIB_TYPE = "personlib_type";
        public static final String PAGE_SIZE = "page_size";
        public static final String TOTAL_PAGE = "total_page";
        public static final String FCPID = "fcpid";
        public static final String ORDER_TYPE = "order_type";
        public static final String ORDER_FIELD = "order_field";
        public static final String PERSON_LIB_TYPE = "personlibtype";
        public static final String IMAGE_NAME = "imagename";
        public static final String TABLE_PERSONLIB_KEY = "fss.phoenix.table.blacklist.name";
        public static final String IMAGE_DATA_LOWER = "imagedata";
        public static final String PERSON_NAME = "person_name";
        public static final String PERSONNAME = "personname";
        public static final String BIRTH = "birth";
        public static final String NATION = "nation";
        public static final String COUNTRY = "country";
        public static final String POSITIVE_URL = "positive_url";
        public static final String IDCARD_IMAGE_DATA = "idcardimagedata";
        public static final String NATURE_RESIDENCE = "nature_residence";
        public static final String ROOM_NUMBER = "room_number";
        public static final String NOWADDR = "nowaddr";
        public static final String DOOR_OPEN = "door_open";
        public static final String IMAGE_NAME_KEY = "image_name";
        public static final String CARD_ID = "card_id";
        //证件类型
        public static final String CARD_TYPE = "card_type";
        public static final String PERSONID = "personid";
        public static final String COMMENT = "comment";
        public static final String CONTROL_START_TIME = "control_start_time";
        public static final String STARTTIME = "starttime";
        public static final String CONTROL_END_TIME = "control_end_time";
        public static final String ENDTIME = "endtime";
        public static final String IS_DEL = "is_del";
        public static final String COMMUNITY_ID = "community_id";
        public static final String COMMUNITYID = "communityid";
        public static final String COMMUNITY_NAME = "community_name";
        public static final String COMMUNITYNAME = "communityname";
        public static final String CONTROL_COMMUNITY_ID = "control_community_id";
        public static final String DEPTID = "deptid";
        public static final String CONTROL_PERSON_ID = "control_person_id";
        public static final String CONTROL_POLICE_CATEGORY = "control_police_category";
        //布控人联系方式
        public static final String CONTROL_PERSON_TEL = "control_person_tel";
        public static final String USERID = "userid";
        public static final String CONTROL_EVENT_ID = "control_event_id";
        public static final String CONTROLLEVEL = "controllevel";
        public static final String ORIGINAL_LIB_ID = "original_lib_id";
        public static final String FEATURE_VALUE = "feature_value";
        public static final String TOTAL = "total";
        public static final String UTF8 = "UTF-8";
        public static final String EVENT = "event";
        public static final String SIMILARITY = "similarity";
        public static final String REFRESHFLAG = "refreshFlag";
        public static final String SESSION_KEEP_TIMEOUT = "facedata.session.keepalivetimeout";
        public static final String FACEDATA = "facedata";
        public static final String RETURNCODE = "returnCode";
        public static final String PERSON_LIB_NUM = "personLibNum";
        public static final String DOMAIN_CAMERA_NUM = "domainCameraNum";
        public static final String PERSON_TOTA_LNUM = "personTotalNum";
        public static final String TODAY_ADD_NUM = "todayAddNum";
        public static final String CAPTURE_STRANGER_NUM = "captureStrangerNum";
        public static final String TODAY_CAPTURE_STRANGER_NUM = "todayCaptureStrangerNum";
        public static final String CONNECTION = "Connection";
        public static final String KEEP_ALIVE = "keep-alive";

        public static final String BIG_PICTURE_UUID = "big_picture_uuid";
        public static final String IMAGE_DATA_KEY = "image_data";
        public static final String SEND_IDX = "send_idx";
        public static final String TASK_IDX = "task_idx";
        public static final String TRACK_IDX = "track_idx";
        public static final String ENTER_TIME = "enter_time";
        public static final String TIME_STAMP = "time_stamp";
        public static final String BOTTOM = "bottom";
        public static final String SOURCE_FRAME = "source_frame";
        public static final String RECOGRESULT = "recogResult";
        public static final String TASKIDX = "taskIdx";
        public static final String TRACKIDX = "trackIdx";
        public static final String IMG_URL_DATA = "img_url_data";
        public static final String FACERECT = "faceRect";
        public static final String IMGWIDTH = "imgWidth";
        public static final String IMGHEIGHT = "imgHeight";
        public static final String LEAVE_TIME = "leave_time";
        public static final String QUALITY_SCORE = "quality_score";
        public static final String ISSEND = "isSend";
        public static final String DEVICE_TYPE = "device_type";
        public static final String FSERVER_ID = "fserver_id";
        public static final String TASKID = "taskId";
        public static final String TRACKID = "trackId";

        public static final String EYEBROW_STYLE = "eyebrow_style";
        public static final String NOSE_STYLE = "nose_style";
        public static final String MUSTACHE_STYLE = "mustache_style";
        public static final String LIP_STYLE = "lip_style";
        public static final String WRINKLE_POUCH = "wrinkle_pouch";
        public static final String ACNE_STAIN = "acne_stain";
        public static final String FRECKLE_BIRTHMARK = "freckle_birthmark";
        public static final String SCAR_DIMPLE = "scar_dimple";
    }

    public static class FssSdkKeyCode {
        public static final String ERROR_CODE = "errorCode";
    }


    public static class FdfsConfig {

        public static final String SMALL_PIC_URL = "GetSmallPic";

        public static final String BIG_PIC_URL = "GetBigBgPic";
    }

}
