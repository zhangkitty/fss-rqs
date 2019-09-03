package com.znv.fssrqs.elasticsearch.homepage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dongzelong on  2019/8/29 15:21.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class AlarmTopLibCountService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;

    /**
     * 获取库告警数
     */
    public JSONObject getAlarmTopLibCount(JSONObject jsonParam) {
        String stringParam = "{'aggs':{'lib_ids':{'terms':{'field':'lib_id','size':10}}},'from':0, 'size':0}";
        JSONObject stringParamJson = JSON.parseObject(stringParam);
        if (jsonParam.containsKey("Top")) {
            stringParamJson.getJSONObject("aggs").getJSONObject("lib_ids").getJSONObject("terms").put("size", jsonParam.getInteger("Top"));
        }

        if (jsonParam.containsKey("LibIDs") && !jsonParam.getJSONArray("LibIDs").isEmpty()) {
            JSONObject stringParamTerms = JSON.parseObject("{'bool':{'filter':{'bool':{'should':{'terms':{'lib_id':[]}}}}}}");
            stringParamTerms.getJSONObject("bool").getJSONObject("filter").getJSONObject("bool").getJSONObject("should")
                    .getJSONObject("terms").put("lib_id", jsonParam.getJSONArray("LibIDs"));
            stringParamJson.put("query", stringParamTerms);
        }

        String esUrl = HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_ALARM_NAME) + "/" + HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_ALARM_TYPE) + "/_search";
        Result<JSONObject, String> result = elasticSearchClient.postRequest(esUrl, stringParamJson);
        return result.value();
    }
}
