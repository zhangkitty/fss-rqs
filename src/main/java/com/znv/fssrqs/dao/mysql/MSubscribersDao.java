package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.MSubscriberCameraEntity;
import com.znv.fssrqs.entity.mysql.MSubscriberLibEntity;
import com.znv.fssrqs.entity.mysql.MSubscribersEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MSubscribersDao {
    List<MSubscribersEntity> findAll();

    List<MSubscriberCameraEntity> getSubscriberCamera();

    List<MSubscriberLibEntity> getSubscriberLib();
}
