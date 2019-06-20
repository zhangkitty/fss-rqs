package com.znv.fssrqs.elasticsearch.index.mapper.image;

public enum HashEnum {
    BIT_SAMPLING, LSH;

    public static HashEnum getByName(String name) {
        return valueOf(name.toUpperCase());
    }
}
