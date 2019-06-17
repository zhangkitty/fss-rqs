package com.znv.fssrqs.util;

import com.alibaba.fastjson.serializer.ValueFilter;

public class WriteNullListAsEmptyFilter implements ValueFilter {
    @Override
    public Object process(Object obj, String s, Object v) {
        if (v == null)
            return "";
        return v;
    }
}
