package com.znv.fssrqs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.znv.fssrqs.config.HomeConfig;
import com.znv.fssrqs.dao.mysql.AITaskDeviceRuleDao;
import com.znv.fssrqs.dao.mysql.MDeviceDao;
import com.znv.fssrqs.dao.mysql.ReidAnalysisTaskDao;
import com.znv.fssrqs.dao.mysql.ReidTaskDao;
import com.znv.fssrqs.entity.mysql.UserGroup;
import com.znv.fssrqs.entity.mysql.UserGroupDeviceRelation;
import com.znv.fssrqs.service.redis.AccessPrecintService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    @Autowired
    private HomeConfig homeConfig;
    @Resource
    private MDeviceDao mDeviceDao;
    @Resource
    private ReidAnalysisTaskDao reidAnalysisTaskDao;
    @Resource
    private AITaskDeviceRuleDao aiTaskDeviceRuleDao;

    /**
     * 获取用户设备树
     */
    public JSONArray getUserDeviceTree(String userId, String useType) {
        JSONArray retCameras = new JSONArray();
        //区域
        Map<String, JSONObject> showPrecinctMap = new HashMap<>();
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
            int count = 1000;
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

                List<String> deviceIds = Lists.newArrayList();
                userGroupDevices.stream().forEach(userGroupDeviceRelation -> {
                    final String apeID = userGroupDeviceRelation.getApeID();
                    deviceIds.add(apeID);
                });

                //人体分析任务关联的设备
                final List<String> faceDeviceIds = aiTaskDeviceRuleDao.getDevicesByDeviceIds(deviceIds);
                //人脸分析任务关联的设备
                final List<String> reidDeviceIds = reidAnalysisTaskDao.getDevicesByDeviceIds(deviceIds);
                //用户所属分组ID
                Integer userGroupId = userGroup.getUserGroupID();
                //区域ID
                Map<String, JSONObject> precintMap = accessPrecintService.getAllPrecint();
                precintMap.forEach(new BiConsumer<String, JSONObject>() {
                    @Override
                    public void accept(String key, JSONObject jsonObject) {
                        int total = jsonObject.getInteger("Total") == null ? 0 : jsonObject.getInteger("Total");
                        jsonObject.put("Total", total);
                    }
                });

                ObjectMapper mapper = new ObjectMapper();
                long size = 0L;
                //用户分组+摄像机
                for (UserGroupDeviceRelation userGroupDevice : userGroupDevices) {
                    final String apeID = userGroupDevice.getApeID();
                    if (faceDeviceIds.contains(apeID)) {
                        userGroupDevice.setUseType(1);
                    } else if (reidDeviceIds.contains(apeID)) {
                        userGroupDevice.setUseType(2);
                    } else {
                        userGroupDevice.setUseType(0);
                    }

                    JSONObject userGroupDeviceObject = null;
                    try {
                        userGroupDeviceObject = JSONObject.parseObject(mapper.writeValueAsString(userGroupDevice));
                    } catch (JsonProcessingException e) {
                        continue;
                    }

                    if (StringUtils.isEmpty(useType)) {//查询所有设备
                        size++;
                        //拼接设备树
                        this.splitJointDeviceTree(userGroupDeviceObject, retCameras, showPrecinctMap, userGroupId, precintMap, globalPrecincts);
                    } else if ("1".equals(useType)) {//只要人脸设备
                        size++;
                        //拼接设备树
                        this.splitJointDeviceTree(userGroupDeviceObject, retCameras, showPrecinctMap, userGroupId, precintMap, globalPrecincts);
                    } else {//只要人体设备
                        size++;
                        //拼接设备树
                        this.splitJointDeviceTree(userGroupDeviceObject, retCameras, showPrecinctMap, userGroupId, precintMap, globalPrecincts);
                    }
                }

                rootJson.put("Name", userGroup.getUserGroupName() + "(" + size + ")");
                for (Map.Entry<String, JSONObject> entry : showPrecinctMap.entrySet()) {
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

            //剔除监控中心
            showPrecinctMap.remove("010100000");
            retCameras.addAll(showPrecinctMap.values());
        }
        showPrecinctMap.clear();
        return retCameras;
    }


    /**
     * @param userGroupDeviceObject 用户组和设备关系
     * @param retCameras            返回摄像机
     * @param showPrecinctMap
     * @param userGroupId
     */
    private void splitJointDeviceTree(JSONObject userGroupDeviceObject, JSONArray retCameras,
                                      Map<String, JSONObject> showPrecinctMap, int userGroupId,
                                      Map<String, JSONObject> precintMap, Map<String, JSONObject> globalPrecincts) {
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
        //所有需要统计的区域Map
        JSONObject precinct = precintMap.get(precinctId);
        String upPrecintId = precinct.getString("UpPrecinctId");
        //统计每个区域的设备数量
        int total = precinct.getInteger("Total") == null ? 0 : precinct.getInteger("Total");
        precinct.put("Total", total + 1);
        //保存最新数据
        this.updateParentsTotal(upPrecintId, precintMap);
        //监控中心区域ID,替换为用户组ID
        if ("010100000".equals(precinctId.trim())) {
            userGroupDeviceObject.put("PID", userGroupId);
            return;
        }

        //区域ID
        if (showPrecinctMap.containsKey(precinct.getString("PrecinctId"))) {
            precinct.put("Name", precinct.get("PrecinctName"));
        } else {
            findPrecinct(showPrecinctMap, userGroupDeviceObject.getString("PID"), userGroupId, globalPrecincts);
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

    public JSONObject getDeviceStatistics() {
        JSONObject jsonObject = new JSONObject();
        //设置服务器数量
        jsonObject.put("BusAndAccessServerCount", homeConfig.getBussinessAndAccess());
        jsonObject.put("FaceAIUnitServerCount", homeConfig.getFaceAIUnit());
        jsonObject.put("BigDataServerCount", homeConfig.getBigData());
        jsonObject.put("ServerTotal", homeConfig.getBussinessAndAccess() + homeConfig.getFaceAIUnit() + homeConfig.getBigData());
        //设置服务器数量
        jsonObject.put("BusAndAccessMachineCount", homeConfig.getBussinessMachine());
        jsonObject.put("FaceAIUnitMachineCount", homeConfig.getFaceAIUnitMachine());
        jsonObject.put("BigDataMachineCount", homeConfig.getBigDataEngineMachine());
        jsonObject.put("MachineTotal", homeConfig.getBussinessMachine() + homeConfig.getFaceAIUnitMachine() + homeConfig.getBigDataEngineMachine());
        //调用存过统计设备
        final Map deviceCountMap = mDeviceDao.getDeviceCount();
        jsonObject.put("NormalCameraCount", deviceCountMap.getOrDefault("normalCameraCount", 0));
        jsonObject.put("SubDomainCameraCount", deviceCountMap.getOrDefault("subDomainCount", 0));
        jsonObject.put("CaptureCameraCount", deviceCountMap.getOrDefault("captureCameraCount", 0));
        jsonObject.put("CameraTotal", deviceCountMap.getOrDefault("deviceTotal", 0));
        return jsonObject;
    }
}
