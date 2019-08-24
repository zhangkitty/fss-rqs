package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.dao.mysql.DeviceDeptRelationDao;
import com.znv.fssrqs.dao.mysql.FaceAITaskDao;
import com.znv.fssrqs.entity.mysql.MapDeviceLocation;
import com.znv.fssrqs.entity.mysql.UserGroup;
import com.znv.fssrqs.entity.mysql.UserGroupDeviceRelation;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.service.UserGroupDeviceService;
import com.znv.fssrqs.service.UserGroupService;
import com.znv.fssrqs.service.redis.AccessDeviceService;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.LocalUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by dongzelong on  2019/6/1 11:49.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CameraController {
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private UserGroupDeviceService userGroupDeviceService;
    @Resource
    private DeviceDeptRelationDao deviceDeptRelationDao;
    @Autowired
    private AccessDeviceService accessDeviceService;
    @Resource
    private FaceAITaskDao faceAITaskDao;

    /**
     * 查询用户下设备列表
     */
    @GetMapping("/VIID/APEs")
    public String getCameras() {
        JSONArray jsonArray = new JSONArray();
        JSONObject user = LocalUserUtil.getLocalUser();
        if (user == null || !user.containsKey("UserId")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
        }
        String userId = user.getString("UserId");
        UserGroup userGroup = userGroupService.queryUserGroupByUserId(userId);
        if (userGroup != null) {
            int roleId = userGroup.getRoleID();
            List<UserGroupDeviceRelation> userGroupDevices;
            //int count = LicenseUtil.instance.getTaskCount();
            if (roleId == 1) {
                userGroupDevices = userGroupDeviceService.queryAdminUserDevice();
            } else {
                userGroupDevices = userGroupDeviceService.queryUserDeviceByUserGroupId(userGroup.getUserGroupID());
            }

            if (userGroupDevices != null && userGroupDevices.size() > 0) {
//                if (userGroupDevices.size() > count) {
//                    userGroupDevices = userGroupDevices.subList(0, count);
//                }
                userGroupDevices.subList(0, 1000);
                for (UserGroupDeviceRelation userGroupDevice : userGroupDevices) {
                    jsonArray.add(userGroupDevice);
                }
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("APEList", jsonArray);
        String res = JSON.toJSONString(jsonObject, new PascalNameFilter());
        return res;
    }

    /**
     * 查询用户下设备列表
     *
     * @param
     * @return
     */
    @GetMapping("/cameras")
    public String selectCameras(HttpServletRequest request) {
        JSONObject user = LocalUserUtil.getLocalUser();
        if (user == null || !user.containsKey("UserId")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
        }
        String cameras = getCameras();
        JSONObject jsonObject = JSON.parseObject(cameras, JSONObject.class);
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().list(jsonObject.getJSONArray("APEList")).json(), new PascalNameFilter());
    }

    @GetMapping("/map/cameras")
    public String getMapCameras(HttpServletRequest request) {
        JSONObject user = LocalUserUtil.getLocalUser();
        if (user == null || !user.containsKey("UserId")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
        }
        ArrayList<String> list = new ArrayList<>();
        list.add("all");
        user.put("FunctionList", list);
        user.put("SessionID", request.getSession().getId());
        UserGroup userGroup = userGroupService.queryUserGroupByUserId(user.getString("UserId"));
        if (userGroup == null) {
            return FastJsonUtils.JsonBuilder.ok().list(null).json().toJSONString();
        }
        int userGroupId = userGroup.getUserGroupID();
        List<MapDeviceLocation> mapDevices;
        if (1 == userGroup.getRoleID()) {//管理员角色
            mapDevices = userGroupDeviceService.queryAdminMapDevice();
        } else {
            mapDevices = userGroupDeviceService.queryNormalMapDevice(userGroupId);
        }

        JSONObject devMap = new JSONObject();
        for (MapDeviceLocation device : mapDevices) {
            JSONObject devObj = new JSONObject();
            devObj.put("ID", device.getApeID());
            devObj.put("Name", device.getApeName());
            devObj.put("Coordinates", new Double[]{device.getLongitude(), device.getLatitude()});
            devMap.put(device.getApeID(), devObj);
        }
        return FastJsonUtils.JsonBuilder.ok().list(filterFaceTree(devMap, user)).json().toJSONString();
    }

    private JSONArray filterFaceTree(JSONObject cameraMap, JSONObject user) {
        JSONArray retArray = new JSONArray();
        try {
            //查询相关设备
            Map<String, JSONObject> devices = accessDeviceService.getAllDevice();
            //设备所在点位
            List<Map<String, Object>> deptRealtion = deviceDeptRelationDao.selectAll();
            Map<String, String> cameraRelation = new HashMap<>();
            for (Map<String, Object> map : deptRealtion) {
                cameraRelation.put(map.get("DeviceID").toString(), map.get("DeptID").toString());
            }

            Map<String, JSONObject> cameras = getDevices(devices, cameraRelation);
            final List<Map<String, Object>> maps = faceAITaskDao.selectAll();
            //获取分析任务ID
            for (Map<String, Object> map : maps) {
                //获取这个设备
                JSONObject camera = cameras.get(map.get("camera_id").toString());
                if (camera != null) {
                    String devId = camera.getString("DeviceId");
                    if (cameraMap.containsKey(devId)) {
                        JSONObject jsobj = cameraMap.getJSONObject(devId);
                        if (retArray.indexOf(jsobj) < 0) {
                            retArray.add(jsobj);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return retArray;
        }
        return retArray;
    }

    private Map<String, JSONObject> getDevices(Map<String, JSONObject> devices, Map<String, String> deptrealtion) {
        Collection<JSONObject> array = devices.values();
        Map<String, JSONObject> deviceCache = new HashMap<String, JSONObject>();
        for (JSONObject device : array) {
            device.remove("DeptId");
            String deptId = deptrealtion.get(device.getString("DeviceId"));
            if (!StringUtils.isEmpty(deptId)) {
                device.put("DeptId", deptrealtion.get(device.getString("DeviceId")));
            }
            deviceCache.put(device.getString("DeviceId"), device);
        }
        return deviceCache;
    }
}
