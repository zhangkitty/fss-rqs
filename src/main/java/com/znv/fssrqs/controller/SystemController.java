package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.dao.mysql.SystemInfoMapper;
import com.znv.fssrqs.entity.mysql.MBusEntity;
import com.znv.fssrqs.entity.mysql.SystemInfo;
import com.znv.fssrqs.service.DiskSpaceService;
import com.znv.fssrqs.timer.SystemDeviceLoadTask;
import com.znv.fssrqs.util.FastJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by dongzelong on  2019/8/19 16:03.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
@Transactional(transactionManager = "transactionManager")
public class SystemController {
    @Autowired
    private SystemDeviceLoadTask systemDeviceLoadTask;
    @Resource
    private SystemInfoMapper systemInfoMapper;
    @Autowired
    private DiskSpaceService diskSpaceService;

    /**
     * 获取系统信息
     */
    @GetMapping("/system/info")
    public JSONObject getSysInfo(HttpServletRequest request) {
        SystemInfo systemInfo = systemInfoMapper.selectOne();
        return FastJsonUtils.JsonBuilder.ok().object(systemInfo).json();
    }

    /**
     * 修改系统信息
     */
    @PutMapping("/system/info")
    @Transactional(transactionManager = "transactionManager")
    public JSONObject modifySystemInfo(HttpServletRequest request, @RequestBody String body) throws IOException {
        final SystemInfo systemInfo = JSON.parseObject(body, SystemInfo.class);
        systemInfoMapper.updateByExampleSelective(systemInfo);
        return FastJsonUtils.JsonBuilder.ok().json();
    }

    @GetMapping("/mbus/ipps")
    public String getMbusIpps(HttpServletRequest request) {
        final List<MBusEntity> mBusOnlineList = systemDeviceLoadTask.getMBusOnlineList();
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().list(mBusOnlineList).json(), new PascalNameFilter());
    }

    /**
     * 获取大数据磁盘信息
     */
    @GetMapping("/linux/disks")
    public JSONObject getDiskInfo() {
        final JSONObject diskCountMap = diskSpaceService.getDiskCountMap();
        final Object totalObject = diskCountMap.get("Total");
        return FastJsonUtils.JsonBuilder.ok().property("Total", totalObject).list((List<?>) diskCountMap.get("List")).json();
    }
}
