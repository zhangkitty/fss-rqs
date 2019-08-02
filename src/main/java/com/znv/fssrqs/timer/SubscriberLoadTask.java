package com.znv.fssrqs.timer;

import com.znv.fssrqs.dao.mysql.MSubscribersDao;
import com.znv.fssrqs.entity.mysql.MSubscriberCameraEntity;
import com.znv.fssrqs.entity.mysql.MSubscriberLibEntity;
import com.znv.fssrqs.entity.mysql.MSubscribersEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SubscriberLoadTask {

    @Autowired
    MSubscribersDao subscribersDao;

    // Map：订阅者ID -> 订阅者
    private static ConcurrentHashMap<Integer, MSubscribersEntity> subscriberInfoMap = new ConcurrentHashMap();

    // Map：抓拍机ID+订阅者ID -> 订阅者ID
    private static ConcurrentHashMap<String, Integer> subscriberCameraMap = new ConcurrentHashMap();

    // Map：库ID+订阅者ID -> 订阅者ID
    private static ConcurrentHashMap<String, Integer> subscriberLibMap = new ConcurrentHashMap();

    @Scheduled(initialDelay = 10000, fixedRate = 300000)
    public void loadSubscribers(){
        List<MSubscribersEntity> subscriberList = subscribersDao.findAll();
        subscriberInfoMap.clear();
        for (MSubscribersEntity subscribersEntity : subscriberList) {
            subscriberInfoMap.put(subscribersEntity.getSubscriberId(), subscribersEntity);
        }

        List<MSubscriberCameraEntity> subscriberCameraList = subscribersDao.getSubscriberCamera();
        subscriberCameraMap.clear();
        for (MSubscriberCameraEntity subscriberCamera : subscriberCameraList) {
            subscriberCameraMap.put(subscriberCamera.getCameraId() + subscriberCamera.getSubscriberId(), subscriberCamera.getSubscriberId());
        }

        List<MSubscriberLibEntity> subscriberLibList = subscribersDao.getSubscriberLib();
        subscriberLibMap.clear();
        for (MSubscriberLibEntity subscriberLib : subscriberLibList) {
            subscriberLibMap.put(subscriberLib.getLibId() + subscriberLib.getSubscriberId(), subscriberLib.getSubscriberId());
        }
    }

    public static Enumeration<Integer> getSubscriberIds() {
        return subscriberInfoMap.keys();
    }

    public static MSubscribersEntity getSubscriberInfo(Integer subscriberId) {
        return subscriberInfoMap.get(subscriberId);
    }

    public static boolean isSubscribedCamera(String cameraId, Integer subscriberId) {
        return subscriberCameraMap.containsKey(cameraId + subscriberId);
    }

    public static boolean isSubscribedLib(String libId, Integer subscriberId) {
        // libId为-1，表示订阅所有
        if (subscriberLibMap.containsKey("-1" + subscriberId)) {
            return true;
        }
        return subscriberLibMap.containsKey(libId + subscriberId);
    }
}
