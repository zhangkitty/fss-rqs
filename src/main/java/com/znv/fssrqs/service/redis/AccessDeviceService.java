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
 * Created by dongzelong on  2019/8/5 18:22.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class AccessDeviceService {
    @Autowired
    private RedisTemplateService redisTemplateService;
    private final String TABLE_NAME = "MDevice";

    public Map<String, JSONObject> getAllDevice() {
        final Set<String> keys = redisTemplateService.getSet(TABLE_NAME);
        final List<String> list = redisTemplateService.multiGet(keys);
        Map<String, JSONObject> map = Maps.newHashMap();
        list.parallelStream().forEach(object -> {
            final JSONObject precinct = JSON.toJavaObject(JSON.parseObject(object), JSONObject.class);
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : precinct.entrySet()) {
                final Object value = entry.getValue();
                final String key = entry.getKey();
                String[] keyArray = key.split("_");
                StringBuffer sb = new StringBuffer();
                for (String tmpKey : keyArray) {
                    sb.append(tmpKey.substring(0, 1).toUpperCase() + tmpKey.substring(1).toLowerCase());
                }
                jsonObject.put(sb.toString(), value);
            }
            map.put(jsonObject.getString("DeviceId"), jsonObject);
        });
        return map;
    }
}
