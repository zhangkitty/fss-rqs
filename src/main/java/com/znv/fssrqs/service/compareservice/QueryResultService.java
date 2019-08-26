package com.znv.fssrqs.service.compareservice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.controller.face.compare.n.n.QueryResultParams;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.util.Base64Util;
import com.znv.fssrqs.util.ImageUtils;
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

        StringBuffer sb = new StringBuffer();
        sb
            .append("http://")
            .append(elasticSearchClient.getHost())
            .append(":")
            .append(elasticSearchClient.getPort())
            .append("/")
            .append("n2m_face_result_n_project*")
            .append("/")
            .append("n2m_face_result")
            .append("/")
            .append("_search");

        String url = sb.toString();

        Map<String, String> map = new HashMap<>();
        map.put("taskId", queryResultParams.getTaskId().toString());
        map.put("from", queryResultParams.getFrom().toString());
        map.put("size",queryResultParams.getSize().toString());
        map.put("sim",queryResultParams.getSim().toString());


        String content = "";
        if(queryResultParams.getRemark()==null){
             content ="{\n" +
                     "  \"query\":{\n" +
                     "      \"bool\":{\n" +
                     "          \"must\": [\n" +
                     "            {\n" +
                     "              \"term\": {\n" +
                     "                \"task_id\": {\n" +
                     "                  \"value\": \"${taskId}\"\n" +
                     "                }\n" +
                     "              }\n" +
                     "            },\n" +
                     "            {\n" +
                     "              \"range\": {\n" +
                     "                \"compare_sim\": {\n" +
                     "                  \"gte\": ${sim}\n" +
                     "                }\n" +
                     "              }\n" +
                     "            }\n" +
                     "          ]\n" +
                     "      }\n" +
                     "  },\n" +
                     "  \"from\":${from},\n" +
                     "  \"size\":${size}\n" +
                     "}";
        }else {
            map.put("remark",queryResultParams.getRemark());
            content = "{\n" +
                    "  \"query\":{\n" +
                    "      \"bool\":{\n" +
                    "          \"must\": [\n" +
                    "            {\n" +
                    "              \"term\": {\n" +
                    "                \"task_id\": {\n" +
                    "                  \"value\": \"${taskId}\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"term\": {\n" +
                    "                \"remark\": {\n" +
                    "                  \"value\": \"${remark}\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"range\": {\n" +
                    "                \"compare_sim\": {\n" +
                    "                  \"gte\": ${sim}\n" +
                    "                }\n" +
                    "              }\n" +
                    "            }\n" +
                    "          ]\n" +
                    "      }\n" +
                    "  },\n" +
                    "  \"from\":${from},\n" +
                    "  \"size\":${size}\n" +
                    "}";
        }

        content = Template.renderString(content, map);

        JSONObject esBody = (JSONObject)JSONObject.parseObject(content);

        Result<JSONObject, String> result = elasticSearchClient.postRequest(url,esBody);

//        ArrayList list = result.value().getJSONObject("hits").getJSONArray("hits")
//                .stream().map(v->((JSONObject)v).get("_source")).collect(Collectors.toCollection(ArrayList::new));


        ArrayList list = result.value().getJSONObject("hits").getJSONArray("hits")
                .stream().map(v->(JSONObject)v).map(t->{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject = t.getJSONObject("_source");
                    jsonObject.put("id",t.getString("_id"));
                    String str1 = t.getJSONObject("_source").getString("person_id1")+"&"+t.getJSONObject("_source").getString("lib_id1");
                    String str2 = t.getJSONObject("_source").getString("person_id2")+"&"+t.getJSONObject("_source").getString("lib_id2");
                    String pic1= ImageUtils.getImgUrl("","get_fss_personimage",Base64Util.encodeString(str1));
                    String pic2= ImageUtils.getImgUrl("","get_fss_personimage",Base64Util.encodeString(str2));
                    jsonObject.put("url1",pic1);
                    jsonObject.put("url2",pic1);
                    return jsonObject;
                })
                .collect(ArrayList::new,(list1,value)->list1.add(value),(list1,list2)->list1.addAll(list2));

        Integer total = result.value().getJSONObject("hits").getInteger("total");

        JSONObject jsonObject  = new JSONObject();

        jsonObject.put("total",total);
        jsonObject.put("list",list);

        return jsonObject;

    }


    private String generatPersonImgUrl(String remoteIp, String libId, String personId) {
        String str = personId + "&" + libId;
        return ImageUtils.getImgUrl(remoteIp, "get_fss_personimage", Base64Util.encodeString(str));
    }
}
