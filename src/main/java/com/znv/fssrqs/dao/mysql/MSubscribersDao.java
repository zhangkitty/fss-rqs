package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.MSubscribersEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface MSubscribersDao {

    ArrayList<MSubscribersEntity> findAll();
}
