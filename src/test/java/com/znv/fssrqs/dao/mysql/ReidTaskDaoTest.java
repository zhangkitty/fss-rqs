package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.ReidTaskEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ReidTaskDaoTest {


    @Resource
    private ReidTaskDao reidTaskDao;

    @Test
    public void sava() {
        ReidTaskEntity reidTaskEntity = new ReidTaskEntity();

        reidTaskEntity.setTaskName("taskName");
        reidTaskEntity.setReidUnitId("1232141");
        reidTaskEntity.setDeviceId("31414123");
        reidTaskEntity.setUserId("dsafafafa");
        reidTaskEntity.setUrl("sdfafaf");
        reidTaskEntity.setReidParamsText("fasdfafaf");

        reidTaskDao.sava(reidTaskEntity);
    }

    @Test
    public void getALl(){

    }

    @Test
    public void update(){
        ReidTaskEntity reidTaskEntity = new ReidTaskEntity();
        reidTaskEntity.setTaskId(31);
        reidTaskEntity.setTaskName("taskName");
        reidTaskEntity.setReidUnitId("1232141");
        reidTaskEntity.setDeviceId("31414123");
        reidTaskEntity.setUserId("dsafafafa");
        reidTaskEntity.setUrl("sdfafaf");
        reidTaskEntity.setReidParamsText("fasdfafaf");
        reidTaskDao.update(reidTaskEntity);
    }

    @Test
    public void delete(){
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(3,4,5));
        reidTaskDao.delete(list);
    }
}

