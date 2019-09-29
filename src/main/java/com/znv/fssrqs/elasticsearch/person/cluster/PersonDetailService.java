package com.znv.fssrqs.elasticsearch.person.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.ImageUtils;
import com.znv.fssrqs.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
                source.put("SmallPictureUrl", ImageUtils.getImgUrl("", "GetSmallPic", smallUuid));
            } else {
                source.put("SmallPictureUrl", "");
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
}
