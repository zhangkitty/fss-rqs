package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.znv.fssrqs.dao.mysql.LibDao;
import com.znv.fssrqs.elasticsearch.homepage.AlarmTopLibCountService;
import com.znv.fssrqs.elasticsearch.homepage.HistoryAlarmDataService;
import com.znv.fssrqs.util.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by dongzelong on  2019/8/29 15:16.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
@Slf4j
public class HomeController {
    @Autowired
    private AlarmTopLibCountService alarmTopLibCountService;
    @Autowired
    private HistoryAlarmDataService historyAlarmDataService;
    @Resource
    private LibDao libDao;

    /**
     * 南岸库告警总数接口
     */
    @PostMapping({"/area/level/alarm/num"})
    public JSONObject getResourceLibAlarmList(@RequestBody String body) {
        JSONObject params = JSON.parseObject(body);
        JSONObject resultObject = alarmTopLibCountService.getAlarmTopLibCount(params);
        Map<String, Map<String, Object>> libMap = libDao.selectAllMap();
        //聚合结果
        final JSONArray esAgg = resultObject.getJSONObject("aggregations").getJSONObject("lib_ids").getJSONArray("buckets");
        //遍历聚合
        Iterator<Object> iterator = esAgg.iterator();
        Set<String> libIdSet = Sets.newHashSet();
        while (iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            String libId = jsonObject.getString("key");
            libIdSet.add(libId);
            jsonObject.put("LibID", libId);
            jsonObject.remove("key");
            if (libMap.containsKey(libId)) {
                jsonObject.put("LibName", libMap.get(libId).get("LibName"));
            } else {
                jsonObject.put("LibName", "");
            }
            final int docCount = jsonObject.getIntValue("doc_count");
            jsonObject.put("DocCount", docCount);
            jsonObject.remove("doc_count");
        }

        JSONArray array = params.getJSONArray("LibIDs");
        array.forEach(o -> {
            String libId = (String) o;
            if (!libIdSet.contains(libId)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("LibID", libId);
                if (libMap.containsKey(libId)) {
                    jsonObject.put("LibName", libMap.get(libId).get("LibName"));
                } else {
                    jsonObject.put("LibName", "");
                }
                jsonObject.put("DocCount", 0);
                esAgg.add(jsonObject);
            }
        });

        return FastJsonUtils.JsonBuilder.ok().list(esAgg).property("Total", resultObject.getJSONObject("hits").getIntValue("total")).json();
    }

