package com.znv.fssrqs.kafka.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericContainer;

public enum ZnvSchema {
    Null("null"),
    Bytes("bytes"),
    CharSequence("string"),
    Boolean("boolean"),
    Integer("int"),
    Long("long"),
    Float("float"),
    Double("double"),
    Map((String) null);

    private Schema schema;

    private ZnvSchema(String type) {
        if (type == null) {
            this.schema = (new Parser()).parse("{\"type\":\"map\", \"values\": [\"null\", \"int\", \"long\", \"float\", \"double\", \"string\", \"boolean\", \"bytes\"]}");
        } else {
            this.schema = (new Parser()).parse(String.format("{\"type\":\"%s\"}", type));
        }

    }

    public Schema schema() {
        return this.schema;
    }

    public static Schema getSchema(Object object) {
        if (object == null) {
            return Null.schema;
        } else {
            String clsName = object.getClass().getSimpleName();
            ZnvSchema[] var2 = values();
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                ZnvSchema schema = var2[var4];
                if (clsName == schema.name()) {
                    return schema.schema;
                }
            }

            if (object instanceof byte[]) {
                return Bytes.schema;
            } else if (object instanceof CharSequence) {
                return CharSequence.schema;
            } else if (object instanceof GenericContainer) {
                return ((GenericContainer) object).getSchema();
            } else if (object instanceof JSONObject) {
                return Map.schema;
            } else {
                String errMsg = String.format("Unsupported type: %s, Supported type are: %s", object.getClass().getName(), JSON.toJSONString(values()));
                throw new IllegalArgumentException(errMsg);
            }
        }
    }
}