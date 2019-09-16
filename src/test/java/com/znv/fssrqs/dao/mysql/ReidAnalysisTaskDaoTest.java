package com.znv.fssrqs.dao.mysql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ReidAnalysisTaskDaoTest {

    @Resource
    private ReidAnalysisTaskDao reidAnalysisTaskDao;

    @Test
    public void getAllDevices() {

        Map<String, Map<String, Object>> map = reidAnalysisTaskDao.getAllDevices();

        System.out.println("mdzz");
    }

    @Test
    public void getDevicesByDeviceIds() {
    }
}