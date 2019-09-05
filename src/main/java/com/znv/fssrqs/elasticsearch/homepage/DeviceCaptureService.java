package com.znv.fssrqs.elasticsearch.homepage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Created by dongzelong on  2019/9/3 10:07.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
@Slf4j
public class DeviceCaptureService {
    @Resource
    private ElasticSearchClient elasticSearchClient;
    private final int DAY = 0; //天
    private final int WEEK = 1; //周
    private final int MONTH = 2; //月

    /**
     * @param params
     * @return
     */
    public JSONObject getDeviceCaptureList(JSONObject params) {
        int timeType = params.getIntValue("TimeType");
        String startTime;
        String endTime;
        switch (timeType) {
            case DAY:
                startTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesmorning(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
                endTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesnight(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
                break;
            case WEEK:
                startTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesWeekmorning(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
                endTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesWeeknight(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
                break;
            case MONTH:
                startTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesMonthmorning(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
                endTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesMonthnight(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
                break;
            default:
                startTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesmorning(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
                endTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesnight(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
                break;
        }

        JSONObject inputParams = new JSONObject();
        inputParams.put("camera_id", params.getJSONArray("CameraIDs"));
        inputParams.put("enter_time_start", startTime);
        inputParams.put("enter_time_end", endTime);
        inputParams.put("camera_top_num", params.getIntValue("Top"));
        final Result<JSONObject, String> result = sendRequest(inputParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }

        JSONObject jsonObject = result.value();
        return getSearchResult(jsonObject);
    }

    public Result<JSONObject, String> sendRequest(JSONObject inputParams) {
        String url = HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_INDEX_HISTORY_NAME) + "/" + HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_INDEX_HISTORY_TYPE);
        url = url + "/_search/template";
        if (Objects.nonNull(inputParams.getJSONArray("camera_id")) && !inputParams.getJSONArray("camera_id").isEmpty()) {
            inputParams.put("is_camera", true);
        }

        inputParams.put("from", 0);
        inputParams.put("size", 0);
        inputParams.put("camera_aggregation", true);
        JSONObject templateParams = new JSONObject();
        String templateName = CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_HISTORY_CAMEMA_TAKE_COUNT;
        templateParams.put("id", templateName);
        templateParams.put("params", inputParams);
        return elasticSearchClient.postRequest(url, templateParams);
    }

    protected JSONObject getSearchResult(JSONObject jsonObject) {
        int took = jsonObject.getIntValue("took");
        String total = jsonObject.getJSONObject("hits").getString("total");
        JSONArray agg = jsonObject.getJSONObject("aggregations").getJSONObject("group_by_camera").getJSONArray("buckets");
        JSONArray hits = new JSONArray();
        if (null != agg && agg.size() != 0) {
            for (int i = 0; i < agg.size(); i++) {
                JSONObject topHit = (JSONObject) agg.getJSONObject(i).getJSONObject("camera_hits").getJSONObject("hits").getJSONArray("hits").get(0);
                JSONObject source = topHit.getJSONObject("_source");
                source.put("CameraName", source.remove("camera_name"));
                source.put("CameraID", agg.getJSONObject(i).getString("key"));
                source.put("DocCount", agg.getJSONObject(i).getString("doc_count"));
                source.put("GpsXy", source.remove("gps_xy"));
                source.put("OfficeID", source.remove("office_id"));
                source.put("OfficeName", source.remove("office_name"));
                hits.add(i, source);
            }
        }
        return FastJsonUtils.JsonBuilder.ok().list(hits).property("Total", total).property("Took", took).json();
    }
}
