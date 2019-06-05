package com.znv.fssrqs.service;

import com.znv.fssrqs.entity.UserEntity;
import com.znv.fssrqs.vo.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void findAll() throws Exception {

        List<UserEntity> list= userService.findAll();
         System.out.println("sfda");
    }
}