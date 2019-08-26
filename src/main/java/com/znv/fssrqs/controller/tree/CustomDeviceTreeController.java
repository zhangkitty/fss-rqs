package com.znv.fssrqs.controller.tree;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.CustomDeviceTreeDao;
import com.znv.fssrqs.dao.mysql.DeviceTreeDao;
import com.znv.fssrqs.dao.mysql.FaceTaskDao;
import com.znv.fssrqs.entity.mysql.*;
import com.znv.fssrqs.service.RedisTemplateService;
import com.znv.fssrqs.service.redis.AccessDeviceService;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Stream;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午10:11
 */

@RestController
@Slf4j
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

    @RequestMapping(value="/getAllCustomTree",method = RequestMethod.GET)
    public ResponseVo getAllCustomTree(){
        List<CrumbCustomTreeEntity> list = customDeviceTreeDao.getAllCrumbList();
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        ArrayList<CrumbCustomTreeEntity> list1  = new ArrayList<CrumbCustomTreeEntity>();
        list.stream().sorted((v1,v2)->v1.getCrumb().split(",").length-v2.getCrumb().split(" ").length).forEach(v->{
            add(arrayList,v);
        });

        return ResponseVo.success(arrayList);

    };

    @RequestMapping(value = "/getAllCustomList",method = RequestMethod.GET)
    public ResponseVo getAllCustomList(){
        List<CrumbCustomTreeEntity> list = customDeviceTreeDao.getAllCrumbList();
        return ResponseVo.success(list);
    }


    @RequestMapping(value="/saveAllCustomTree",method = RequestMethod.POST)
    public ResponseVo saveAllCustomTree(@RequestBody ArrayList<Map> arrayList){

        ArrayList<CrumbCustomTreeEntity> list = new ArrayList<CrumbCustomTreeEntity>();
        expand(arrayList,list);
        List<CrumbCustomTreeEntity> allList = customDeviceTreeDao.getAllCrumbList();

        List<Integer> allListIds = allList.stream().map(v->v.getId()).collect(Collectors.toList());

        List<CrumbCustomTreeEntity> insertList = list.stream().filter(v->!allListIds.contains(v.getId())).collect(Collectors.toList());

        List<CrumbCustomTreeEntity> updateList = list.stream().filter(v->allListIds.contains(v.getId())).collect(Collectors.toList());

        if(insertList.size()>0){
            customDeviceTreeDao.batchInsertCrumbList(insertList);
        }
        if(updateList.size()>0){
            customDeviceTreeDao.batchUpdateCrumbList(updateList);
        }

        return ResponseVo.success("成功");

    }

    @RequestMapping(value = "/saveAllCustomList",method = RequestMethod.POST)
    public ResponseVo saveAllCustomList(@RequestBody ArrayList<CrumbCustomTreeEntity> arrayListInput){


        ArrayList<CrumbCustomTreeEntity> arrayList = new ArrayList<>();
        try {
            ArrayList<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
            arrayListInput.stream().sorted((v1,v2)->v1.getCrumb().length()-v2.getCrumb().length())
                    .forEach(v-> add(jsonObjectList,v));
            expandJSONObject(jsonObjectList,arrayList);
        }catch (Exception e){
            return ResponseVo.error("入参错误");
        }
        List<CrumbCustomTreeEntity> allList = customDeviceTreeDao.getAllCrumbList();
        List<Integer> allListIds = allList.stream().map(v->v.getId()).collect(Collectors.toList());
        List<CrumbCustomTreeEntity> insertList = arrayList.stream().filter(v->!allListIds.contains(v.getId())).collect(Collectors.toList());
        List<CrumbCustomTreeEntity> updateList = arrayList.stream().filter(v->allListIds.contains(v.getId())).collect(Collectors.toList());
        if(insertList.size()>0){
            customDeviceTreeDao.batchInsertCrumbList(insertList);
        }
        if(updateList.size()>0){
            customDeviceTreeDao.batchUpdateCrumbList(updateList);
        }
        List<CrumbCustomTreeEntity> allList1 = customDeviceTreeDao.getAllCrumbList();
        return ResponseVo.success(allList1);
    }


    private void add(ArrayList<JSONObject> arrayList,CrumbCustomTreeEntity crumbCustomTreeEntity){
        if(
            arrayList.size()==0||crumbCustomTreeEntity.getCrumb().trim().length()==1
        ){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("children",new ArrayList<JSONObject>());
            jsonObject.put("crumbCustomTreeEntity",crumbCustomTreeEntity);
            arrayList.add(jsonObject);
        }else {
            arrayList.stream().forEach(v->{
                if(crumbCustomTreeEntity.getCrumb().contains(v.getJSONObject("crumbCustomTreeEntity").getString("crumb"))){
                    if((v.getJSONObject("crumbCustomTreeEntity").getString("crumb")+","+v.getJSONObject("crumbCustomTreeEntity").getString("id"))
                            .equals(crumbCustomTreeEntity.getCrumb().trim())){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("children",new ArrayList<JSONObject>());
                        if(v.getJSONObject("crumbCustomTreeEntity").getBoolean("isDel")){
                            crumbCustomTreeEntity.setIsDel(true);
                        }
                        jsonObject.put("crumbCustomTreeEntity",crumbCustomTreeEntity);
                        JSONArray jsonArray = v.getJSONArray("children");
                        jsonArray.add(jsonObject);
                        v.put("children",jsonArray);
                        return;
                    }
                    if(crumbCustomTreeEntity.getCrumb().contains(v.getJSONObject("crumbCustomTreeEntity").getString("crumb")+","+v.getJSONObject("crumbCustomTreeEntity").getString("id"))){
                        crumbCustomTreeEntity.setIsDel(v.getJSONObject("crumbCustomTreeEntity").getBoolean("isDel"));
                        crumbCustomTreeEntity.setIsDefault(v.getJSONObject("crumbCustomTreeEntity").getBoolean("isDefault"));
                        add((ArrayList<JSONObject>) v.get("children"),crumbCustomTreeEntity);
                    }
                }else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("children",new ArrayList<JSONObject>());
                    jsonObject.put("crumbCustomTreeEntity",crumbCustomTreeEntity);
                    arrayList.add(jsonObject);
                }
            });

        }
    }


    private void expandJSONObject(List<JSONObject> arrayList,ArrayList<CrumbCustomTreeEntity> list){
        arrayList.stream().forEach(v->{
            list.add(v.getObject("crumbCustomTreeEntity",CrumbCustomTreeEntity.class));
            if(v.getJSONArray("children").size()>0){
                String jsonStr = JSONObject.toJSONString(v.getJSONArray("children"));
                expandJSONObject(JSONObject.parseArray(jsonStr,JSONObject.class), list);
            }
        });

    }


    private void expand(ArrayList<Map> arrayList,ArrayList<CrumbCustomTreeEntity> list){

        arrayList.stream().forEach(v->{
            CrumbCustomTreeEntity crumbCustomTreeEntity = new CrumbCustomTreeEntity();

            LinkedHashMap linkedHashMap = (LinkedHashMap) v.get("crumbCustomTreeEntity");
            crumbCustomTreeEntity.setId((Integer) linkedHashMap.get("id"));
            crumbCustomTreeEntity.setCrumb((String)linkedHashMap.get("crumb"));
            crumbCustomTreeEntity.setParentId((Integer)linkedHashMap.get("parentId"));
            crumbCustomTreeEntity.setNodeId((String) linkedHashMap.get("nodeId"));
            crumbCustomTreeEntity.setNodeName((String)linkedHashMap.get("nodeName"));
            crumbCustomTreeEntity.setNodeDesc((String)linkedHashMap.get("nodeDesc"));
            crumbCustomTreeEntity.setIsLeaf((Boolean) linkedHashMap.get("isLeaf"));
            crumbCustomTreeEntity.setIsDel((Boolean)linkedHashMap.get("idDel"));

            list.add(crumbCustomTreeEntity);
            if(((ArrayList)v.get("children")).size()>0){
                expand((ArrayList)v.get("children"),list);
            }
        });

    }
}
