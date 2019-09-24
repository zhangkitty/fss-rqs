package com.znv.fssrqs.elasticsearch.homepage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.AITaskDeviceRuleDao;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dongzelong on  2019/9/3 20:58.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class TopTenDeviceAlarmService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    @Resource
    private AITaskDeviceRuleDao aiTaskDeviceRuleDao;

    /**
     * 去告警索引中,按照摄像机分组聚合,取前10条
     */
    public JSONObject top10DeviceAlarms(Map<String, Object> params) {
        final Object top = params.getOrDefault("Top", 10);
        final JSONObject responseObject = sendRequest((Integer) top);
        return getResult(responseObject);
    }

    public JSONObject sendRequest(int top) {
        String url = new StringBuffer()
                .append(HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_ALARM_NAME))
                .append("/")
                .append(HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_ALARM_TYPE))
                .append("/_search/template").toString();
        final String templateName = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_ALARM_PERSON_COUNT_ID);
        JSONObject requestParams = new JSONObject();
        requestParams.put("id", templateName);
        JSONObject params = new JSONObject();
        params.put("top", top);
        requestParams.put("params", params);
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, requestParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return result.value();
    }

    public JSONObject getResult(JSONObject responseObject) {
        final Integer took = responseObject.getInteger("took");
        String total = responseObject.getJSONObject("hits").getString("total");
        JSONObject aggs = responseObject.getJSONObject("aggregations");
        JSONArray aggCameraBuckets = aggs.getJSONObject("agg_by_camera_id").getJSONObject("agg_by_camera_buckets").getJSONArray("buckets");
        if (!aggCameraBuckets.isEmpty()) {
            //查询所有分析任务
            final Map<String, Map<String, Object>> cameraMap = aiTaskDeviceRuleDao.selectAllTaskCameras();
            aggCameraBuckets.forEach(object -> {
                JSONObject jsonObject = (JSONObject) object;
                final Object key = jsonObject.remove("key");
                jsonObject.put("CameraID", key);
                final Map<String, Object> map = cameraMap.getOrDefault(key, null);
                if (Objects.nonNull(map)) {
                    jsonObject.put("CameraName", map.get("CameraName"));
                } else {
                    jsonObject.put("CameraName", key);
                    jsonObject.put("Desc", "key has no name");
                }
                jsonObject.put("DocCount", jsonObject.remove("doc_count"));
            });
        }
        return FastJsonUtils.JsonBuilder.ok().list(aggCameraBuckets).property("Total", total).property("Took", took).json();
    }
}
