package com.znv.fssrqs.elasticsearch.homepage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dongzelong on  2019/9/4 19:03.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class CapturePersonCountService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;

    public JSONObject getStrangerAggs() {
        JSONObject templateParams = new JSONObject();
        JSONObject params = new JSONObject();
        templateParams.put("id", EsBaseConfig.getInstance().getHistoryPersonCountTemplateName());
        templateParams.put("params", params);
        String startTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesmorning());
        String endTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesnight());
        params.put("day_start_time", startTime);
        params.put("day_end_time", endTime);
        params.put("person_id", "0");
        params.put("agg_start_time", startTime);
        params.put("agg_end_time", endTime);
        String url = EsBaseConfig.getInstance().getEsIndexHistoryName() + "/" + EsBaseConfig.getInstance().getEsIndexHistoryType() + "/_search/template";
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, templateParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return getResult(result.value());
    }

    public JSONObject getResult(JSONObject response) {
        int took = response.getIntValue("took");
        JSONObject aggs = response.getJSONObject("aggregations");
        String personTotal = aggs.getJSONObject("agg_total").getString("doc_count");
        String strangerTotal = response.getJSONObject("hits").getString("total");
        String personDailyAdd = aggs.getJSONObject("person_daily_add").getString("doc_count");
        String strangerDailyAdd = aggs.getJSONObject("person_daily_add").getJSONObject("stranger_daily_add").getJSONObject("buckets").getJSONObject("filter").getString("doc_count");
        JSONArray personAggByOfficeBuckets = aggs.getJSONObject("agg_by_time").getJSONObject("person_agg_by_office").getJSONArray("buckets");
        JSONArray strangerAggByOfficeBuckets = aggs.getJSONObject("agg_by_time").getJSONObject("stranger").getJSONObject("stranger_agg_by_office").getJSONArray("buckets");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("Took", took);
        jsonObject.put("PersonTotal", personTotal);//抓拍人员总数
        jsonObject.put("StrangerTotal", strangerTotal);//陌生人总数
        jsonObject.put("DailyPersonAdd", personDailyAdd);//规定时间段内抓拍人员新增数
        jsonObject.put("StrangerDailyAdd", strangerDailyAdd);//规定时间段内陌生人新增数
        jsonObject.put("PersonAggByOfficeBuckets",personAggByOfficeBuckets);//综合分析时间段内人流量按区域聚合
        jsonObject.put("StrangerAggByOfficeBuckets",strangerAggByOfficeBuckets);//综合分析时间段内陌生人统计按区域聚合
        return FastJsonUtils.JsonBuilder.ok().object(jsonObject).json();
    }
}
