package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.entity.mysql.UserGroup;
import com.znv.fssrqs.entity.mysql.UserGroupDeviceRelation;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.service.UserGroupDeviceService;
import com.znv.fssrqs.service.UserGroupService;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.LicenseUtil;
import com.znv.fssrqs.util.LocalUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dongzelong on  2019/6/1 11:49.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
public class CameraController {
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private UserGroupDeviceService userGroupDeviceService;

    /**
     * 查询用户下设备列表
     *
     * @param userId 用户ID
     */
    @GetMapping("/VIID/APEs/{userId}")
    public String getCameras(@PathVariable(value = "userId") String userId) {
        JSONArray jsonArray = new JSONArray();
        UserGroup userGroup = userGroupService.queryUserGroupByUserId(userId);
        if (userGroup != null) {
            int roleId = userGroup.getRoleID();
            List<UserGroupDeviceRelation> userGroupDevices;
            int count = LicenseUtil.instance.getTaskCount();
            if (roleId == 1) {
                userGroupDevices = userGroupDeviceService.queryAdminUserDevice();
            } else {
                userGroupDevices = userGroupDeviceService.queryUserDeviceByUserGroupId(userGroup.getUserGroupID());
            }

            if (userGroupDevices != null && userGroupDevices.size() > 0) {
                if (userGroupDevices.size() > count) {
                    userGroupDevices = userGroupDevices.subList(0, count);
                }
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
    public String selectCameras() {
        JSONObject user = LocalUserUtil.getLocalUser();
        if (user == null || !user.containsKey("UserId")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
        }
        String cameras = getCameras(user.getString("UserId"));
        JSONObject jsonObject = JSON.parseObject(cameras, JSONObject.class);
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().list(jsonObject.getJSONArray("APEList")).json(), new PascalNameFilter());
    }
}
