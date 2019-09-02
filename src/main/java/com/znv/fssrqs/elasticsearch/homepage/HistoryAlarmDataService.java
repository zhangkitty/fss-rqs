package com.znv.fssrqs.elasticsearch.homepage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.EventDao;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.znv.fssrqs.util.FormatObject.formatTime;

/**
 * Created by dongzelong on  2019/8/30 13:42.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
@Slf4j
public class HistoryAlarmDataService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    @Resource
    private EventDao eventDao;

    public JSONObject getHistoryAlarmList(Map<String, Object> requestMap, String remoteIp) {
        Integer size = (Integer) requestMap.getOrDefault("PageSize", 10);
        Integer pageNo = (Integer) requestMap.getOrDefault("CurrentPage", 1);
        int from = ParamUtils.getPageOffset(pageNo, size);
        String queryStr = "{\"_source\":{\"excludes\":[\"rt_feature\",\"op_time\",\"rowkey\",\"leave_time\",\"camera_type\",\"duration_time\",\"track_idx\",\"frame_index\",\"right_pos\",\"task_idx\",\"bottom\",\"birth\"]},\"query\":{\"bool\":{\"filter\":{}}},\"from\": %d,\"size\": %d,\"sort\": [{\"enter_time\":{\"order\":\"desc\"}},{\"person_id\":{\"order\":\"desc\"}}]}";
        String stringParam = String.format(queryStr, from, size);
        JSONObject params = JSON.parseObject(stringParam);
        //查询告警索引
        String url = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_ALARM_NAME) + "/" + HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_ALARM_TYPE) + "/_search";
        Result<JSONObject, String> result = elasticSearchClient.postRequest(url, params);
        JSONObject esAlarmResult = result.value();
        int took = esAlarmResult.getIntValue("took");
        //查询结果总数
        String total = esAlarmResult.getJSONObject("hits").getString("total");
        //查询结果
        JSONArray esAlarmHits = esAlarmResult.getJSONObject("hits").getJSONArray("hits");
        JSONArray alarmList = new JSONArray();
        if (Objects.nonNull(esAlarmHits) && esAlarmHits.size() > 0) {
            StringBuilder personIdList = new StringBuilder("[\"");
            for (int i = 0; i < esAlarmHits.size(); i++) {
                JSONObject source = esAlarmHits.getJSONObject(i).getJSONObject("_source");
                String enterTime = formatTime(source.getString("enter_time"));
                source.put("enter_time", enterTime);
                String personId = source.getString("person_id");
                if (i == (esAlarmHits.size() - 1)) {
                    personIdList.append(personId).append("\"]");
                } else {
                    personIdList.append(personId).append("\",\"");
                }
                alarmList.add(i, source);
            }

            //人员信息遍历
            Map<String, JSONObject> personMap = new HashMap<>();
            Result<JSONObject, String> personResult = this.getPersonList(personIdList.toString(), esAlarmHits.size());
            JSONObject jsonEsResultSecond = personResult.value();
            JSONArray personHits = jsonEsResultSecond.getJSONObject("hits").getJSONArray("hits");
            if (Objects.nonNull(personHits) && personHits.size() > 0) {
                personHits.parallelStream().forEach(object -> {
                    JSONObject source = ((JSONObject) object).getJSONObject("_source");
                    String personId = source.getString("person_id");
                    personMap.put(personId, source);
                });
            }

            //查询事件
            final Map<String, Map<String, Object>> eventMap = eventDao.selectAllMap();
            //人员信息
            alarmList.parallelStream().forEach(object -> {
                JSONObject personObject = (JSONObject) object;
                String personId = personObject.getString("person_id");
                if (personMap.containsKey(personId)) {
                    JSONObject jsonObject = personMap.get(personId);
                    //人员信息
                    personObject.put("CardID", jsonObject.remove("card_id"));
                    personObject.put("Sex", jsonObject.remove("sex"));
                    personObject.put("EyebrowStyle", jsonObject.remove("eyebrow_style"));
                    personObject.put("NoseStyle", jsonObject.remove("nose_style"));
                    personObject.put("MustacheStyle", jsonObject.remove("mustache_style"));
                    personObject.put("LipStyle", jsonObject.remove("lip_style"));
                    personObject.put("WrinklePouch", jsonObject.remove("wrinkle_pouch"));
                    personObject.put("AcneStain", jsonObject.remove("acne_stain"));
                    personObject.put("FreckleBirthmark", jsonObject.remove("freckle_birthmark"));
                    personObject.put("ScarDimple", jsonObject.remove("scar_dimple"));
                    //布控事件ID
                    personObject.put("ControlEventID", personObject.remove("control_event_id"));
                    //布控单位ID
                    personObject.put("ControlCommunityID", jsonObject.remove("control_community_id"));
                    //布控警钟
                    personObject.put("ControlPoliceCategory", jsonObject.remove("control_police_category"));
                    //布控警号
                    personObject.put("ControlPersonID", jsonObject.remove("control_person_id"));
                    //布控人联系方式
                    personObject.put("ControlPersonTel", jsonObject.remove("control_person_tel"));
                    personObject.put("PersonID", personId);
                    personObject.remove("person_id");
                    personObject.put("PersonName", personObject.remove("person_name"));
                    final Map<String, Object> eventNameMap = eventMap.get(personObject.getString("ControlEventID"));
                    if (eventNameMap != null) {
                        personObject.put("EventName", eventNameMap.get(personObject.getString("ControlEventID")));
                    } else {
                        personObject.put("EventName", "");
                    }

                    personObject.put("Similarity", personObject.remove("similarity"));
                    personObject.remove("similarity");
                    String personImg = "";
                    try {
                        personImg = ImageUtils.getImgUrl(remoteIp, "get_fss_personimage", Base64Util.encode(String.format("%s&%s&%s", personId, personObject.getString("lib_id"), UUID.randomUUID()).getBytes("utf-8")));
                    } catch (UnsupportedEncodingException e) {
                        log.error("", e);
                    }
                    personObject.put("PersonImg", personImg);
                    personObject.put("AlarmType", personObject.remove("alarm_type"));
                    personObject.put("LibID", personObject.remove("lib_id"));
                    personObject.put("LibName", personObject.remove("img_url"));
                    personObject.put("OfficeID", personObject.remove("office_id"));
                    personObject.put("OfficeName", personObject.remove("office_name"));
                    personObject.put("CameraId", personObject.remove("camera_id"));
                    personObject.put("CameraName", personObject.remove("camera_name"));
                    personObject.put("ImgWidth", personObject.remove("img_width"));
                    personObject.put("ImgHeight", personObject.remove("img_height"));
                    personObject.put("LeftPos", personObject.remove("left_pos"));
                    personObject.put("Top", personObject.remove("top"));
                    personObject.put("UUID", personObject.remove("uuid"));
                    String smallPictureUuid = (String) personObject.remove("img_url");
                    if (!("null".equals(smallPictureUuid) || StringUtils.isEmpty(smallPictureUuid))) {
                        personObject.put("SmallPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetSmallPic", smallPictureUuid));
                    } else {
                        personObject.put("SmallPictureUrl", "");
                    }
                    personObject.put("EnterTime", personObject.remove("enter_time"));
                    String bigPictureUuid = (String) personObject.remove("big_picture_uuid");
                    if (!("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid))) {
                        personObject.put("BigPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
                    } else {
                        personObject.put("BigPictureUrl", "");
                    }
                }
            });
            took += jsonEsResultSecond.getIntValue("took");
        }
        JSONObject outputResult = new JSONObject();
        outputResult.put("Took", took);
        outputResult.put("Total", total);
        outputResult.put("Hits", alarmList);
        return FastJsonUtils.JsonBuilder.ok().list(alarmList).property("Total", total).property("Took", took).json();
    }


    /**
     * @param personIds
     * @param size
     * @return
     */
    private Result<JSONObject, String> getPersonList(String personIds, int size) {
        String queryStr =
                "{\"_source\":{\"excludes\":[\"feature\",\"control_end_time\",\"create_time\",\"nation\",\"control_start_time\",\"community_name\",\"modify_time\",\"birth\",\"community_id\",\"door_open\"]},\"query\":{\"bool\":{\"filter\":{\"bool\":{\"should\":{\"terms\":{\"person_id\":" +
                        personIds + "}}}}}},\"from\": 0,\"size\":" + size + "}";
        String esUrl = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_PERSON_LIST_NAME) + "/" + HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_PERSON_LIST_TYPE) + "/_search";
        final JSONObject jsonObject = JSON.parseObject(queryStr);
        return elasticSearchClient.postRequest(esUrl, jsonObject);
    }
}
