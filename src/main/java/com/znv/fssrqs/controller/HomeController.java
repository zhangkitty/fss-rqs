package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.znv.fssrqs.dao.mysql.LibDao;
import com.znv.fssrqs.elasticsearch.homepage.AlarmTopLibCountService;
import com.znv.fssrqs.util.FastJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by dongzelong on  2019/8/29 15:16.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
public class HomeController {
    @Autowired
    private AlarmTopLibCountService alarmTopLibCountService;
    @Resource
    private LibDao libDao;

    @PostMapping({"/area/level/alarm/num"})
    public JSONObject getResourceLibAlarmList(@RequestBody String body) {
        JSONObject params = JSON.parseObject(body);
        JSONObject resultObject = alarmTopLibCountService.getAlarmTopLibCount(params);
        Map<String, Map<String, Object>> libMap = libDao.selectAllMap();
        //聚合结果
        final JSONArray esAgg = resultObject.getJSONObject("aggregations").getJSONObject("lib_ids").getJSONArray("buckets");
        //遍历聚合
        Iterator<Object> iterator = esAgg.iterator();
        Set<String> libIdSet = Sets.newHashSet();
        while (iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            String libId = jsonObject.getString("key");
            libIdSet.add(libId);
            jsonObject.put("LibID", libId);
            jsonObject.remove("key");
            if (libMap.containsKey(libId)) {
                jsonObject.put("LibName", libMap.get(libId).get("LibName"));
            } else {
                jsonObject.put("LibName", "");
            }
            final int docCount = jsonObject.getIntValue("doc_count");
            jsonObject.put("DocCount", docCount);
            jsonObject.remove("doc_count");
        }

        JSONArray array = params.getJSONArray("LibIDs");
        array.forEach(o -> {
            String libId = (String) o;
            if (!libIdSet.contains(libId)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("LibID", libId);
                if (libMap.containsKey(libId)) {
                    jsonObject.put("libName", libMap.get(libId).get("LibName"));
                } else {
                    jsonObject.put("libName", "");
                }
                jsonObject.put("DocCount", 0);
                esAgg.add(jsonObject);
            }
        });

        return FastJsonUtils.JsonBuilder.ok().list(esAgg).property("Total", resultObject.getJSONObject("hits").getIntValue("total")).json();
    }
}
