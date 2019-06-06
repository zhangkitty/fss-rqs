package com.znv.fssrqs.service;

import com.znv.fssrqs.entity.hbase.HUserEntity;
import com.znv.fssrqs.entity.mysql.MUserEntity;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Log4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void findAll() throws Exception {

        List<MUserEntity> list= userService.findAll();
        for(MUserEntity mUserEntity:list){
            System.out.println(mUserEntity.getUserType());
        }

    }

    @Test
    public void find() throws Exception{
        List<HUserEntity> list = userService.find();
        for (HUserEntity hUserEntity:list){
            System.out.println(hUserEntity.getPersonName());
        }
    }
}