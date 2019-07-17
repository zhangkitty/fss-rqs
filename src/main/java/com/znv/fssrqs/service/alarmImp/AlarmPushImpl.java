package com.znv.fssrqs.service.alarmImp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.entity.mysql.MSubscribersEntity;
import com.znv.fssrqs.service.personnel.management.PersonListService;
import com.znv.fssrqs.timer.SubscriberLoadTask;
import com.znv.fssrqs.util.HttpUtils;
import com.znv.fssrqs.util.ImageUtils;
import com.znv.fssrqs.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.07.02 下午4:09
 */
@Slf4j
public class AlarmPushImpl {

    public void service(List<JSONObject> jsonList) {
        if (jsonList.isEmpty()) {
            return;
        }

        Enumeration<Integer> subscriberIds = SubscriberLoadTask.getSubscriberIds();
        while (subscriberIds.hasMoreElements()) {
            Integer subscriberId = subscriberIds.nextElement();
            MSubscribersEntity subscribersEntity = SubscriberLoadTask.getSubscriberInfo(subscriberId);
            JSONArray dispositionNotificationList = buildAlarmObject(subscriberId, jsonList);
            if (! dispositionNotificationList.isEmpty()) {
                JSONObject notification = new JSONObject();
                notification.put("DispositionNotificationList", dispositionNotificationList);
                try {
                    HttpUtils.sendPostData(notification.toJSONString(),
                            subscribersEntity.getAlarmPushUrl());
                    log.info("alarm push {}: {}", subscribersEntity.getAlarmPushUrl(), notification.toJSONString());
                } catch (IOException e) {
                    log.error("sendPostData failed! {}", subscribersEntity.getAlarmPushUrl());
                }
            }
        }
    }

    private JSONArray buildAlarmObject(Integer subscriberId, List<JSONObject> jsonList) {
        JSONArray dispositionNotificationList = new JSONArray();
        for (JSONObject jsonObject : jsonList) {
            if (! jsonObject.containsKey("isAlarm")
                    || jsonObject.getIntValue("isAlarm") != 1) {
                continue;
            }

            if (! jsonObject.containsKey("cameraId") ||
                    ! jsonObject.containsKey("libId")
            ) {
                continue;
            }

            boolean subscribedCamera = SubscriberLoadTask.isSubscribedCamera(
                    jsonObject.getString("cameraId"), subscriberId);
            boolean subscribedLib = SubscriberLoadTask.isSubscribedLib(
                    jsonObject.getString("libId"), subscriberId);
            if (! subscribedCamera || !subscribedLib) {
                continue;
            }

            JSONObject dispositionNotification = new JSONObject();
            if (jsonObject.containsKey("taskIdx") && jsonObject.containsKey("trackIdx")) {
                dispositionNotification.put("NotificationID",
                        jsonObject.getString("taskIdx") + jsonObject.getString("trackIdx"));
            }
            if (jsonObject.containsKey("opTime")) {
                dispositionNotification.put("TriggerTime", jsonObject.getString("opTime"));
            }
            if (jsonObject.containsKey("cameraName")) {
                dispositionNotification.put("DeviceName", jsonObject.getString("cameraName"));
            }
            if (jsonObject.containsKey("bigPictureUuid")) {
                String bigPictureUrl = ImageUtils.getImgUrl(null, "GetDHPicUrl", jsonObject.getString("bigPictureUuid"));
                dispositionNotification.put("BigPictureUrl", bigPictureUrl);
            }
            if (jsonObject.containsKey("imgUrl")) {
                dispositionNotification.put("SmallPictureUrl", ImageUtils.getImgUrl(null, "GetSmallPic", jsonObject.getString("imgUrl")));
            }

            if (jsonObject.containsKey("similarity")) {
                dispositionNotification.put("Similarity", jsonObject.getDoubleValue("similarity"));
            }
            JSONObject person = new JSONObject();
            if (jsonObject.containsKey("personId")
                    && jsonObject.containsKey("libId")) {
                PersonListService personListService = SpringContextUtil.getCtx().getBean(PersonListService.class);
                person = personListService.getPerson("127.0.0.1",
                        jsonObject.getString("libId"),
                        jsonObject.getString("personId"));
                if (person != null && person.getJSONObject("PersonObject") != null) {
                    person = person.getJSONObject("PersonObject");
                    person.remove("Feature");
                }
            }
            dispositionNotification.put("Person", person);
            dispositionNotificationList.add(dispositionNotification);
        }

        return dispositionNotificationList;
    }
}
