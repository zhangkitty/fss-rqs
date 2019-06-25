package com.znv.fssrqs.enums;

public enum StoreImgType {

    HBASE(0, "Hbase数据库"), FDFS(1, "FastDfs文件系统"), BOTH(2, "大图fastdfs，小图hbase"), DH_CLOUD(3, "大华云");

    private int value;
    private String desc;

    private StoreImgType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
