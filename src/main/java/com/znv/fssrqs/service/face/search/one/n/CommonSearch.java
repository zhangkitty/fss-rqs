package com.znv.fssrqs.service.face.search.one.n;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.elasticsearch.service.ISearch;
import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import com.znv.fssrqs.service.face.search.one.n.dto.CommonSearchParams;
import com.znv.fssrqs.service.face.search.one.n.dto.CommonSearchResultDTO;
import com.znv.fssrqs.service.personnel.management.dto.OnePersonListDTO;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangcaochao
 * @Description 不带图片的查询
 * @Date 2019.6.7 上午9:10
 */

@Service
public class CommonSearch {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private ModelMapper modelMapper;


    public JSONObject commonSearch(GeneralSearchParam params) throws IOException {

        CommonSearchParams commonSearchParams = modelMapper.map(params,CommonSearchParams.class);

        commonSearchParams.setFrom((params.getPageNum()-1)*params.getPageSize());

        JSONObject paramsWithTempId = new JSONObject();
        paramsWithTempId.put("id","template_fss_arbitrarysearch");
        paramsWithTempId.put("params",commonSearchParams);

        HttpEntity httpEntity = new NStringEntity(paramsWithTempId.toJSONString()
                ,ContentType.APPLICATION_JSON);

        Response response = elasticSearchClient.getInstance().getRestClient().performRequest("get","/_search/template",Collections.emptyMap(),httpEntity);

        JSONObject result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));

        Integer Total = (Integer)result.getJSONObject("hits").get("total");

        List list = new LinkedList();

        result.getJSONObject("hits").getJSONArray("hits").forEach(v->{
            CommonSearchResultDTO commonSearchResultDTO = modelMapper.map(((JSONObject)v).get("_source"),CommonSearchResultDTO.class);
            list.add(JSONObject.parse(JSONObject.toJSONString(commonSearchResultDTO)));
        });

        JSONObject ret = new JSONObject();
        ret.put("TotalSize",Total);
        ret.put("List",list);
        return  ret;
    }
}
