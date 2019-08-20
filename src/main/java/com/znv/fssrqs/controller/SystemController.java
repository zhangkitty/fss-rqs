package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.entity.mysql.MBusEntity;
import com.znv.fssrqs.timer.SystemDeviceLoadTask;
import com.znv.fssrqs.util.FastJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by dongzelong on  2019/8/19 16:03.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
public class SystemController {
    @Autowired
    private SystemDeviceLoadTask systemDeviceLoadTask;

    /**
     * 获取系统信息
     */
    @GetMapping("/system/info")
    public String getSysInfo(HttpServletRequest request) {
        return "";
    }

    @PostMapping("/system/info")
    public String modifySystemInfo(@RequestBody String body, HttpServletRequest request) {
        final JSONObject jsonObject = JSON.parseObject(body);
        return jsonObject.toJSONString();
    }

    @GetMapping("/mbus/ipps")
    public String getMbusIpps(HttpServletRequest request) {
        final List<MBusEntity> mBusOnlineList = systemDeviceLoadTask.getMBusOnlineList();
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().list(mBusOnlineList).json(), new PascalNameFilter());
    }
}
