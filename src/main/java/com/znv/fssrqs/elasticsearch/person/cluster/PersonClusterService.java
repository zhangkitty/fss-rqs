package com.znv.fssrqs.elasticsearch.person.cluster;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public JSONObject getPersonAggs(JSONObject requestParams) {
        JSONObject templateParams = getTemplateParams(requestParams);
        String url = new StringBuffer().append(CommonConstant.ElasticSearch.INDEX_PERSON_CLUSTER_NAME)
                .append("/")
                .append(CommonConstant.ElasticSearch.INDEX_PERSON_CLUSTER_TYPE)
                .append("/_search/template/").toString();
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, templateParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return getResult(result.value());
    }

    private JSONObject getTemplateParams(JSONObject requestParams) {
        JSONObject templateParams = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("enter_time_start", "2010-05-05 00:00:00");
        params.put("enter_time_end", "2020-06-13 11:11:17");
        params.put("from", 0);
        params.put("size", 0);
        params.put("is_office", false);
        params.put("office_id", new ArrayList<>());
        params.put("is_fused", false);
        params.put("fused_id", new ArrayList<>());
        params.put("is_camera", false);
        params.put("camera_id", new ArrayList<>());
        params.put("date_aggregation", true);
        templateParams.put("id", "template_person_aggregation");
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
                        int tmpDocNums = tmpFusedObject.getInteger("doc_count").intValue();
                        tmpDocNums += docNums;
                        tmpFusedObject.put("doc_count", tmpDocNums);
                    } else {
                        map.put(fusedId, fusedJsonObject);
                    }
                });
            });
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("total", totalCount.get());
            jsonObject.put("buckets", map.values());
            bucketsMap.put(key, jsonObject);
        });
        return FastJsonUtils.JsonBuilder.ok().property("Total", total).property("Took", took).object(bucketsMap).json();
    }
}
