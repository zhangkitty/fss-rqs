package com.znv.fssrqs.elasticsearch.homepage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dongzelong on  2019/9/4 16:02.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class PersonListCountService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;

    /**
     * 获取人员统计:新增重点人员数,历史重点人员数,当日路人入库数,历史路人入库数
     */
    public JSONObject getPersonStatistics() {
        //人员名单库统计
        String isMultiIndex = HdfsConfigManager.getString("person.list.multi.index");
        String url = "";
        if (StringUtils.isEmpty(isMultiIndex) || isMultiIndex.trim().equals("2")) {//单索引
            url = new StringBuffer().append(EsBaseConfig.getInstance().getIndexPersonListName())
                    .append("/")
                    .append(EsBaseConfig.getInstance().getIndexPersonListType())
                    .append("/_search/template").toString();
        } else {
            url = new StringBuffer().append(EsBaseConfig.getInstance().getIndexPersonListName())
                    .append("-*")
                    .append("/")
                    .append(EsBaseConfig.getInstance().getIndexPersonListType())
                    .append("/_search/template").toString();
        }
        JSONObject templateParams = new JSONObject();
        templateParams.put("id", EsBaseConfig.getInstance().getPersonListCountTemplateName());
        JSONObject params = new JSONObject();
        String startTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesmorning(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
        String endTime = DataConvertUtils.dateToStr(DataConvertUtils.getTimesnight(), DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("is_del", 0);
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, templateParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return getResult(result.value());
    }

    private JSONObject getResult(JSONObject response) {
        String total = response.getJSONObject("hits").getString("total");
        double took = response.getDouble("took");
        JSONArray aggByTypeBuckets = new JSONArray();
        JSONObject aggs = response.getJSONObject("aggregations");
        JSONArray agg_by_type = aggs.getJSONObject("agg_by_type").getJSONArray("buckets");
        for (int i = 0; i < agg_by_type.size(); i++) {
            JSONObject in = agg_by_type.getJSONObject(i);
            JSONObject out = new JSONObject();
            out.put("Key", in.getString("key"));
            out.put("DocCount", in.getIntValue("doc_count"));
            out.put("LibCount", in.getJSONObject("lib_count").getIntValue("value"));
            out.put("DailyAddCount", in.getJSONObject("daily_add").getJSONObject("buckets").getJSONObject("query").getIntValue("doc_count"));
            out.put("ControlCount", in.getJSONObject("control_count").getIntValue("doc_count"));
            aggByTypeBuckets.add(out);
        }
        return FastJsonUtils.JsonBuilder.ok().list(aggByTypeBuckets).property("Took", took).property("Total", total).json();
    }
}
