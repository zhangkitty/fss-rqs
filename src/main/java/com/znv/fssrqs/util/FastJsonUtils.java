package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * fastjson工具类
 * Created by dongzelong on 2019/2/20.
 *
 * @author dongzelong
 * @version:1.3.0
 */
public class FastJsonUtils {
    private static final SerializeConfig config;

    static {
        config = new SerializeConfig();
        config.put(java.util.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
    }

    private static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
    };


    /**
     * 将javabean转化为序列化的json字符串
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return JSON.toJSONString(object, config, features);
    }

    /**
     * 将javabean转化为序列化的json字符串,不适用特征
     *
     * @param object
     * @return
     */
    public static String toJSONNoFeatures(Object object) {
        return JSON.toJSONString(object, config);
    }

    /**
     * 将json字符串转化为JSONObject
     *
     * @param text
     * @return
     */
    public static JSONObject toJSONObject(String text) {
        return toObject(text, JSONObject.class);
    }

    /**
     * 将json字符串转化为bean对象
     *
     * @param text
     * @return
     */
    public static <T> T toObject(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     * 将json字符串转化为bean数组
     *
     * @param text
     * @return
     */
    public static <T> Object[] toArray(String text, Class<T> clazz) {
        return toList(text, clazz).toArray();
    }

    /**
     * 将json字符串转化为beanList
     *
     * @param text
     * @return
     */
    public static <T> List<T> toList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
        //JSON.parseObject(array.toString(), new TypeReference<List<JSONObject>>(){});
    }

    /**
     * 将json字符串转化为beanList
     *
     * @param obj
     * @return
     */
    public static JSONArray toJsonArray(Object obj) {
        return JSONArray.parseArray(JSON.toJSONString(obj, config, features));
    }

    /**
     * json字符串转化为map
     */
    public static Map<String, Object> toMap(String text) {
        return toObject(text, Map.class);
    }

    public static Map<String, Object> toMap(Object obj) {
        return toMap(toJSONString(obj));
    }

    public static HashMap<String, Object> toHashMap(String text) {
        return toObject(text, HashMap.class);
    }

    public static HashMap<String, Object> toHashMap(Object obj) {
        return toHashMap(toJSONString(obj));
    }

    public static HashMap<String, Object> JSONObjectToSingleHashMap(JSONObject JSONObject) {
        HashMap<String, Object> map = new HashMap<>();
        if (JSONObject != null) {
            for (String key : JSONObject.keySet()) {
                map.put(key, JSONObject.get(key));
            }
        }
        return map;
    }

    public static HashMap<String, HashMap<String, Object>> JSONObjectToDoubleHashMap(JSONObject JSONObject) {
        HashMap<String, HashMap<String, Object>> map = new HashMap<>();
        if (JSONObject != null) {
            for (String key : JSONObject.keySet()) {
                map.put(key, toHashMap(JSONObject.get(key)));
            }
        }
        return map;
    }

    /**
     * 将map转化为string
     */
    public static String toString(Map map) {
        return JSONObject.toJSONString(map);
    }

    /**
     * 将map转化为JSONObject
     */
    public static JSONObject toJSONObject(Map map) {
        return JSONObject.parseObject(JSON.toJSONString(map));
    }

    public static class FastJsonBuilder {
        private final JSONObject data;

        public FastJsonBuilder() {
            this.data = new JSONObject();
        }

        public static FastJsonBuilder build() {
            return new FastJsonBuilder();
        }

        public FastJsonBuilder property(String key, Object object) {
            data.put(key, object);
            return this;
        }

        public JSONObject json() {
            return data;
        }
    }

    public static class JsonBuilder {
        private final int code;
        private final JSONObject data;
        private String message;

        private JsonBuilder(int code) {
            this.code = code;
            this.data = new JSONObject();
        }

        public JsonBuilder(int code, JSONObject data) {
            this.code = code;
            this.data = data;
        }

        public JsonBuilder(int code, String message, JSONObject data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public static JsonBuilder build(int code, JSONObject data) {
            return new JsonBuilder(code, data);
        }

        public static JsonBuilder build(int code, String message, JSONObject data) {
            return new JsonBuilder(code, message, data);
        }

        private JsonBuilder(int code, String message) {
            this.code = code;
            this.data = new JSONObject();
            this.message = message;
        }

        public static JsonBuilder ok(String message) {
            JsonBuilder builder = new JsonBuilder(10000, message);
            return builder;
        }

        public static JsonBuilder ok() {
            return ok("Ok");
        }

        public static JsonBuilder ok(int code) {
            JsonBuilder builder = new JsonBuilder(code, "Ok");
            return builder;
        }

        public static JsonBuilder failed(int code) {
            return failed(code, "failed");
        }

        public static JsonBuilder failed(int code, String message) {
            return new JsonBuilder(code, message);
        }

        public static JsonBuilder redirect(String message) {
            return new JsonBuilder(302).message(message);
        }

        public static JsonBuilder redirect(int code) {
            return new JsonBuilder(code);
        }

        public JsonBuilder id(UUID id) {
            data.put("ID", id.toString());
            return this;
        }

        public JsonBuilder id(String id) {
            data.put("ID", id);
            return this;
        }

        public JsonBuilder list(List<?> list) {
            data.put("List", list);
            return this;
        }

        public JsonBuilder object(Object object) {
            data.put("Object", object);
            return this;
        }

        public JsonBuilder message(String message) {
            this.message = message;
            return this;
        }

        public JsonBuilder property(String key, Object object) {
            data.put(key, object);
            return this;
        }

        public static JsonBuilder badRequest() {
            return new JsonBuilder(20000);
        }

        public static JsonBuilder badRequest(int code) {
            Assert.isTrue(code >= 20000 && code < 50000, "expression is not true:20000<=code<50000");
            return new JsonBuilder(code);
        }

        public static JsonBuilder badRequest(String message) {
            return badRequest().message(message);
        }

//        public static JsonBuilder badRequest(String message, I18nService I18nService, HttpServletRequest request) {
////            return badRequest().message(I18nService.getMessage(message, request));
////        }
////
////        public static JsonBuilder badRequest(int code, String message) {
////            return badRequest(code).message(message);
////        }
////
////        public static JsonBuilder badRequest(int code, String message, I18nService i18nService, HttpServletRequest request) {
////            return badRequest(code).message(i18nService.getMessage(message, request));
////        }

        public static JsonBuilder error() {
            return new JsonBuilder(50000);
        }

        public static JsonBuilder error(int code) {
            Assert.isTrue(code >= 50000 || code < 0, "expression is not true:code>=500");
            return new JsonBuilder(code);
        }

        public static JsonBuilder error(String message) {
            return error().message(message);
        }

        public static JsonBuilder error(int code, String message) {
            return error(code).message(message);
        }

//        public static JsonBuilder error(int code, String message, I18nService I18nService, HttpServletRequest request) {
//            return error(code).message(I18nService.getMessage(message, request));
//        }
//
//        public static JsonBuilder error(String message, I18nService I18nService, HttpServletRequest request) {
//            return error().message(I18nService.getMessage(message, request));
//        }

        public JSONObject json() {
            JSONObject object = new JSONObject();
            object.put("Code", code);
            object.put("Message", message);
            object.put("Data", data);
            return object;
        }
    }

    public static String json(Object object) {
        return toJSONString(object);
    }
}