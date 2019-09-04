package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.ControlCameraMapper;
import com.znv.fssrqs.dao.mysql.LibDao;
import com.znv.fssrqs.elasticsearch.homepage.*;
import com.znv.fssrqs.service.DeviceService;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@Slf4j
public class HomeController {
    @Autowired
    private AlarmTopLibCountService alarmTopLibCountService;
    @Autowired
    private HistoryAlarmDataService historyAlarmDataService;
    @Autowired
    private DeviceCaptureService deviceCaptureService;
    @Autowired
    private TopTenDeviceAlarmService topTenDeviceAlarmService;
    @Resource
    private LibDao libDao;
    @Autowired
    private ControlCameraMapper controlCameraMapper;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private PersonListCountService personListCountService;

    /**
     * 南岸库告警总数接口
     */
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
                    jsonObject.put("LibName", libMap.get(libId).get("LibName"));
                } else {
                    jsonObject.put("LibName", "");
                }
                jsonObject.put("DocCount", 0);
                esAgg.add(jsonObject);
            }
        });

        return FastJsonUtils.JsonBuilder.ok().list(esAgg).property("Total", resultObject.getJSONObject("hits").getIntValue("total")).json();
    }

    /**
     * 查询历史告警列表
     */
    @GetMapping({"/person/alarms"})
    public JSONObject getHistoryAlarmList(@RequestHeader("Host") String host, @RequestParam Map<String, Object> params) {
        String remoteIp = host.split(":")[0];
        final JSONObject historyAlarmList = historyAlarmDataService.getHistoryAlarmList(params, remoteIp);
        return historyAlarmList;
    }

    /**
     * 设备抓拍统计
     * 0-日,1-自然周,2-自然月
     */
    @PostMapping("/device/capture/statistics")
    public JSONObject getHistoryDeviceCapture(@RequestBody String body) {
        final JSONObject params = JSON.parseObject(body);
        return deviceCaptureService.getDeviceCaptureList(params);
    }

    /**
     * 查询摄像机前十告警数统计
     */
    @GetMapping("/top/ten/device/alarms")
    public JSONObject top10DeviceAlarm(@RequestParam Map<String, Object> params) {
        return topTenDeviceAlarmService.top10DeviceAlarms(params);
    }

    @GetMapping("/control/task/num")
    public JSONObject getControlTasks() {
        int count = controlCameraMapper.count();
        return FastJsonUtils.JsonBuilder.ok().property("Total", count).json();
    }

    /**
     * 设备树统计
     */
    @GetMapping("/device/num/statistics")
    public JSONObject getDeviceStatistics() {
        final JSONObject deviceStatistics = deviceService.getDeviceStatistics();
        return FastJsonUtils.JsonBuilder.ok().object(deviceStatistics).json();
    }

    @GetMapping("/person/statistics")
    public JSONObject getKeyPersons() {
        return personListCountService.getPersonStatistics();
    }
}
