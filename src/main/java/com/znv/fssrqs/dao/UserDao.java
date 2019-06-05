package com.znv.fssrqs.dao;

import com.znv.fssrqs.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    List<UserEntity> findAll();
}
