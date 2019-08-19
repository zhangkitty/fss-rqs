package com.znv.fssrqs.controller.tree;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.CustomDeviceTreeDao;
import com.znv.fssrqs.dao.mysql.DeviceTreeDao;
import com.znv.fssrqs.dao.mysql.FaceTaskDao;
import com.znv.fssrqs.entity.mysql.CustomDeviceEntity;
import com.znv.fssrqs.entity.mysql.CustomUserGroupEntity;
import com.znv.fssrqs.entity.mysql.FaceTaskEntity;
import com.znv.fssrqs.entity.mysql.TCfgDevice;
import com.znv.fssrqs.service.RedisTemplateService;
import com.znv.fssrqs.service.redis.AccessDeviceService;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.Data;
import lombok.val;
import org.apache.hadoop.classification.InterfaceAudience;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.ws.rs.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午10:11
 */

@RestController
public class CustomDeviceTreeController {

    @Autowired
    private RedisTemplateService redisTemplateService;

    @Autowired
    private AccessDeviceService accessDeviceService;

    @Resource
    private DeviceTreeDao deviceTreeDao;

    @Resource
    private FaceTaskDao faceTaskDao;

    @Resource
    private CustomDeviceTreeDao customDeviceTreeDao;

    @RequestMapping(value="/site/FSSAPP/pc/customtree/queryAllCustomGroupList",method = RequestMethod.GET)
    public ResponseVo queryAllCustomGroupList(){
        List<CustomUserGroupEntity> list = customDeviceTreeDao.getAllCustomUserGroup();
        return ResponseVo.success(list);
    }


    @RequestMapping(value = "/site/FSSAPP/pc/customtree/addCustomGroup",method = RequestMethod.POST)
    public ResponseVo saveCustomGroup(@RequestBody CustomUserGroupEntity customUserGroupEntity){
        Integer count = customDeviceTreeDao.saveCustomUserGroup(customUserGroupEntity);
        if(count>0){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("treeId",customUserGroupEntity.getTreeId());
            return ResponseVo.success(jsonObject);
        }
        else {
            return ResponseVo.error("新增用户组失败");
        }
    }

    @RequestMapping(value = "/site/FSSAPP/pc/customtree/deleteCustomGroup/{treeId}")
    public ResponseVo deleteCustomGroup(@PathVariable("treeId") Integer treeId){
        Integer result = customDeviceTreeDao.deleteCustomUserGroup(treeId);
        if(result>0){
            return ResponseVo.success("删除成功");
        }else {
            return ResponseVo.error("删除失败");
        }
    }


    @RequestMapping(value = "/site/FSSAPP/pc/customtree/quyerCustomTreeNodes.ds",method = RequestMethod.GET)
    public ResponseVo getCustomDeviceTree(){
        List<CustomDeviceEntity> list = customDeviceTreeDao.getAllCustomDeviceList();
        List<Map<String, String>> list2 = customDeviceTreeDao.getCustomDeviceByGroup();
        list2.stream().forEach(value -> {
            System.out.println(value.values().toString());
        });
        List result = list2.stream().map(value -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tree_id", value.get("tree_id"));
            jsonObject.put("children", list.stream().filter(v -> Arrays.asList(value.get("node_ids").split(",")).contains(v.getNodeId())).collect(Collectors.toList()));
            return jsonObject;

        }).collect(Collectors.toList());
        return ResponseVo.success(result);
    }

    @RequestMapping(value = "/site/FSSAPP/pc/customtree/saveCustomTreeNodes.ds",method = RequestMethod.POST)
    public ResponseVo saveCustomDeviceTree(@RequestBody @Validated List<Map<String,Object>> list){
        List<CustomDeviceEntity> list1 = list.stream().flatMap(v->((List<HashMap>)v.get("children")).stream())
                .map(v->{
                    CustomDeviceEntity customDeviceEntity = new CustomDeviceEntity();
                    customDeviceEntity.setNodeId((String) v.get("nodeId"));
                    customDeviceEntity.setNodeKind((Integer)v.get("nodeKind"));
                    customDeviceEntity.setNodeName((String)v.get("nodeName"));
                    customDeviceEntity.setTreeId((Integer)v.get("treeId"));
                    customDeviceEntity.setUpNodeId((String)v.get("upNodeId"));
                    return customDeviceEntity;
                })
                .collect(Collectors.toList());
        List<CustomDeviceEntity> allList = customDeviceTreeDao.getAllCustomDeviceList();
        List<String> allListNodeId = allList.stream().map(t->t.getNodeId()).collect(Collectors.toList());
        list1.stream().map(v->allListNodeId.contains(v.getNodeId()));
        customDeviceTreeDao.updateBatch(
                list1.stream().filter(v->allListNodeId.contains(v.getNodeId())).collect(Collectors.toList()));
        if( list1.stream().filter(v->!allListNodeId.contains(v.getNodeId())).collect(Collectors.toList()).size()>0){
            customDeviceTreeDao.insertBatch(
                    list1.stream().filter(v->!allListNodeId.contains(v.getNodeId())).collect(Collectors.toList())
            );
        }
        return  ResponseVo.success(null);
    }
}
