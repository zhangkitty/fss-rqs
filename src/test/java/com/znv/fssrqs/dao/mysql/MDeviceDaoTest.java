package com.znv.fssrqs.dao.mysql;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MDeviceDaoTest {

    @Resource
    private MDeviceDao mDeviceDao;

    @Test
    public void getDeviceBatch() {


       ArrayList list = new ArrayList();
       list.add("11000000001110000003");
       list.add("11000000001110000005");
       List list1= mDeviceDao.getDeviceBatch(list);

        System.out.println("mdzz");
    }

    @Test
    public void getCount(){
        Integer result = mDeviceDao.getCameralCount();
        System.out.println(result);
    }
}