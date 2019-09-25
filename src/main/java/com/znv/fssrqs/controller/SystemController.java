package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.dao.mysql.SystemInfoMapper;
import com.znv.fssrqs.entity.mysql.MBusEntity;
import com.znv.fssrqs.entity.mysql.SystemInfo;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.service.DiskSpaceService;
import com.znv.fssrqs.service.reid.ReidUnitService;
import com.znv.fssrqs.timer.SystemDeviceLoadTask;
import com.znv.fssrqs.util.FastJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ReidUnitService reidUnitService;

    /**
     * 获取系统信息
     */
    @GetMapping("/system/info")
    public JSONObject getSysInfo(HttpServletRequest request) {
        SystemInfo systemInfo = systemInfoMapper.selectOne();
        return FastJsonUtils.JsonBuilder.ok().object(systemInfo).json();
    }


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

    @Value("${spring.datasource.mysql.jdbc-url}")
    private String jdbcUrl;

    @GetMapping("/icap/ipps")
    public JSONObject getIcapIpps() {
        String ip = jdbcUrl.split(":")[2].substring(2);
        String port = "8000";
        return FastJsonUtils.JsonBuilder.ok().property("Ipps", ip + ":" + port).json();
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


    @GetMapping(value = "/Info/DeviceType")
    public JSONObject getInfoDeviceType(@RequestParam Map mapParam) {
        if (!mapParam.containsKey("DeviceKind")) {
            throw ZnvException.error("RequestParamNull", "DeviceKind");
        }

        return FastJsonUtils.JsonBuilder.ok().list(reidUnitService.getInfoDeviceType(mapParam)).json();
    }

    @GetMapping(value = "/Info/Manufacture")
    public JSONObject getInfoManufacture(@RequestParam Map mapParam) {
        if (!mapParam.containsKey("DeviceKind")) {
            throw ZnvException.error("RequestParamNull", "DeviceKind");
        }

        return FastJsonUtils.JsonBuilder.ok().list(reidUnitService.getInfoManufacture(mapParam)).json();
    }
}
