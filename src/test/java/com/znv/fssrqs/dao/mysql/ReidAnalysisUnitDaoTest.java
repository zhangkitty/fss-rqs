package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.ReidAnalysisUnitEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReidAnalysisUnitDaoTest {

    @Resource
    private ReidAnalysisUnitDao reidAnalysisUnitDao;

    @Test
    public void findAll() {
        List<ReidAnalysisUnitEntity> list = reidAnalysisUnitDao.findAll();
        System.out.println("mdzz");
    }

    @Test
    public void findOne() {

        ReidAnalysisUnitEntity reidAnalysisUnitEntity = reidAnalysisUnitDao.findOne("11000001901000008");
        System.out.println("sb");
    }
}