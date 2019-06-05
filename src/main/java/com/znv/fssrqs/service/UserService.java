package com.znv.fssrqs.service;

import com.znv.fssrqs.dao.UserDao;
import com.znv.fssrqs.entity.UserEntity;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public List<UserEntity> findAll() throws Exception{
        return userDao.findAll();
    }
}
