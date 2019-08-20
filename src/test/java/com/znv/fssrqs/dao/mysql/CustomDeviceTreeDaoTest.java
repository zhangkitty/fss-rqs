package com.znv.fssrqs.dao.mysql;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.entity.mysql.CrumbCustomTreeEntity;
import com.znv.fssrqs.entity.mysql.CustomDeviceEntity;
import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomDeviceTreeDaoTest {

    @Resource
    private CustomDeviceTreeDao customDeviceTreeDao;

    @Test
    public void getCustomDeviceList() {

        List<CustomDeviceEntity> list = customDeviceTreeDao.getAllCustomDeviceList();

        List<Map<String,String>> list2 = customDeviceTreeDao.getCustomDeviceByGroup();

        list2.stream().forEach(value->{
            System.out.println(value.values().toString());
        });

        List result = list2.stream().map(value->{

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("tree_id",value.get("tree_id"));
            jsonObject.put("children",list.stream().filter(v->Arrays.asList(value.get("node_ids").split(",")).contains(v.getNodeId())).collect(Collectors.toList())) ;
            return  jsonObject;

        }).collect(Collectors.toList());

        System.out.println("mdzz");
    }

    @Test
    public void update(){
        List<CustomDeviceEntity> list = customDeviceTreeDao.getAllCustomDeviceList();
        List<CustomDeviceEntity> list1 = list.stream().map(v->{
            v.setNodeKind(4);
            return  v;
        }).collect(Collectors.toList());
        System.out.println(customDeviceTreeDao.updateBatch(list1));
    }


    @Test
    public void insertBatch(){
        List<CustomDeviceEntity> list = customDeviceTreeDao.getAllCustomDeviceList();
        List<CustomDeviceEntity> list1 = list.stream().map(v->{
            v.setNodeId(v.getNodeId()+"====");
            return  v;
        }).collect(Collectors.toList());
        System.out.println(customDeviceTreeDao.insertBatch(list1));
    }

    @Test
    public void deleteCustomerGroup(){
        Integer result = customDeviceTreeDao.deleteCustomUserGroup(60);
        System.out.println(result);
    }

    @Test
    public void findAll(){
        List<CrumbCustomTreeEntity> list = customDeviceTreeDao.getAllCrumbList();
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        ArrayList<CrumbCustomTreeEntity> list1  = new ArrayList<CrumbCustomTreeEntity>();
        list.stream().sorted((v1,v2)->v1.getCrumb().split(",").length-v2.getCrumb().split(" ").length).forEach(v->{
            add(arrayList,v);
        });

        expand(arrayList,list1);




        System.out.println("mdzz");
    }

    private void add(ArrayList<JSONObject> arrayList,CrumbCustomTreeEntity crumbCustomTreeEntity){
       if(arrayList.stream().filter(v->v.getJSONObject("crumbCustomTreeEntity").getIntValue("id")==crumbCustomTreeEntity.getParentId()).count()==0){
           JSONObject jsonObject = new JSONObject();
           jsonObject.put("children",new ArrayList<JSONObject>());
           jsonObject.put("crumbCustomTreeEntity",crumbCustomTreeEntity);
           arrayList.add(jsonObject);
       }else {
           arrayList.stream().forEach(v->{
               if(v.getJSONObject("crumbCustomTreeEntity").getIntValue("id")==crumbCustomTreeEntity.getParentId()){
                   add((ArrayList<JSONObject>) v.get("children"),crumbCustomTreeEntity);
               }
           });
       }
    }

    private void expand(ArrayList<JSONObject> arrayList,ArrayList<CrumbCustomTreeEntity> list){

       arrayList.stream().forEach(v->{
           list.add(v.getObject("crumbCustomTreeEntity",CrumbCustomTreeEntity.class));
           if(v.getObject("children",ArrayList.class).size()>0){
               expand(v.getObject("children",ArrayList.class),list);
           }
       });

    }
}