package com.znv.fssrqs.service.personnel.management;


import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.personnel.management.dto.HKPersonListSearchDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class VIIDHKSDKServiceTest {

    @Autowired
    private VIIDHKSDKService viidhksdkService;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void queryHkPerson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CurrentPage",10);
        jsonObject.put("PageSize",1);

        HKPersonListSearchDTO hkPersonListSearchDTO = modelMapper.map(jsonObject,HKPersonListSearchDTO.class);

    }
}