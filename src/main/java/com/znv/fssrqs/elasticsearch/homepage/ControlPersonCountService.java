package com.znv.fssrqs.elasticsearch.homepage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dongzelong on  2019/9/5 16:07.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class ControlPersonCountService {
    String body = "{\n" +
            "                \"size\":0,\n" +
            "\t\"query\": {\n" +
            "\t\t\"bool\": {\n" +
            "\t\t\t\"filter\": {\"term\":{\"personlib_type\":1}},\n" +
            "\t\t\t\"filter\": {\n" +
            "\t\t\t\t\"term\": {\n" +
            "\t\t\t\t\t\"is_del\": \"0\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t},\n" +
            "                                                \"filter\":{\n" +
            "                                                         \"term\":{\n" +
            "                                                               \"flag\":1\n" +
            "                                                          }\n" +
            "                                                }\n" +
            "\t\t}\n" +
            "\t},\n" +
            "               \"aggs\":{\n" +
            "                      \"agg_by_control_police_category\":{\n" +
            "                                \"terms\":{\n" +
            "\t\t      \"field\":\"control_police_category\"\n" +
            "\t\t}\n" +
            "                     }                  \n" +
            "            }\n" +
            "}";

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    /**
     * 布控人员数按照警种进行分组统计
     *
     * @return
     */
    public JSONObject getControlPersonCount() {
        String url = "";
        String isMultiIndex = HdfsConfigManager.getString("person.list.multi.index");
        if (StringUtils.isEmpty(isMultiIndex) || isMultiIndex.trim().equals("2")) {//单索引
            url = new StringBuffer().append(EsBaseConfig.getInstance().getIndexPersonListName())
                    .append("/")
                    .append(EsBaseConfig.getInstance().getIndexPersonListType())
                    .append("/_search").toString();
        } else {
            url = new StringBuffer().append(EsBaseConfig.getInstance().getIndexPersonListName())
                    .append("-*")
                    .append("/")
                    .append(EsBaseConfig.getInstance().getIndexPersonListType())
                    .append("/_search").toString();
        }
        JSONObject requestParams = JSON.parseObject(body);
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, requestParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return getResult(result.value());
    }

    public JSONObject getResult(JSONObject response) {
        Integer took = response.getInteger("took");
        //查询记录数
        final Integer total = response.getJSONObject("hits").getInteger("total");
        final JSONArray aggBuckets = response.getJSONObject("aggregations").getJSONObject("agg_by_control_police_category").getJSONArray("buckets");
        aggBuckets.forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            jsonObject.put("Key", jsonObject.remove("key"));
            jsonObject.put("DocCount", jsonObject.remove("doc_count"));
        });
        return FastJsonUtils.JsonBuilder.ok().list(aggBuckets).property("Total", total).property("Took", took).json();
    }
}