    /**
     * 查询历史告警列表
     */
    @GetMapping({"/person/alarms"})
    public JSONObject getHistoryAlarmList(@RequestHeader("Host") String host, @RequestParam Map<String, Object> params) {
        String remoteIp = host.split(":")[0];
        final JSONObject historyAlarmList = historyAlarmDataService.getHistoryAlarmList(params, remoteIp);
        return historyAlarmList;
//        //查询案件名称
//        Map<String, String> map = getFaceEventList();
//        Map<String, ProtocolNode> libMap = getLibMap();

//        JSONArray hits = esResult.getJSONArray("hits");
//        int length = hits.size();
//        List<JSONObject> list = new ArrayList<>();
//        for (int i = 0; i < length; i++) {
//            JSONObject data = (JSONObject) hits.get(i);
//            //人员ID
//            String personId = data.getString(CommonConsts.FinalKeyCode.PERSON_ID);
//            //人员名称
//            String personName = data.getString(CommonConsts.FinalKeyCode.PERSON_NAME);
//            //事件ID
//            String eventId = data.getString(CommonConsts.FinalKeyCode.CONTROL_EVENT_ID);
//            //事件名称
//            String eventName = "";
//            if (map.containsKey(eventId)) {
//                //事件名称
//                eventName = map.get(eventId);
//            }
//
//            //相似度
//            String similarity = data.getString(CommonConsts.FinalKeyCode.SIMILARITY);
//            //人员库ID
//            int libId = data.getInteger("lib_id");
//            String libName = "";
//            //名单库告警类型
//            String pLibAlarmLevel = "";
//            if (libMap.containsKey(String.valueOf(libId))) {
//                ProtocolNode protocolNode = libMap.get(String.valueOf(libId));
//                libName = protocolNode.getAttribute("lib_name");
//                pLibAlarmLevel = protocolNode.getAttribute("plib_alarm_level");
//            }
//
//            String controlCommunityId = data.getString(CommonConsts.FinalKeyCode.CONTROL_COMMUNITY_ID);
//            String officeId = data.getString("office_id");
//            String officeName = data.getString("office_name");
//            //证件号ID
//            String certificateId = data.getString("card_id");
//            String cameraId = data.getString("camera_id");
//            String imgWidth = data.getString("img_width");
//            String imgHeight = data.getString("img_height");
//            String cameraName = data.getString("camera_name");
//            String enterTime = data.getString("enter_time");

//
//            //告警小图片
//            String smallUuid = data.getString("img_url");
//            String imgUrl = "";
//            if (!("null".equals(smallUuid) || StringUtils.isEmpty(smallUuid))) {
//                imgUrl = ImageUtils.getImgUrl(remoteIp, "GetSmallPic", smallUuid);
//            } else {
//                imgUrl = "";
//            }
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("personId", personId);
//            jsonObject.put("personName", personName);
//            jsonObject.put("eventId", eventId);
//            jsonObject.put("eventName", eventName);
//            jsonObject.put("similarity", similarity);
//            jsonObject.put("personImg", personImg);
//            jsonObject.put("certificateId", certificateId);
//            jsonObject.put("alarmType", pLibAlarmLevel);
//            jsonObject.put("libId", libId);
//            jsonObject.put("libName", libName);
//            jsonObject.put("controlCommunityId", controlCommunityId);
//            jsonObject.put("officeId", officeId);
//            jsonObject.put("officeName", officeName);
//            jsonObject.put("cameraId", cameraId);
//            jsonObject.put("cameraName", cameraName);
//            jsonObject.put("imgWidth", imgWidth);
//            jsonObject.put("imgHeight", imgHeight);
//            jsonObject.put("imgUrl", imgUrl);
//            jsonObject.put("enterTime", enterTime);
//            if (data.containsKey("sex")) {
//                jsonObject.put("sex", data.getString("sex"));
//            }
//
//            // 马拉松看板告警页面临时加字段
//            jsonObject.put(CommonConsts.FinalKeyCode.EYEBROW_STYLE, data.getString(CommonConsts.FinalKeyCode.EYEBROW_STYLE));
//            jsonObject.put(CommonConsts.FinalKeyCode.NOSE_STYLE, data.getString(CommonConsts.FinalKeyCode.NOSE_STYLE));
//            jsonObject.put(CommonConsts.FinalKeyCode.MUSTACHE_STYLE, data.getString(CommonConsts.FinalKeyCode.MUSTACHE_STYLE));
//            jsonObject.put(CommonConsts.FinalKeyCode.LIP_STYLE, data.getString(CommonConsts.FinalKeyCode.LIP_STYLE));
//            jsonObject.put(CommonConsts.FinalKeyCode.WRINKLE_POUCH, data.getString(CommonConsts.FinalKeyCode.WRINKLE_POUCH));
//            jsonObject.put(CommonConsts.FinalKeyCode.ACNE_STAIN, data.getString(CommonConsts.FinalKeyCode.ACNE_STAIN));
//            jsonObject.put(CommonConsts.FinalKeyCode.FRECKLE_BIRTHMARK, data.getString(CommonConsts.FinalKeyCode.FRECKLE_BIRTHMARK));
//            jsonObject.put(CommonConsts.FinalKeyCode.SCAR_DIMPLE, data.getString(CommonConsts.FinalKeyCode.SCAR_DIMPLE));
//            String bigPictureUuid = data.getString("big_picture_uuid");
//            if (!("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid))) {
//                jsonObject.put("BigPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
//            } else {
//                jsonObject.put("BigPictureUrl", "");
//            }
//            list.add(jsonObject);
//        }
//        return FastJsonUtils.JsonBuilder.ok().list(list).property("count", esResult.get("total")).json();
    }
}
