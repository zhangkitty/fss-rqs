package com.znv.fssrqs.constant;

/**
 * Created by dongzelong on  2019/6/18 10:12.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class CommonConstant {
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
}
