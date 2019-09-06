package com.znv.fssrqs.elasticsearch.homepage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dongzelong on  2019/9/5 19:14.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class PersonLibCountService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    private String templateName = "template_personlib_count";

    public JSONObject personAggsByLibId(Integer personLibType) {
        String url = new StringBuffer()
                .append(EsBaseConfig.getInstance().getIndexPersonListName())
                .append("/")
                .append(EsBaseConfig.getInstance().getIndexPersonListType())
                .append("/_search/template").toString();

        JSONObject templateParams = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("is_del", 0);
        params.put("is_personlib_type", true);
        params.put("personlib_type", personLibType);
        templateParams.put("id", templateName);
        templateParams.put("params", params);
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, templateParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return getResult(result.value());
    }

    public JSONObject getResult(JSONObject response) {
        Integer took = response.getInteger("took");
        final Integer total = response.getJSONObject("hits").getInteger("total");
        final JSONArray personLibAggs = response.getJSONObject("aggregations").getJSONObject("agg_by_lib_id").getJSONArray("buckets");
        personLibAggs.forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            jsonObject.put("DocCount", jsonObject.remove("doc_count"));
            jsonObject.put("Key", jsonObject.remove("key"));
        });
        return FastJsonUtils.JsonBuilder.ok().list(personLibAggs).property("Took", took).property("Total", total).json();
    }
}
