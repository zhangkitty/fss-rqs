package com.znv.fssrqs.service.alarmImp;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.MBUSConsts;
import com.znv.fssrqs.service.api.AlarmService;
import com.znv.fssrqs.service.api.IDataPushService;
import com.znv.fssrqs.util.*;
import com.znv.fssrqs.websocket.WebSocketAsSession;
import com.znv.fssrqs.websocket.WebSocketSessionContext;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FaceAlarmImpl implements AlarmService {
    /**
     * 数据推送
     *
     * @param jsonObjectList
     */
    @Override
    public void service(List<JSONObject> jsonObjectList) {
        Stream<JSONObject> stream = jsonObjectList.parallelStream().filter(new Predicate<JSONObject>() {
            @Override
            public boolean test(JSONObject jsonObject) {
                //过滤掉实时比对告警以外的告警数据（小孩独自出门，老人未出门）并且过滤掉任务ID为空的数据
                return jsonObject.getIntValue("alarmType") != 1 && jsonObject.getIntValue("alarmType") != 2 && !StringUtils.isEmpty(jsonObject.getString("taskIdx"));
            }
        });

        List<JSONObject> list = stream.collect(Collectors.toList());
        send(list);
    }

    private static void buildAddr(String remoteHost, JSONObject json) {
        String fcPid = json.getString("fcPid");
        String controlLevel = json.getString("controlLevel");
        String smallUuid = json.getString("imgUrl");
        String imgUrl = ImageUtils.getImgUrl(remoteHost, "GetSmallPic", smallUuid);
        if ("1".equals(json.getString("isAlarm"))) {
            json.put("type", MBUSConsts.FACE_DATA_ALARM); // 告警数据
        } else {
            json.put("type", MBUSConsts.FACE_DATA_NO_ALARM); // 非告警数据
        }
        json.put("imgUrl", imgUrl);
        json.put(
                "personimgUrl",
                ImageUtils.getImgUrl(remoteHost, "get_fss_personimage",
                        Base64Util.encodeString((String.format("%s&%s", fcPid, controlLevel)))));
        json.put("nowstamp", DataConvertUtils.dateToStr(new Date()));
        String bigPictureUuid = json.getString("bigPictureUuid");
        // 如果大图的uuid是空
        if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)) {
            json.put("bigPictureUrl", "");
        } else {
            json.put("bigPictureUrl", ImageUtils.getImgUrl(remoteHost, "GetBigBgPic", bigPictureUuid));
        }
    }

    /**
     * 数据发送处理
     */
    public static void send(List<JSONObject> list) {
        // CaptureDataContext.getInstance().addData(object); // 加入到内存中 为了定时发送抓拍数统计使用
        Map<String, WebSocketAsSession> wssMap = WebSocketSessionContext.getInstance();
        wssMap.forEach((key, wss) -> {
            if (wss == null) {
                return;
            }

            //过滤掉没有订阅数据
            List<JSONObject> filterList = list.parallelStream().filter(new Predicate<JSONObject>() {
                @Override
                public boolean test(JSONObject jsonObject) {
                    return SubscribManager.getInstance().isSubscrib(jsonObject, wss);
                }
            }).collect(Collectors.toList());

            int size = filterList.size();
            for (int index = 0; index < size; index++) {
                //克隆数据
                JSONObject tmpObject = (JSONObject) filterList.get(index).clone();
                //如果是实时告警或者马拉松订阅的链接
                buildAddr(wss.getHostIp(), tmpObject);
                try {
                    //开启线程发送数据
                    ICAPVThreadPool.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<IDataPushService> ips = SubscribManager.getInstance().getIservices();
                                String pageName = wss.get(MBUSConsts.SubscribeParams.FACE_PAGE_NAME) == null ? "" : String.valueOf(wss.get(MBUSConsts.SubscribeParams.FACE_PAGE_NAME));
                                int eventType = wss.get(MBUSConsts.SubscribeParams.FACE_EVENT_TYPE) == null ? 0 : Integer.parseInt(String.valueOf(wss.get(MBUSConsts.SubscribeParams.FACE_EVENT_TYPE)));
                                for (IDataPushService ip : ips) {
                                    if (ip.getPageName().equals(pageName) && ip.getPushEventType() == eventType) {
                                        ip.service(wss, tmpObject);
                                    }
                                }
                            } catch (Exception e) {
                                log.warn("send data to message error", e);
                            }
                        }
                    });
                } catch (Exception e) {
                    log.warn("send data to Browser error,WebSocketSession={}", wss, e);
                }
            }
        });
    }
}
