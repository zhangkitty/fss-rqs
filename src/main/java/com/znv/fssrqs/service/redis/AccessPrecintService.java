package com.znv.fssrqs.service.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.znv.fssrqs.service.RedisTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dongzelong on  2019/8/5 15:37.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class AccessPrecintService {
    @Autowired
    private RedisTemplateService redisTemplateService;
    private String tableName = "MPrecinct";

    public Map<String, JSONObject> getAllPrecint() {
        final Set<String> keys = redisTemplateService.getSet("MPrecinct");
        final List<String> list = redisTemplateService.multiGet(keys);
        Map<String, JSONObject> map = Maps.newHashMap();
        list.parallelStream().forEach(object -> {
            final JSONObject precinct = JSON.toJavaObject(JSON.parseObject(object), JSONObject.class);
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : precinct.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                String[] keyArray = key.split("_");
                StringBuffer sb = new StringBuffer();
                for (String tmpKey : keyArray) {
                    sb.append(tmpKey.substring(0, 1) + tmpKey.substring(1).toLowerCase());
                }
                jsonObject.put(sb.toString(), value);
            }
            map.put(jsonObject.getString("PrecinctId"), jsonObject);
        });
        return map;
    }
}
