package com.znv.fssrqs.elasticsearch.person.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.timer.SystemDeviceLoadTask;
import com.znv.fssrqs.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dongzelong on  2019/9/24 10:14.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class PersonDetailService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;

    public JSONObject getPersonDetail(Map<String, Object> requestParams) {
        JSONObject queryJson = getQueryJson(requestParams);
        String url = new StringBuffer().append(CommonConstant.ElasticSearch.INDEX_PERSON_CLUSTER_HISTORY_NAME)
                .append("/")
                .append(CommonConstant.ElasticSearch.INDEX_PERSON_CLUSTER_HISTORY_TYPE)
                .append("/_search?pretty").toString();
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, queryJson);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return getResult(result.value());
    }

    private JSONObject getQueryJson(Map<String, Object> requestParams) {
        String queryStr = "{\"query\": {\"bool\": {\"filter\": [{\"term\": {\"fused_id\": \"%s\"}}]}},\"from\": 0,\"size\": %d,\"sort\": [{\"enter_time\": \"desc\"}, \"_score\"]}";
        queryStr = String.format(queryStr, (String) requestParams.get("FusedID"), Integer.parseInt((String) requestParams.getOrDefault("Size", "8")));
        return JSON.parseObject(queryStr);
    }

    private JSONObject getResult(JSONObject response) {
        final int took = response.getInteger("took").intValue();
        final Integer total = response.getJSONObject("hits").getInteger("total");
        final JSONArray hits = response.getJSONObject("hits").getJSONArray("hits");
        final List<JSONObject> list = hits.parallelStream().map(object -> {
            JSONObject hit = (JSONObject) object;
            final JSONObject source = hit.getJSONObject("_source");
            JSONObject jsonObject = new JSONObject();
            final String smallUuid = (String) source.remove("img_url");
            if (!("null".equals(smallUuid) || StringUtils.isEmpty(smallUuid))) {
                jsonObject.put("SmallPictureUrl", ImageUtils.getImgUrl("", "GetSmallPic", smallUuid));
            } else {
                jsonObject.put("SmallPictureUrl", "");
            }
            jsonObject.put("PersonID", source.remove("person_id"));
            jsonObject.put("LibID", source.remove("lib_id"));
            jsonObject.put("CameraID", source.remove("camera_id"));
            jsonObject.put("EnterTime", source.remove("enter_time"));
            jsonObject.put("OfficeID", source.remove("office_id"));
            jsonObject.put("FusedID", source.remove("fused_id"));
            jsonObject.put("LeaveTime", source.remove("leave_time"));
            jsonObject.put("CameraType", source.remove("camera_type"));
            jsonObject.put("ImgWidth", source.remove("img_width"));
            jsonObject.put("ImgHeight", source.remove("img_height"));
            jsonObject.put("LeftPos", source.remove("left_pos"));
            jsonObject.put("UUID", source.remove("uuid"));
            jsonObject.put("Similarity", source.remove("similarity"));
            jsonObject.put("BigPictureUuid", source.remove("big_picture_uuid"));
            return jsonObject;
        }).collect(Collectors.toList());
        return FastJsonUtils.JsonBuilder.ok().property("Total", total).property("Took", took).list(list).json();
    }

    public JSONObject getTemplateParams(JSONObject requestParams) {
        JSONObject params = new JSONObject();
        params.put("from", 0);
        params.put("size", 0);
        params.put("fused_id_aggregation", true);
        JSONObject templateParams = new JSONObject();
        templateParams.put("id", "template_archive_person_count");
        templateParams.put("params", params);
        return templateParams;
    }

    public JSONObject getPersonDetails(JSONObject requestParams) {
        JSONObject templateParams = getTemplateParams(requestParams);
        String url = new StringBuffer().append(CommonConstant.ElasticSearch.INDEX_PERSON_CLUSTER_HISTORY_NAME)
                .append("/")
                .append(CommonConstant.ElasticSearch.INDEX_PERSON_CLUSTER_HISTORY_TYPE)
                .append("/_search/template").toString();
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, templateParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return processHits(result.value(), requestParams);
    }

    public JSONObject processHits(JSONObject result, JSONObject requestParams) {
        final int took = result.getInteger("took").intValue();
        final Integer total = result.getJSONObject("hits").getInteger("total");
        final JSONArray aggBuckets = result.getJSONObject("aggregations").getJSONObject("group_by_fused").getJSONArray("buckets");
        final List<Object> list = aggBuckets.parallelStream().map(object -> {
            JSONObject jsonObject = (JSONObject) object;
            jsonObject.put("DocCount", jsonObject.remove("doc_count"));
            jsonObject.put("Key", jsonObject.remove("key"));
            final JSONObject hitObject = jsonObject.getJSONObject("camera_person_hits").getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source");
            processHit(hitObject);
            jsonObject.put("Hit", hitObject);
            jsonObject.remove("camera_person_hits");
            return object;
        }).collect(Collectors.toList());

        final Integer currentPage = (Integer) requestParams.getOrDefault("CurrentPage", 0);
        final Integer pageSize = (Integer) requestParams.getOrDefault("PageSize", 10);
        Paging paging = Paging.pagination(list.size(), pageSize, currentPage);
        int fromIndex = paging.getQueryIndex();
        int toIndex = 0;
        if (fromIndex + paging.getPageSize() >= list.size()) {
            toIndex = aggBuckets.size();
        } else {
            toIndex = fromIndex + paging.getPageSize();
        }
        if (fromIndex > toIndex) {
            return FastJsonUtils.JsonBuilder.ok().property("Took", took).property("Total", total).list(Collections.EMPTY_LIST).json();
        }
        return FastJsonUtils.JsonBuilder.ok().property("Took", took).property("Total", list.size()).list(list.subList(fromIndex, toIndex)).json();
    }

    private static JSONObject processHit(JSONObject source) {
        String smallUuid = (String) source.remove("img_url");
        String imgUrl = ImageUtils.getImgUrl(SystemDeviceLoadTask.getMBus().getIP(), "GetSmallPic", smallUuid);
        source.put("SmallPictureUrl", imgUrl);
        source.put("LeaveTime", source.remove("leave_time"));
        source.put("ImgWidth", source.remove("img_width", 0));
        source.put("ImgHeight", source.remove("img_height", 0));
        source.put("CameraID", source.remove("camera_id"));
        source.put("CameraType", source.remove("camera_type"));
        source.put("EnterTime", source.remove("enter_time"));
        source.put("PersonID", source.remove("person_id"));
        source.put("CameraName", source.remove("camera_name"));
        source.put("FusedID", source.remove("fused_id"));
        source.put("OfficeID", source.remove("office_id"));
        source.put("OfficeName", source.remove("office_name"));
        source.put("UUID", source.remove("uuid"));
        source.put("LeftPos", source.remove("left_pos", 0));
        String bigPictureUuid = (String) source.remove("big_picture_uuid");
        if (!("null".equals(bigPictureUuid) || org.springframework.util.StringUtils.isEmpty(bigPictureUuid))) {
            source.put("BigPictureUrl", ImageUtils.getImgUrl(SystemDeviceLoadTask.getMBus().getIP(), "GetBigBgPic", bigPictureUuid));
        } else {
            source.put("BigPictureUrl", "");
        }
        source.put("LibID", source.remove("lib_id"));
        source.put("Similarity", source.remove("similarity"));
        return source;
    }
}
