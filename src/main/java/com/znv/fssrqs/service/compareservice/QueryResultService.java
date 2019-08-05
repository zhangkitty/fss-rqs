package com.znv.fssrqs.service.compareservice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.controller.face.compare.n.n.QueryResultParams;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.util.Result;
import com.znv.fssrqs.util.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:50
 */

@Service
@Slf4j
public class QueryResultService {


    @Autowired
    private ElasticSearchClient elasticSearchClient;


    public JSONObject queryResultService(QueryResultParams queryResultParams){

        String url = "http://10.45.152.230:9200/n2m_face_result_n_project_v1.20/n2m_face_result/_search";

        Map<String, String> map = new HashMap<>();
        map.put("taskId", queryResultParams.getTaskId());
        map.put("from", queryResultParams.getFrom().toString());
        map.put("size",queryResultParams.getSize().toString());
        String content = "{\"query\":{\"bool\":{\"filter\":{\"term\":{\"task_id\":\"${taskId}\"}}}},\"from\":${from},\"size\":${size}}";

        content = Template.renderString(content, map);

        JSONObject esBody = (JSONObject)JSONObject.parseObject(content);

        Result<JSONObject, String> result = elasticSearchClient.postRequest(url,esBody);

        ArrayList list = result.value().getJSONObject("hits").getJSONArray("hits")
                .stream().map(v->((JSONObject)v).get("_source")).collect(Collectors.toCollection(ArrayList::new));

        Integer total = result.value().getJSONObject("hits").getInteger("total");

        JSONObject jsonObject  = new JSONObject();

        jsonObject.put("total",total);
        jsonObject.put("list",list);

        return jsonObject;

    }
}
