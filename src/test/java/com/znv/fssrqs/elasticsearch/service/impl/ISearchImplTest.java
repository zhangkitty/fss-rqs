//package com.znv.fssrqs.elasticsearch.service.impl;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.*;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class ISearchImplTest {
//
//    @Autowired
//    private ISearchImpl iSearch;
//
//    @Test
//    public void searchList(){
//        Map<String,String> map = new HashMap<>();
//        map.put("person_id","0907103550091581");
//        map.put("country","美国");
//        try {
//            iSearch.searchList(map,"person_list_data_v1_2_copy","person_list",9,false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void searchListByPage(){
//        Map<String,String> map = new HashMap<>();
//        map.put("person_id","0907103550091581");
//        map.put("country","美国");
//        try {
//            iSearch.searchListByPage(map,"person_list_data_v1_2_copy","person_list",10,2,false);
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }mdzz
//}