package com.znv.fssrqs.service.compareservice;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class CompareServiceTest {

    @Resource
    private CompareService compareService;

    @Test
    public void check() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("LibID",Arrays.asList(2,3));
        jsonObject.put("LimitCount",20000);

        compareService.check(jsonObject);

    }
}