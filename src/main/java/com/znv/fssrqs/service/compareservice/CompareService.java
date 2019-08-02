package com.znv.fssrqs.service.compareservice;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.CompareTaskDao;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.entity.mysql.CompareTaskEntity;
import com.znv.fssrqs.param.face.compare.n.n.NToNCompareTaskParam;
import com.znv.fssrqs.timer.CompareTaskLoader;
import com.znv.fssrqs.util.HttpUtils;
import com.znv.fssrqs.util.MD5Util;
import com.znv.fssrqs.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午3:37
 */

@Service
public class CompareService {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private CompareTaskDao compareTaskDao;



    public HashMap check(JSONObject jsonObject){

        HashMap hashMap = new HashMap();

        Integer max = jsonObject.getIntValue("LimitCount");

        jsonObject.getJSONArray("LibID").forEach(value->{
            if(getPersonCount((Integer) value)>max){
                hashMap.put(value,"该库的人数超过"+max);
            }
        });

        return hashMap;
    }


   public Integer save(NToNCompareTaskParam nToNCompareTaskParam){

        String MD5 = MD5Util.encode(nToNCompareTaskParam.toString());

        nToNCompareTaskParam.setTaskId(MD5);
        nToNCompareTaskParam.setStatus(1);
        nToNCompareTaskParam.setProcess(0f);
        Integer result = compareTaskDao.save(nToNCompareTaskParam);

        CompareTaskEntity o = new CompareTaskEntity();
        o.setTaskId(MD5);
        o.setStatus(1);
        o.setProcess(0f);
        o.setLib1(nToNCompareTaskParam.getLib1());
        o.setLib2(nToNCompareTaskParam.getLib2());
        o.setSim(nToNCompareTaskParam.getSim());

        if(result>0){
            CompareTaskLoader.getInstance().registerObserver(o);
        }
        return result;
   }


    private Integer getPersonCount(Integer libID){

        StringBuffer str = new StringBuffer();

        str.append("{\"size\":0,\"query\":{\"bool\":{\"must\":{\"term\":{\"lib_id\":").append(libID).append("}}}},\"from\":0}");

        JSONObject jsonObject = JSONObject.parseObject(str.toString());

        Result<JSONObject, String> result = elasticSearchClient.postRequest("http://10.45.152.230:9200/person_list_data_n_project_v1_2/person_list/_search?pretty",jsonObject);

       return (Integer) result.value().getJSONObject("hits").get("total");
    }

}
