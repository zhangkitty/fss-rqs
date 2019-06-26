package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.service.elasticsearch.history.alarm.HistoryAlarmService;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.vo.SearchRetrieval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Created by dongzelong on  2019/6/26 10:46.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 历史告警数据管理
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class HistoryAlarmController {
    @Autowired
    private HistoryAlarmService historyAlarmService;

    /**
     * 战果统计
     */
    @PostMapping("/alarm/result/statistics")
    public String getHistoryAlarm(@RequestHeader("Host") String host, @RequestBody String body) {
        SearchRetrieval searchRetrieval = JSON.parseObject(body, SearchRetrieval.class);
        JSONArray jsonArray = historyAlarmService.getAllByCondition(host, searchRetrieval);
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().list(jsonArray).json(), new PascalNameFilter());
    }
}
