package com.znv.fssrqs.service;

import com.znv.fssrqs.dao.hbase.HUserDao;
import com.znv.fssrqs.dao.mysql.MUserDao;
import com.znv.fssrqs.entity.hbase.HUserEntity;
import com.znv.fssrqs.entity.mysql.MUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    private MUserDao mUserDao;

    @Autowired
    private HUserDao hUserDao;

    public List<MUserEntity> findAll() throws Exception{
        return mUserDao.findAll();
    }

    public List<HUserEntity> find() throws Exception{
        return hUserDao.findAll();
    }
}
