package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.CompareTaskEntity;
import com.znv.fssrqs.param.face.compare.n.n.NToNCompareTaskParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CompareTaskDaoTest {

    @Resource
    private CompareTaskDao compareTaskDao;

    @Test
    public void findAllCompareTask() {

        List<CompareTaskEntity> list   = compareTaskDao.findAllCompareTask();

        System.out.println("mdzz");
    }

    @Test
    public void update(){
        CompareTaskEntity compareTaskEntity = new CompareTaskEntity();
        compareTaskEntity.setTaskId("EAA4EAD9A55ADBE2F79C8638CF643FD9");
        compareTaskEntity.setProcess(1f);
        compareTaskEntity.setStatus(17);
        compareTaskDao.update(compareTaskEntity);

    }

    @Test
    public void save(){
        NToNCompareTaskParam nToNCompareTaskParam = new NToNCompareTaskParam();
        nToNCompareTaskParam.setTaskId("sdafafafasf");
        nToNCompareTaskParam.setLib1(1);
        nToNCompareTaskParam.setLib2(2);
        nToNCompareTaskParam.setStatus(3);
        nToNCompareTaskParam.setCreateUser("11000000000");
        nToNCompareTaskParam.setRemainningTime(0l);
        nToNCompareTaskParam.setProcess(1f);
        nToNCompareTaskParam.setSim(0.7f);
        nToNCompareTaskParam.setLib1Name("cc_test");
        nToNCompareTaskParam.setLib1Name("cc_test");
        compareTaskDao.save(nToNCompareTaskParam);

    }
}