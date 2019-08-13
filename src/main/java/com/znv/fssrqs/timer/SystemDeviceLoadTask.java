package com.znv.fssrqs.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.MDeviceDao;
import com.znv.fssrqs.entity.mysql.*;
import com.znv.fssrqs.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class SystemDeviceLoadTask {

    @Autowired
    MDeviceDao deviceDao;

    // 在线缓冲服务器列表
    private static List<MBusEntity> mBusOnlineList = new CopyOnWriteArrayList<>();
    // 离线缓冲服务器
    private static List<MBusEntity> mBusOfflineList = new CopyOnWriteArrayList<>();

    // 在线静态分析单元列表
    private static List<AnalysisUnitEntity> staticAIUnitOnlineList = new CopyOnWriteArrayList<>();
    // 离线静态分析单元列表
    private static List<AnalysisUnitEntity> staticAIUnitOfflineList = new CopyOnWriteArrayList<>();

    private static int count = 1;
    private static boolean firstLoad = true;

    @Scheduled(fixedRateString = "${conf.encoderState.reportInterval:30000}")
    public void loadOrCheckSystemDevices(){
        if (firstLoad) {
            firstLoad = false;
            loadMBus();
            loadStaticAIUnit();
            return;
        }

        // 定期重新加载
        if (count++ % 100 == 0) {
            loadAndCheckMBus();
            loadAndCheckStaticAIUnit();
        } else {
            checkMBus();
            checkStaticAIUnit();
        }
    }

    private void loadMBus() {
        // 重新加载所有
        mBusOfflineList.clear();
        List<MBusEntity> mBusList = deviceDao.getMBus();
        mBusOnlineList.clear();
        mBusOnlineList.addAll(mBusList);

        // 遍历每个设备，在线的保留，离线的写mBusOfflineList
        Iterator<MBusEntity> iterator = mBusOnlineList.iterator();
        while (iterator.hasNext()) {
            MBusEntity mbus = iterator.next();
            if (mbus.getLoginState() == null || mbus.getLoginState() != 1) {
                mBusOfflineList.add(mbus);
                mBusOnlineList.remove(mbus);
            }
        }

        log.info("load mBus, online {}, offline {}.", mBusOnlineList.size(), mBusOfflineList.size());
    }

    private void loadAndCheckMBus() {
        // 重新加载所有
        mBusOfflineList.clear();
        List<MBusEntity> mBusList = deviceDao.getMBus();
        mBusOnlineList.clear();
        mBusOnlineList.addAll(mBusList);

        // 遍历每个设备，在线的保留，离线的写mBusOfflineList
        Iterator<MBusEntity> iterator = mBusOnlineList.iterator();
        while (iterator.hasNext()) {
            MBusEntity mbus = iterator.next();
            if (! checkMbusOnline(mbus)) {
                mBusOfflineList.add(mbus);
                mBusOnlineList.remove(mbus);
            }
        }

        log.info("reload mBus, online {}, offline {}.", mBusOnlineList.size(), mBusOfflineList.size());
    }


    private void checkMBus() {
        boolean isDeviceStateChanged = false;
        List<MBusEntity> newOfflineList = new ArrayList<>();

        // 遍历在线设备
        Iterator<MBusEntity> iterator = mBusOnlineList.iterator();
        while (iterator.hasNext()) {
            MBusEntity mbus = iterator.next();
            if (! checkMbusOnline(mbus)) {
                // 设备从在线变成离线
                newOfflineList.add(mbus);
                mBusOnlineList.remove(mbus);
                isDeviceStateChanged = true;
            }
        }

        // 遍历离线设备
        iterator = mBusOfflineList.iterator();
        while (iterator.hasNext()) {
            MBusEntity mbus = iterator.next();
            if (checkMbusOnline(mbus)) {
                // 设备从离线变成在线
                mBusOnlineList.add(mbus);
                mBusOfflineList.remove(mbus);
                isDeviceStateChanged = true;
            }
        }

        // 将新离线的设备加入离线列表
        if (! newOfflineList.isEmpty()) {
            mBusOfflineList.addAll(newOfflineList);
        }

        if (isDeviceStateChanged) {
            log.info("mBus changed, online {}, offline {}.", mBusOnlineList.size(), mBusOfflineList.size());
        }
    }

    private void loadStaticAIUnit() {
        // 重新加载所有
        staticAIUnitOfflineList.clear();
        List<AnalysisUnitEntity> staticAIUnitList = deviceDao.getStaticAnalysisUnit();
        staticAIUnitOnlineList.clear();
        staticAIUnitOnlineList.addAll(staticAIUnitList);

        // 遍历每个设备，在线的保留，离线转staticAIUnitOfflineList
        Iterator<AnalysisUnitEntity> iterator = staticAIUnitOnlineList.iterator();
        while (iterator.hasNext()) {
            AnalysisUnitEntity staticAIUnit = iterator.next();
            if (staticAIUnit.getLoginState() == null || staticAIUnit.getLoginState() != 0) {
                staticAIUnitOfflineList.add(staticAIUnit);
                staticAIUnitOnlineList.remove(staticAIUnit);
            }
        }

        log.info("load AIUnit, online {}, offline {}.", staticAIUnitOnlineList.size(), staticAIUnitOfflineList.size());
    }

    private void loadAndCheckStaticAIUnit() {
        // 重新加载所有
        staticAIUnitOfflineList.clear();
        List<AnalysisUnitEntity> staticAIUnitList = deviceDao.getStaticAnalysisUnit();
        staticAIUnitOnlineList.clear();
        staticAIUnitOnlineList.addAll(staticAIUnitList);

        // 遍历每个设备，在线的保留，离线转staticAIUnitOfflineList
        Iterator<AnalysisUnitEntity> iterator = staticAIUnitOnlineList.iterator();
        while (iterator.hasNext()) {
            AnalysisUnitEntity staticAIUnit = iterator.next();
            if (! checkAIUnitOnline(staticAIUnit)) {
                staticAIUnitOfflineList.add(staticAIUnit);
                staticAIUnitOnlineList.remove(staticAIUnit);
            }
        }

        log.info("reload AIUnit, online {}, offline {}.", staticAIUnitOnlineList.size(), staticAIUnitOfflineList.size());
    }

    private void checkStaticAIUnit() {
        boolean isDeviceStateChanged = false;
        List<AnalysisUnitEntity> newOfflineList = new ArrayList<>();

        // 遍历在线设备
        Iterator<AnalysisUnitEntity> iterator = staticAIUnitOnlineList.iterator();
        while (iterator.hasNext()) {
            AnalysisUnitEntity analysisUnit = iterator.next();
            if (! checkAIUnitOnline(analysisUnit)) {
                // 设备从在线变成离线
                newOfflineList.add(analysisUnit);
                staticAIUnitOnlineList.remove(analysisUnit);
                isDeviceStateChanged = true;
            }
        }

        // 遍历离线设备
        iterator = staticAIUnitOfflineList.iterator();
        while (iterator.hasNext()) {
            AnalysisUnitEntity analysisUnit = iterator.next();
            if (checkAIUnitOnline(analysisUnit)) {
                // 设备从离线变成在线
                staticAIUnitOnlineList.add(analysisUnit);
                staticAIUnitOfflineList.remove(analysisUnit);
                isDeviceStateChanged = true;
            }
        }

        // 将新离线的设备加入离线列表
        if (! newOfflineList.isEmpty()) {
            staticAIUnitOfflineList.addAll(newOfflineList);
        }

        if (isDeviceStateChanged) {
            log.info("AIUnit changed, online {}, offline {}.", staticAIUnitOnlineList.size(), staticAIUnitOfflineList.size());
        }
    }

    private boolean checkMbusOnline(MBusEntity mbus) {
        try {
            HttpUtils.sendGet(String.format("http://%s:%s", mbus.getPrivateIP(), mbus.getPort()));
        } catch (ParseException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean checkAIUnitOnline(AnalysisUnitEntity analysisUnit) {
        try {
            String data = HttpUtils.sendGet(String.format("http://%s:%s/%s",
                    analysisUnit.getIP(), analysisUnit.getPort(), "verify/detail"));
            JSONObject obj = JSON.parseObject(data);
            String result = obj.getString("result");
            if (! "success".equals(result)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static MBusEntity getMBus() {
        if (mBusOnlineList.isEmpty()) {
            return null;
        }

        Random r = new Random(System.currentTimeMillis());
        int i = r.nextInt(mBusOnlineList.size());
        return mBusOnlineList.get(i);
    }

    public static AnalysisUnitEntity getStaticAIUint() {
        if (staticAIUnitOnlineList.isEmpty()) {
            return null;
        }

        Random r = new Random(System.currentTimeMillis());
        int i = r.nextInt(staticAIUnitOnlineList.size());
        return staticAIUnitOnlineList.get(i);
    }
}