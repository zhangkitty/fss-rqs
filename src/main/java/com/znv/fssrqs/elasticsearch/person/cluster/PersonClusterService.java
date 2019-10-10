package com.znv.fssrqs.elasticsearch.person.cluster;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.service.elasticsearch.trailsearch.ClusterTrachSearchByHit;
import com.znv.fssrqs.timer.SystemDeviceLoadTask;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.FeatureCompUtil;
import com.znv.fssrqs.util.ImageUtils;
import com.znv.fssrqs.util.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by dongzelong on  2019/9/7 14:16.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class PersonClusterService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    @Autowired
    private SystemDeviceLoadTask systemDeviceLoadTask;

    public JSONObject getPersonAggs(JSONObject requestParams) {
        this.checkParams(requestParams);
        JSONObject templateParams = getTemplateParams(requestParams);
        String url = new StringBuffer().append(HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_TYPE_PERSON_CLUSTER_HISTORY))
                .append("/_search/template/").toString();
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, templateParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return getResult(result.value());
    }

    private void checkParams(JSONObject requestParams) {
        if (StringUtils.isEmpty(requestParams.getString("StartTime")) || StringUtils.isEmpty(requestParams.getString("EndTime"))) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeOrEndTimeNotEmpty");
        }

        if (!requestParams.containsKey("OfficeIDs") || !(requestParams.getJSONArray("OfficeIDs") instanceof JSONArray)) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "OfficeIDsTypeError");
        }

        if (!requestParams.containsKey("CameraIDs") || !(requestParams.getJSONArray("CameraIDs") instanceof JSONArray)) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "CameraIDsTypeError");
        }

        if (StringUtils.isEmpty(requestParams.getString("FusedID"))) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "FusedIDEmpty");
        }

        if (requestParams.getString("StartTime").compareTo(requestParams.getString("EndTime")) > 0) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeGtEndTime");
        }
    }

    private JSONObject getTemplateParams(JSONObject requestParams) {
        JSONObject templateParams = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("enter_time_start", requestParams.getString("StartTime"));
        params.put("enter_time_end", requestParams.getString("EndTime"));
        params.put("from", 0);
        params.put("size", requestParams.getIntValue("Size") > 8 ? requestParams.getIntValue("Size") : 8);
        final JSONArray officeIDs = requestParams.getJSONArray("OfficeIDs");
        if (officeIDs.size() > 0) {
            params.put("is_office", true);
            params.put("office_id", officeIDs);
        } else {
            params.put("is_office", false);
        }

        params.put("is_fused", true);
        params.put("fused_id", Collections.singletonList(requestParams.getString("FusedID")));

        final JSONArray cameraIDs = requestParams.getJSONArray("CameraIDs");
        if (cameraIDs.size() > 0) {
            params.put("is_camera", true);
            params.put("camera_id", cameraIDs);
        } else {
            params.put("is_camera", false);
        }

        params.put("date_aggregation", true);
        templateParams.put("id", HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_ARCHIVE_PERSON_COUNT_ID));
        templateParams.put("params", params);
        return templateParams;
    }

    private JSONObject getResult(JSONObject response) {
        final int took = response.getInteger("took").intValue();
        final Integer total = response.getJSONObject("hits").getInteger("total");
        final JSONArray aggBuckets = response.getJSONObject("aggregations").getJSONObject("group_by_date").getJSONArray("buckets");
        //按照小时进行分组
        final Map<String, List<Object>> hourGroupMap = aggBuckets.parallelStream().collect(Collectors.groupingBy(object -> ((JSONObject) object).getString("key_as_string").split("\\s+")[1].substring(0, 2), Collectors.toList()));
        Map<String, JSONObject> bucketsMap = Maps.newHashMap();
        hourGroupMap.forEach((key, list) -> {
            //每天的同一个时刻数据累加
            final AtomicInteger totalCount = new AtomicInteger(0);
            //按照融合ID累加值
            Map<String, JSONObject> map = new HashMap<>();
            list.stream().forEach(object -> {
                JSONObject jsonObject = (JSONObject) object;
                //该时刻总文档数量
                final Integer docCount = jsonObject.getInteger("doc_count");
                final JSONArray fusedAggs = jsonObject.getJSONObject("group_by_fused").getJSONArray("buckets");
                int sum = totalCount.get();
                sum += docCount;
                totalCount.set(sum);
                fusedAggs.stream().forEachOrdered(fusedObject -> {
                    JSONObject fusedJsonObject = (JSONObject) fusedObject;
                    final String fusedId = fusedJsonObject.getString("key");
                    final int docNums = fusedJsonObject.getInteger("doc_count");
                    if (map.containsKey(fusedId)) {
                        final JSONObject tmpFusedObject = map.get(fusedId);
                        int tmpDocNums = tmpFusedObject.getInteger("DocCount").intValue();
                        tmpDocNums += docNums;
                        tmpFusedObject.put("DocCount", tmpDocNums);
                    } else {
                        fusedJsonObject.put("Key", fusedJsonObject.remove("key"));
                        fusedJsonObject.put("DocCount", fusedJsonObject.remove("doc_count"));
                        map.put(fusedId, fusedJsonObject);
                    }
                });
            });
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Total", totalCount.get());
            jsonObject.put("Buckets", map.values());
            bucketsMap.put(key, jsonObject);
        });

        final JSONArray hits = response.getJSONObject("hits").getJSONArray("hits");
        final List<JSONObject> list = processHits(hits);
        JSONObject outResult = new JSONObject();
        outResult.put("Aggs", bucketsMap);
        outResult.put("Hits", list);
        return FastJsonUtils.JsonBuilder.ok().property("Total", total).property("Took", took).object(outResult).json();
    }

    private List<JSONObject> processHits(JSONArray hits) {
        return hits.parallelStream().map(object -> {
            JSONObject jsonObject = ((JSONObject) object).getJSONObject("_source");
            String smallUuid = jsonObject.getString("img_url");
            String imgUrl = ImageUtils.getImgUrl(SystemDeviceLoadTask.getMBus().getIP(), "GetSmallPic", smallUuid);
            //小图地址
            jsonObject.put("SmallPictureUrl", imgUrl);
            String bigPictureUuid = jsonObject.getString("BigPictureUuid");
            if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)) {
                jsonObject.put("BigPictureUrl", "");
            } else {
                jsonObject.put("BigPictureUrl", ImageUtils.getImgUrl(SystemDeviceLoadTask.getMBus().getIP(), "GetBigBgPic", bigPictureUuid));
            }
            jsonObject.put("LeaveTime", jsonObject.remove("leave_time"));
            jsonObject.put("ImgWidth", jsonObject.remove("img_width"));
            jsonObject.put("PersonID", jsonObject.remove("person_id"));
            jsonObject.put("ImgHeight", jsonObject.remove("img_height"));
            jsonObject.put("CameraID", jsonObject.remove("camera_id"));
            jsonObject.put("CameraType", jsonObject.remove("camera_type"));
            jsonObject.put("EnterTime", jsonObject.remove("enter_time"));
            jsonObject.put("UUID", jsonObject.remove("uuid"));
            jsonObject.put("PersonID", jsonObject.remove("person_id"));
            jsonObject.put("OfficeID", jsonObject.remove("office_id"));
            jsonObject.put("LibID", jsonObject.remove("lib_id"));
            jsonObject.put("CameraType", jsonObject.remove("camera_type"));
            jsonObject.put("LeftPos", jsonObject.remove("left_pos"));
            jsonObject.put("Similarity", jsonObject.remove("similarity"));
            jsonObject.put("LeftPos", jsonObject.remove("left_pos"));
            jsonObject.put("FusedID", jsonObject.remove("fused_id"));
            return jsonObject;
        }).collect(Collectors.toList());
    }


    public Result<JSONObject, String>  getPersonTask (JSONObject requestParams){
        this. paramsCheck(requestParams);
        JSONObject params=getParams(requestParams);
        FeatureCompUtil featureCompUtil = new FeatureCompUtil();

        String indexTemplateUrl = new StringBuffer().append(HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_PERSON_CLUSTER))
                .append("/_search/template/").toString();
        Result<JSONObject, String> resultObject = elasticSearchClient.postRequest(indexTemplateUrl,params);
        if (resultObject.isErr()) {
            return resultObject;
        }
        JSONObject jsonEsResult = resultObject.value();
        if (jsonEsResult.getBoolean("timed_out")) {
            return Result.err("QueryEsTimeout");
        }

        JSONArray esHits = jsonEsResult.getJSONObject("hits").getJSONArray("hits");
        List<ClusterTrachSearchByHit> outHits = new ArrayList<>();
        featureCompUtil.setFeaturePoints(HdfsConfigManager.getPoints());
        for (int i = 0; i < esHits.size(); i++) {
            ClusterTrachSearchByHit outHit = new ClusterTrachSearchByHit();
            JSONObject hit = esHits.getJSONObject(i);
            float score = hit.getFloatValue("_score");
            JSONObject source = hit.getJSONObject("_source");
            //若某人从摄像头A->摄像头B，然后出去了，又回到摄像头B，则只返回摄像头A和B，摄像头A下1次，摄像头B下面2次；
            //若某人从摄像头A->摄像头B->摄像头A，则返回摄像头A,B,A，分别各一次
            if (i >= 1 && (source.getString("camera_id").equals(outHits.get(outHits.size() - 1).getCameraId()))) {
                outHit = outHits.get(outHits.size() - 1);
                //当前摄像机逗留次数+1
                outHit.setStayNum(outHit.getStayNum() + 1);
                outHit.setEnterTime(source.getString("enter_time"));
                outHit.setLeaveTime(source.getString("leave_time"));
                String gpsXy = source.getString("gps_xy");
                if (Objects.nonNull(gpsXy)) {
                    String[] gps = gpsXy.split(",");
                    outHit.setGpsy(gps[0]);
                    outHit.setGpsx(gps[1]);
                }
                outHit.setSmallPictureUrl(source.getString("img_url"));
                outHit.setBigPictureUuid(source.getString("big_picture_uuid"));
                outHit.setUuid(source.getString("uuid"));
                outHit.setLibId(source.getInteger("lib_id"));
                outHit.setPersonId(source.getString("person_id"));
                String scoreStr = String.valueOf(score);
                //获取最后一位数字为图片编号
                outHit.setImageNo((int) ((Double.parseDouble("0." + scoreStr.substring(scoreStr.length() - 1)) * 10)));
                outHit.setScore(featureCompUtil.Normalize(score));
            } else {
                String scoreStr = String.valueOf(score);
                outHit.setImageNo((int) ((Double.parseDouble("0." + scoreStr.substring(scoreStr.length() - 1)) * 10)));
                outHit.setScore(featureCompUtil.Normalize(score));
                outHit.setCameraId(source.getString("camera_id"));
                String gpsXy = source.getString("gps_xy");
                if (gpsXy != null) {
                    String[] gps = gpsXy.split(",");
                    outHit.setGpsy(gps[0]);
                    outHit.setGpsx(gps[1]);
                }
                outHit.setEnterTime(source.getString("enter_time"));
                outHit.setLeaveTime(source.getString("leave_time"));
                outHit.setPersonId(source.getString("person_id"));
                outHit.setLibId(source.getInteger("lib_id"));
                outHit.setSmallPictureUrl(source.getString("img_url"));
                outHit.setBigPictureUuid(source.getString("big_picture_uuid"));
                outHit.setUuid(source.getString("uuid"));
                outHit.setStayNum(1);
                outHits.add(outHit);
            }
        }
        int total = jsonEsResult.getJSONObject("hits").getInteger("total");
        int took = jsonEsResult.getInteger("took");
        JSONObject resObject = new JSONObject();
        resObject.put("Total", total);
        resObject.put("Took", took);
        resObject.put("Hits", outHits);
        return Result.ok(resObject);
    }

    private JSONObject getParams(JSONObject requestParams) {
        JSONObject templateParams = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("enter_time_start", requestParams.getString("StartTime"));
        params.put("enter_time_end",requestParams.getString("EndTime"));
        params.put("from", 0);
        params.put("size", 100);
        params.put("is_office", false);
        params.put("office_id", new ArrayList<>());

        params.put("date_aggregation", false);
        params.put("is_fused", true);
        params.put("fused_id", requestParams.getJSONArray("FusedID"));

        params.put("is_camera", false);
        params.put("camera_id", new ArrayList<>());

        params.put("is_sort",true);
        params.put("sortField","enter_time");
        params.put("sortOrder","desc");
        templateParams.put("id", "template_archive_person_count");
        templateParams.put("params", params);
        return templateParams;
    }

    private void paramsCheck(JSONObject requestParams) {
        if (StringUtils.isEmpty(requestParams.getString("StartTime")) || StringUtils.isEmpty(requestParams.getString("EndTime"))) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeOrEndTimeNotEmpty");
        }

        if (StringUtils.isEmpty(requestParams.getString("FusedID"))) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "FusedIDEmpty");
        }

        if (requestParams.getString("StartTime").compareTo(requestParams.getString("EndTime")) > 0) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeGtEndTime");
        }
    }
}
