package com.znv.fssrqs.service.elasticsearch.history.alarm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.*;
import com.znv.fssrqs.vo.SearchRetrieval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by dongzelong on  2019/6/26 10:58.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 历史告警数据
 */
@Service
public class HistoryAlarmService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;

    /**
     * 战果统计查询
     *
     * @param host
     * @param searchRetrieval
     * @return
     */
    public String getAllByCondition(String host, SearchRetrieval searchRetrieval) {
        //检查参数是否合法
        Result<String, String> checkResult = checkParam(searchRetrieval);
        if (checkResult.isErr()) {
            throw new RuntimeException(checkResult.error());
        }
        JSONObject searchResult = getSearchResult(searchRetrieval);
        JSONArray jsonArray = searchResult.getJSONArray("Hits");
        int length = jsonArray.size();

        //这个没有用了
        String remoteIp = "";

        for (int i = 0; i < length; i++) {
            JSONObject data = (JSONObject) jsonArray.get(i);
            String personId = data.getString("PersonID");
            int libId = data.getInteger("LibID");
            String optTime = data.getString("OpTime");
            String alarmTYpe = data.getString("AlarmType");
            // 获取名单库图片
            String paramstr = String.format("%s&%s&%s&%s", personId, libId, alarmTYpe, optTime);
            data.put("PersonImg", ImageUtils.getImgUrl(remoteIp, "get_fss_personimage", Base64Util.encodeString(paramstr)));
            // 获取告警图片
            String smallUuid = data.getString("ImgUrl");
            String imgUrl = ImageUtils.getImgUrl(remoteIp, "GetSmallPic", smallUuid);
            data.put("SmallPictureUrl", imgUrl);
            // 大图
            String bigPictureUuid = data.getString("BigPictureUuid");
            if (!("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid))) {
                data.put("BigPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
            } else {
                data.put("BigPictureUrl", "");
            }
        }
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().list(jsonArray).property("Total", searchResult.getIntValue("Total"))
                .property("Size", searchResult.getJSONArray("Hits").size()).json(), new PascalNameFilter());
    }

    public JSONObject getSearchResult(SearchRetrieval searchRetrieval) {
        JSONObject templateParams = getTemplateParams(searchRetrieval);
        String esUrl = EsBaseConfig.getInstance().getIndexAlarmName() + "/" + EsBaseConfig.getInstance().getIndexAlarmType() + "/_search/template";
        Result<JSONObject, String> result = elasticSearchClient.postRequest(esUrl, templateParams);
        if (result.isErr()) {
            throw new RuntimeException("查询es数据失败:" + result.value());
        }
        JSONObject jsonEsResult = result.value();
        if (jsonEsResult.get("error") != null) {
            throw new RuntimeException("查询es数据异常");
        }

        if (jsonEsResult.getBoolean("timed_out")) {
            throw new RuntimeException("查询es超时");
        }

        // 从es查询结果中获取hits
        JSONArray esHits = jsonEsResult.getJSONObject("hits").getJSONArray("hits");
        int containerNum = esHits.size() / 3 * 4 + 1;
        Map<String, JSONObject> personMap = new HashMap<>(containerNum);
        // 查询告警中用户信息
        int secondTook = getPersonInfo(esHits, personMap);
        if (secondTook == -1) {
            throw new RuntimeException("查询es人员信息失败");
        }

        JSONObject jsonObject = new JSONObject();
        List<HistoryAlarmHit> outHits = setHitParams(esHits, personMap);
        int total = jsonEsResult.getJSONObject("hits").getInteger("total");
        int took = jsonEsResult.getInteger("took");
        jsonObject.put("Total", total);
        jsonObject.put("Hits", outHits);
        jsonObject.put("Took", took + secondTook);
        return jsonObject;
    }

    private JSONObject getTemplateParams(SearchRetrieval searchRetrieval) {
        List<String> featureValues = new ArrayList<>();
        if (Objects.nonNull(searchRetrieval.getFeatures())) {
            for (String img : searchRetrieval.getFeatures()) {
                String feature = FaceAIUnitUtils.getImageFeature(img);
                JSONObject featureJson = JSON.parseObject(feature);
                if (!"success".equalsIgnoreCase(featureJson.getString("result"))) {
                    throw new RuntimeException("访问商汤服务器失败");
                }
                featureValues.add(featureJson.getString("feature"));
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enter_time_start", searchRetrieval.getStartTime());
        jsonObject.put("enter_time_end", searchRetrieval.getEndTime());
        jsonObject.put("feature_name", "rt_feature.feature_high");
        FeatureCompUtil fc = new FeatureCompUtil();
        fc.setFeaturePoints(HdfsConfigManager.getPoints());
        float sim = (float) searchRetrieval.getSimilarityDegree();
        jsonObject.put("sim_threshold", fc.reversalNormalize(sim));//脚本中未归一化
        jsonObject.put("from", ParamUtils.getPageOffset(searchRetrieval.getCurrentPage(), searchRetrieval.getPageSize()));
        jsonObject.put("size", searchRetrieval.getPageSize());
        jsonObject.put("sortField", searchRetrieval.getSortField());
        jsonObject.put("sortOrder", searchRetrieval.getSortOrder());
        int minShouldMatch = 0;
        if (Objects.isNull(searchRetrieval.getOfficeIDs())) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "区域字段为空");
        }

        if (searchRetrieval.getOfficeIDs() != null && !searchRetrieval.getOfficeIDs().isEmpty()) {
            jsonObject.put("office_id", searchRetrieval.getOfficeIDs());
            jsonObject.put("is_office", true);
            minShouldMatch = 1;
        }

        if (Objects.isNull(searchRetrieval.getCameraIDs())) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "摄像机ID字段为空");
        }

        if (searchRetrieval.getCameraIDs() != null && !searchRetrieval.getCameraIDs().isEmpty()) {
            jsonObject.put("camera_id", searchRetrieval.getCameraIDs());
            jsonObject.put("is_camera", true);
            minShouldMatch = 1;
        }

        if (Objects.isNull(searchRetrieval.getEventIDs())) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "事件ID字段为空");
        }

        if (searchRetrieval.getEventIDs() != null && !searchRetrieval.getEventIDs().isEmpty()) {
            jsonObject.put("control_event_id", searchRetrieval.getEventIDs());
            jsonObject.put("is_control_event", true);
            minShouldMatch = 1;
        }

        if (minShouldMatch != 0) {
            jsonObject.put("minimum_should_match", minShouldMatch);
        }

        if (searchRetrieval.isIsCalcSim()) {
            jsonObject.put("is_calcSim", searchRetrieval.isIsCalcSim());
            jsonObject.put("feature_value", featureValues);
        }

        JSONObject obj = new JSONObject();
        obj.put("id", EsBaseConfig.getInstance().getAlarmSearchTemplateName());
        obj.put("params", jsonObject);
        return obj;
    }

    private List<HistoryAlarmHit> setHitParams(JSONArray esHits, Map<String, JSONObject> personMap) {
        List<HistoryAlarmHit> outHits = new ArrayList<>(esHits.size());
        FeatureCompUtil fc = new FeatureCompUtil();
        fc.setFeaturePoints(HdfsConfigManager.getPoints());
        for (int i = 0; i < esHits.size(); i++) {
            HistoryAlarmHit outHit = new HistoryAlarmHit();
            JSONObject hit = esHits.getJSONObject(i);
            float score = hit.getFloatValue("_score");
            outHit.setScore(fc.Normalize(score)); // 归一化
            JSONObject source = hit.getJSONObject("_source"); // 从hits数组中获取_source
            outHit.setBigPictureUuid(source.getString("big_picture_uuid"));
            outHit.setImgUrl(source.getString("img_url"));
            outHit.setCameraId(source.getString("camera_id"));
            outHit.setCameraName(source.getString("camera_name"));
            outHit.setOfficeId(source.getString("office_id"));
            outHit.setOfficeName(source.getString("office_name"));
            outHit.setEnterTime(DataConvertUtils.parseDateStr2DateStr(source.getString("enter_time")));
            outHit.setLeaveTime(DataConvertUtils.parseDateStr2DateStr(source.getString("leave_time")));
            outHit.setOpTime(DataConvertUtils.parseDateStr2DateStr(source.getString("op_time")));
            outHit.setPersonName(source.getString("person_name"));
            outHit.setPersonId(source.getString("person_id"));
            outHit.setControlEventId(source.getString("control_event_id"));
            outHit.setBirth(source.getString("birth"));
            outHit.setUuid(source.getString("uuid"));
            if (source.containsKey("lib_id")) {
                outHit.setLibId(source.getInteger("lib_id"));
            }
            if (source.containsKey("alarm_type")) {
                outHit.setAlarmType(source.getInteger("alarm_type"));
            }
            if (source.containsKey("similarity")) {
                outHit.setSimilarity(source.getFloatValue("similarity"));
            }

            if (source.containsKey("img_width")) {
                outHit.setImgWidth(source.getIntValue("img_width"));
            }
            if (source.containsKey("img_height")) {
                outHit.setImgHeight(source.getIntValue("img_height"));
            }
            if (source.containsKey("left_pos")) {
                outHit.setLeftPos(source.getIntValue("left_pos"));
            }
            if (source.containsKey("right_pos")) {
                outHit.setRightPos(source.getIntValue("right_pos"));
            }
            if (source.containsKey("top")) {
                outHit.setTop(source.getIntValue("top"));
            }
            if (source.containsKey("bottom")) {
                outHit.setBottom(source.getIntValue("bottom"));
            }
            JSONObject personInfo = personMap.get(outHit.getPersonId());
            if (personInfo != null && !personInfo.isEmpty()) {
                //设置监控人信息
                outHit.setControlCommunityId(personInfo.getString("control_community_id"));
                outHit.setControlPersonId(personInfo.getString("control_person_id"));
                outHit.setControlPoliceCategory(personInfo.getString("control_police_category"));
                outHit.setControlPersonTel(personInfo.getString("control_person_tel"));
                outHit.setControlPersonName(personInfo.getString("control_person_name"));
                outHit.setControlStartTime(personInfo.getString("control_start_time"));
                outHit.setControlEndTime(personInfo.getString("control_end_time"));
                outHit.setComment(personInfo.getString("comment"));
                outHit.setBelongPoliceStation(personInfo.getString("belong_police_station"));
            }
            outHits.add(outHit);
        }
        return outHits;
    }

    /**
     * 查询告警中用户信息
     *
     * @author Chenfei 2019/4/30
     */
    private int getPersonInfo(JSONArray esHits, Map<String, JSONObject> personMap) {
        Set<String> personList = new HashSet<>(personMap.size());
        for (int i = 0; i < esHits.size(); i++) {
            JSONObject hit = esHits.getJSONObject(i);
            JSONObject source = hit.getJSONObject("_source");
            personList.add(source.getString("person_id"));
        }
        String queryJsonStr = "{\"query\":{\"constant_score\":{\"filter\":{\"terms\":{\"person_id\":" + JSON.toJSONString(personList)
                + "}}}},\"_source\":{\"excludes\":[\"feature\",\"door_open\",\"image_name\",\"rowkey\"]}}";
        String esSearchUrl = EsBaseConfig.getInstance().getIndexPersonListName() + "/" + EsBaseConfig.getInstance().getIndexPersonListType() + "/_search";
        Result<JSONObject, String> result = elasticSearchClient.postRequest(esSearchUrl, JSON.parseObject(queryJsonStr));
        if (result.isErr()) {
            throw new RuntimeException("获取es数据失败:" + result.error());
        }
        JSONObject jsonEsResultSecond = result.value();
        JSONArray esSecondHits = jsonEsResultSecond.getJSONObject("hits").getJSONArray("hits");
        int took = jsonEsResultSecond.getInteger("took");
        if (null != esSecondHits && esSecondHits.size() != 0) {
            for (int i = 0; i < esSecondHits.size(); i++) {
                JSONObject source = esSecondHits.getJSONObject(i).getJSONObject("_source");
                String personId = source.getString("person_id");
                personMap.put(personId, source);
            }
        }
        return took;
    }

    private Result<String, String> checkParam(SearchRetrieval searchRetrieval) {
        if (StringUtils.isEmpty(searchRetrieval.getStartTime())) {
            return Result.err("开始时间不能为空");
        }

        if (StringUtils.isEmpty(searchRetrieval.getEndTime())) {
            return Result.err("结束时间不能为空");
        }

        if (searchRetrieval.getStartTime().compareTo(searchRetrieval.getEndTime()) > 0) {
            return Result.err("开始时间不能大于结束时间");
        }

        if (searchRetrieval.isIsCalcSim()) {
            if (Objects.isNull(searchRetrieval.getFeatures()) || searchRetrieval.getFeatures().isEmpty()) {
                return Result.err("图片不能为空");
            }

            if (searchRetrieval.getSimilarityDegree() < 0.5f) {
                return Result.err("相似度不能低于0.5");
            }
        }

        if (searchRetrieval.getCurrentPage() < 0) {
            return Result.err("当前页码不能小于0");
        }

        if (searchRetrieval.getPageSize() < 1) {
            return Result.err("页码小于1");
        }

        Integer offset = ParamUtils.getPageOffset(searchRetrieval.getCurrentPage(), searchRetrieval.getPageSize());
        if (offset + searchRetrieval.getPageSize() >= 10000) {
            return Result.err("查询范围超出ElasticSearch范围上限");
        }

        return Result.ok("ok");
    }
}
