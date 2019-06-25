package com.znv.fssrqs.enums;

/**
 * Created by dongzelong on  2019/6/25 14:51.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public enum ImageStoreType {
    HBASE(0, "Hbase数据库"),
    FDFS(1, "FastDfs文件系统"),
    HBASE_FDFS(2, "大图fastdfs，小图hbase"),
    DH_CLOUD(3, "大华云");
    private int code;
    private String message;

    ImageStoreType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
