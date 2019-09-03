package com.znv.fssrqs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.znv.fssrqs.entity.mysql.UserGroup;
import com.znv.fssrqs.entity.mysql.UserGroupDeviceRelation;
import com.znv.fssrqs.service.redis.AccessPrecintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by dongzelong on  2019/8/1 14:10.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class DeviceService {
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private UserGroupDeviceService userGroupDeviceService;
    @Autowired
    private AccessPrecintService accessPrecintService;

    /**
     * 获取用户设备树
     */
    public JSONArray getUserDeviceTree(String userId) {
        JSONArray retCameras = new JSONArray();
        //区域
        Map<String, JSONObject> precinctMap = new HashMap<>();
        JSONObject rootJson = new JSONObject();
        //用户组
        UserGroup userGroup = userGroupService.queryUserGroupByUserId(userId);
        if (userGroup != null) {
            //区域列表
            Map<String, JSONObject> globalPrecincts = accessPrecintService.getAllPrecint();
            rootJson.put("PID", "0");
            //用户组ID
            rootJson.put("ID", userGroup.getUserGroupID());
            rootJson.put("IconSkin", "icon-precinct");
            rootJson.put("Open", "true");
            rootJson.put("Name", userGroup.getUserGroupName() + "(0)");
            retCameras.add(rootJson);
            //超级管理员角色
            int roleId = userGroup.getRoleID();
            //用户组设备权限
            List<UserGroupDeviceRelation> userGroupDevices = null;
            //授权摄像机个数
            //int count = LicenseUtil.instance.getTaskCount();
            int count=1000;
//            if (count == 0) {
//                return retCameras;
//            }
            if (roleId == 1) {
                userGroupDevices = userGroupDeviceService.queryAdminUserDevice();
            } else {
                userGroupDevices = userGroupDeviceService.queryUserDeviceByUserGroupId(userGroup.getUserGroupID());
            }

            if (userGroupDevices != null && userGroupDevices.size() > 0) {
                if (userGroupDevices.size() > count) {
                    userGroupDevices = userGroupDevices.subList(0, count);
                }
                rootJson.put("Name", userGroup.getUserGroupName() + "(" + userGroupDevices.size() + ")");
                //用户所属分组ID
                Integer userGroupId = userGroup.getUserGroupID();
                Map<String, JSONObject> precintMap = accessPrecintService.getAllPrecint();
                precintMap.forEach(new BiConsumer<String, JSONObject>() {
                    @Override
                    public void accept(String key, JSONObject jsonObject) {
                        int total = jsonObject.getInteger("Total") == null ? 0 : jsonObject.getInteger("Total");
                        jsonObject.put("Total", total);
                    }
                });

                ObjectMapper mapper = new ObjectMapper();
                //用户分组+摄像机
                for (UserGroupDeviceRelation userGroupDevice : userGroupDevices) {
                    JSONObject userGroupDeviceObject = null;
                    try {
                        userGroupDeviceObject = JSONObject.parseObject(mapper.writeValueAsString(userGroupDevice));
                    } catch (JsonProcessingException e) {
                        continue;
                    }
                    //拼接设备树
                    this.splitJointDeviceTree(userGroupDeviceObject, retCameras, precinctMap, userGroupId, precintMap, globalPrecincts);
                }

                for (Map.Entry<String, JSONObject> entry : precinctMap.entrySet()) {
                    String precinctId = entry.getKey();
                    if (precintMap.containsKey(precinctId)) {
                        JSONObject precint = precintMap.get(precinctId);
                        JSONObject precintObject = entry.getValue();
                        precintObject.put("Total", precint.getString("Total"));
                        precintObject.put("Name", precintObject.getString("Name") + "(" + precint.getString("Total") + ")");
                    } else {
                        JSONObject precintObject = entry.getValue();
                        precintObject.put("Name", precintObject.getString("Name") + "(" + precintObject.getString("Total") + ")");
                    }
                }
            }

            retCameras.addAll(precinctMap.values());
        }
        precinctMap.clear();
        return retCameras;
    }


    /**
     * @param userGroupDeviceObject 用户组和设备关系
     * @param retCameras            返回摄像机
     * @param precinctMap
     * @param userGroupId
     */
    private void splitJointDeviceTree(JSONObject userGroupDeviceObject, JSONArray retCameras, Map<String, JSONObject> precinctMap, int userGroupId, Map<String, JSONObject> precintMap, Map<String, JSONObject> globalPrecincts) {
        //区域ID
        String precinctId = userGroupDeviceObject.getString("PrecinctID");
        userGroupDeviceObject.put("PID", precinctId);
        //设备ID
        userGroupDeviceObject.put("ID", userGroupDeviceObject.getString("ApeID"));
        //设备名称
        userGroupDeviceObject.put("Name", userGroupDeviceObject.getString("Name"));
        //图标皮肤
        userGroupDeviceObject.put("IconSkin", "icon-camera-fss");
        retCameras.add(userGroupDeviceObject);
        //区域
        JSONObject precinct = precintMap.get(precinctId);
        String upPrecintId = precinct.getString("UpPrecinctId");
        //统计每个区域的设备数量
        int total = precinct.getInteger("Total") == null ? 0 : precinct.getInteger("Total");
        precinct.put("Total", total + 1);
        //保存最新数据
        this.updateParentsTotal(upPrecintId, precintMap);
        //监控中心区域ID,替换为用户组ID
        if ("010100000".equals(precinctId)) {
            userGroupDeviceObject.put("PID", userGroupId);
            return;
        }

        //区域区域ID
        if (precinctMap.containsKey(precinct.getString("PrecinctId"))) {
            precinct.put("Name", precinct.get("PrecinctName"));
        } else {
            findPrecinct(precinctMap, userGroupDeviceObject.getString("PID"), userGroupId, globalPrecincts);
        }
    }

    private void findPrecinct(Map<String, JSONObject> precinctMap, String precinctId, int userGroupId, Map<String, JSONObject> globalPrecincts) {
        JSONObject precinct = globalPrecincts.get(precinctId);
        if (precinct == null) {
            return;
        }
        precinct.put("IconSkin", "Icon-precinct");
        precinct.put("PId", precinct.getString("UpPrecinctId"));
        precinct.put("ID", precinct.getString("PrecinctId"));
        precinct.put("Name", precinct.get("PrecinctName"));
        if (precinct.getString("UpPrecinctId").startsWith("0@")) {
            precinct.put("PID", userGroupId);
            String pid = precinct.getString("UpPrecinctId");
            precinct.put("Name", precinct.getString("PrecinctName").concat("(" + "子域" + "[" + pid.split("@")[1] + "])"));
        }
        precinctMap.put(precinct.getString("PrecinctId"), precinct);
        if ("010100000".equals(precinct.getString("UpPrecinctId"))) {
            precinct.put("PID", userGroupId);
            precinct.put("UpPrecinctId", userGroupId);
            return;
        }
        String pId = precinct.getString("UpPrecinctId");
        findPrecinct(precinctMap, pId, userGroupId, globalPrecincts);
    }

    //更新所有父级区域的设备数量
    private void updateParentsTotal(String pId, Map<String, JSONObject> precintMap) {
        if (precintMap.containsKey(pId)) {
            JSONObject precinct = precintMap.get(pId);
            Integer tmpTotal = precinct.getInteger("Total");
            int total = tmpTotal == null ? 0 : tmpTotal;
            precinct.put("Total", total + 1);
            this.updateParentsTotal(precinct.getString("UpPrecinctId"), precintMap);
        }
    }
}
