package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.entity.mysql.UserGroup;
import com.znv.fssrqs.entity.mysql.UserGroupDeviceRelation;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.service.CustomTreeNodeService;
import com.znv.fssrqs.service.CustomTreeService;
import com.znv.fssrqs.service.UserGroupDeviceService;
import com.znv.fssrqs.service.UserGroupService;
import com.znv.fssrqs.util.LocalUserUtil;
import com.znv.fssrqs.entity.mysql.CustomTree;
import com.znv.fssrqs.entity.mysql.CustomTreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by dongzelong on  2019/8/1 11:16.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DeviceTreeController {
    @Autowired
    private CustomTreeService customTreeService;
    @Autowired
    private CustomTreeNodeService customTreeNodeService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private UserGroupDeviceService userGroupDeviceService;

    @GetMapping("/customize/device/tree/{id}")
    public String getCustomTreeById(@PathVariable(value = "id") String treeId, HttpServletRequest request) {
        //获取一棵树
        CustomTree treeObj = customTreeService.getTree(treeId);
        //获取该棵树下所有自定义树
        List<CustomTreeNode> nodes = customTreeNodeService.selectTreeNodeByTreeId(Integer.parseInt(treeId));
        nodes = filterCustomNodes(request, nodes); // 根据权限过滤
        JSONArray retArr = new JSONArray();
        if (treeObj == null) {
            return retArr.toJSONString();
        }
        //顶级树
        JSONObject treeJs = new JSONObject();
        treeJs.put("id", treeId);
        treeJs.put("name", treeObj.getTreeName());
        treeJs.put("device_kind", 2);
        treeJs.put("iconSkin", "icon-precinct");
        treeJs.put("pId", -1);
        treeJs.put("open", true);
        retArr.add(treeJs);
        Map<String, Long> rootMap = new HashMap<>();
        rootMap.put("count", 0L);
        Map<String, JSONObject> areaMap = new HashMap<>();
        Map<String, JSONObject> cameraMap = new HashMap<>();
        nodes.forEach(n -> {
            JSONObject js = new JSONObject();
            //节点ID
            String id = n.getNodeId();
            //节点名称
            String name = n.getNodeName();
            //节点类型
            int deviceKind = n.getNodeKind();
            if (deviceKind == 4) {
                Long count = rootMap.get("count");
                count += 1;
                rootMap.put("count", count);
                cameraMap.put(id, js);
            } else {
                //初始化设备总量为0
                js.put("total", 0);
                areaMap.put(id, js);
            }
            //2-区域,4-摄像机
            String iconSkin = deviceKind == 4 ? "icon-camera-fss" : "icon-precinct";
            id = deviceKind == 4 ? id.replaceFirst(treeId, "") : id;
            //节点类型,上级节点ID
            js.put("id", id);
            js.put("pId", n.getUpNodeId());
            js.put("name", name);
            js.put("device_kind", deviceKind);
            js.put("iconSkin", iconSkin);
        });

        for (Map.Entry<String, JSONObject> entry : cameraMap.entrySet()) {
            JSONObject cameraObject = entry.getValue();
            String pId = cameraObject.getString("pId");
            recursive(pId, areaMap);
        }

        Stream<JSONObject> stream = areaMap.values().parallelStream()
                .filter(jsonObject -> jsonObject.getInteger("total") != 0 || String.valueOf(jsonObject.get("pId")).equals("-1"));
        Object[] objects = stream.toArray();
        Arrays.stream(objects).parallel().forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            jsonObject.put("name", jsonObject.getString("name") + "(" + jsonObject.getInteger("total") + ")");
        });
        retArr.addAll(Arrays.asList(objects));
        retArr.addAll(cameraMap.values());
        treeJs.put("name", treeObj.getTreeName() + "(" + rootMap.get("count") + ")");
        return retArr.toJSONString();
    }

    private List<CustomTreeNode> filterCustomNodes(HttpServletRequest request, List<CustomTreeNode> nodes) {
        List<CustomTreeNode> res = new ArrayList<CustomTreeNode>();
        JSONObject user = LocalUserUtil.getLocalUser();
        if (user == null || !user.containsKey("UserId")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
        }
        if (Objects.nonNull(user)) {
            String userId = user.getString("UserId");
            UserGroup userGroup = userGroupService.queryUserGroupByUserId(userId);
            if (userGroup != null) {
                int roleId = userGroup.getRoleID();
                if (roleId == 1) { // admin user
                    res = nodes;
                } else {
                    List<CustomTreeNode> officeRes = new ArrayList<CustomTreeNode>();
                    List<UserGroupDeviceRelation> userGroupDevices = userGroupDeviceService.queryUserDeviceByUserGroupId(userGroup.getUserGroupID());
                    List<String> userGroupDevicesId = new ArrayList<String>();
                    int userGroupDevicesLen = userGroupDevices.size();
                    for (int i = 0; i < userGroupDevicesLen; i++) {
                        userGroupDevicesId.add(userGroupDevices.get(i).getApeID());
                    }
                    int nodeLen = nodes.size();
                    for (int i = 0; i < nodeLen; i++) {
                        CustomTreeNode curNode = nodes.get(i);
                        int curNodeKind = curNode.getNodeKind();
                        if (curNodeKind == 2) { // office
                            officeRes.add(curNode);
                        } else if (curNodeKind == 4) { // camera
                            String curNodeId = curNode.getNodeId().substring(1);
                            if (userGroupDevicesId.indexOf(curNodeId) >= 0) {
                                res.add(curNode);
                            }
                        }
                    }
                    res.addAll(officeRes);
                }
            }
        }
        return res;
    }

    private void recursive(String pId, Map<String, JSONObject> areaMap) {
        if (areaMap.containsKey(pId)) {
            JSONObject area = areaMap.get(pId);
            area.put("total", area.getInteger("total") + 1);
            recursive(area.getString("pId"), areaMap);
        }
    }
}
