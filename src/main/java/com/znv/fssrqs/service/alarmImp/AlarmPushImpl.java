package com.znv.fssrqs.service.alarmImp;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.entity.mysql.MSubscribersEntity;
import com.znv.fssrqs.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.07.02 下午4:09
 */
@Slf4j
public class AlarmPushImpl {

    public void service(MSubscribersEntity mSubscribersEntity, List<JSONObject> json) {
        if(json.size()==0)
            return;
        List list = json.stream().filter(new Predicate<JSONObject>() {
            @Override
            public boolean test(JSONObject jsonObject) {
                for(String cameraId:mSubscribersEntity.getCameraIds().split(",")){
                    if(cameraId.equals(jsonObject.get("cameraId"))){
                        return true;
                    }else {
                        return false;
                    }
                }
                return true;
            }
        }).collect(Collectors.toList());
        if(list.size()==0)
            return;
        try {
            HttpUtils.sendPostData(list.toString(),mSubscribersEntity.getAlarmPushUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
