package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.service.hbase.PhoenixService;
import com.znv.fssrqs.util.FastJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by dongzelong on  2019/6/1 13:53.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
public class PersonController {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PhoenixService phoenixService;

    /**
     * 批量激活人员接口,支持整库激活
     */
    @PostMapping("/batch/active/control/persons")
    public String batchControl(@RequestBody String body) {
        JSONObject params = JSON.parseObject(body);
        if (params.getInteger("PersonLibType") != null) {
            return batchControlByLibId(params).toJSONString();
        } else {
            return batchControlPersons(params).toJSONString();
        }
    }

    /**
     * 勾选人员进行布控
     */
    private JSONObject batchControlPersons(JSONObject params) {
        String tableName = HdfsConfigManager.getString("fss.phoenix.table.blacklist.name");
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        JSONObject insertData = new JSONObject();
        JSONObject personData = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray pIDs = params.getJSONArray("PIDs");
        int flag = params.getInteger("Flag");
        String startTime = params.getString("StartTime");
        String endTime = params.getString("EndTime");
        JSONArray errorArray = new JSONArray();
        pIDs.forEach(id -> {
            String[] spl = ((String) id).split(":");
            String personId = spl[0];
            int libId = Integer.parseInt(spl[1]);
            data.put("person_id", personId);
            data.put("flag", flag);
            data.put("control_start_time", startTime);
            data.put("control_end_time", endTime);
            data.put("modify_time", currentDate);
            personData.put("lib_id", libId);
            personData.put("original_lib_id", libId);
            personData.put("data", data);
            insertData.put("id", "31001");
            insertData.put("table_name", tableName);
            insertData.put("data", personData);
            try {
                phoenixService.update(insertData);
            } catch (Exception e) {
                errorArray.add(id);
            }
        });

        if (errorArray.size() == 0) {
            return FastJsonUtils.JsonBuilder.ok().json();
        } else {
            return FastJsonUtils.JsonBuilder.badRequest(415).property("PersonIDs", errorArray).json();
        }
    }

    /**
     * 布控整库
     */
    private JSONObject batchControlByLibId(JSONObject params) {
        JSONObject data = new JSONObject();
        data.put("lib_id", params.getInteger("LibID"));
        data.put("flag", params.getInteger("Flag"));
        data.put("personlib_type", params.getInteger("PersonLibType"));
        data.put("control_end_time", params.getString("EndTime"));
        data.put("control_start_time", params.getString("StartTime"));
        String tableName = HdfsConfigManager.getTableName("fss.phoenix.table.blacklist.name");
        JSONObject insertData = new JSONObject();
        insertData.put("id", "31011");
        insertData.put("table_name", tableName);
        insertData.put("data", data);
        phoenixService.update(insertData);
        return FastJsonUtils.JsonBuilder.ok().json();
    }
}
