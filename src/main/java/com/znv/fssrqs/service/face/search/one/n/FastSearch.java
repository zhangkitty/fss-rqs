package com.znv.fssrqs.service.face.search.one.n;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.elasticsearch.util.FeatureCompUtil;
import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import com.znv.fssrqs.service.face.search.one.n.dto.CommonSearchParams;
import com.znv.fssrqs.service.face.search.one.n.dto.CommonSearchResultDTO;
import lombok.extern.slf4j.Slf4j;
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

import static com.znv.fssrqs.elasticsearch.lopq.LOPQModel.predictCoarseOrder;


/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.18 下午4:24
 */
@Service
@Slf4j
public class FastSearch {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private ModelMapper modelMapper;

    public JSONObject fastSearch(GeneralSearchParam params) throws IOException {

        CommonSearchParams commonSearchParams = modelMapper.map(params,CommonSearchParams.class);

        commonSearchParams.setFrom((params.getPageNum()-1)*params.getPageSize());

        JSONObject paramsWithTempId = new JSONObject();
        paramsWithTempId.put("id","template_fss_arbitrarysearch");
        paramsWithTempId.put("params",commonSearchParams);

        String url = calculateIndex(params);

        HttpEntity httpEntity = new NStringEntity(paramsWithTempId.toJSONString()
                ,ContentType.APPLICATION_JSON);

        Response response = elasticSearchClient.getInstance().getRestClient().performRequest("get",url,Collections.emptyMap(),httpEntity);

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

    //todo
    //计算索引的迁移很麻烦
    private String calculateIndex(GeneralSearchParam params){
        FeatureCompUtil fc = new FeatureCompUtil();
        String indexName="";
        String indexNamePrepix = "history_fss_data_n_project_v1_2";
        int coarseCentersNum = 3;


        int[][] coarseCodeOrder = null;

        String[] featureValue = params.getFeatureValue();
        for (int i = 0; i < featureValue.length; i++) {
            try {
                coarseCodeOrder = predictCoarseOrder(fc.getFloatArray(new org.apache.commons.codec.binary.Base64().decode(featureValue[i])), coarseCentersNum);
            } catch (Exception e) {
                log.info("Get Coarse Code Error: " + e);

            }
            if (i < featureValue.length - 1) {
                for (int j = 0; j < coarseCodeOrder.length; j++) {
                    indexName += indexNamePrepix + "-" + coarseCodeOrder[j][0] + ",";
                }
            } else {
                for (int j = 0; j < coarseCodeOrder.length - 1; j++) {
                    indexName += indexNamePrepix + "-" + coarseCodeOrder[j][0] + ",";
                }
                indexName += indexNamePrepix + "-" + coarseCodeOrder[coarseCodeOrder.length - 1][0];
            }
        }

        return indexName+"/history_data/_search/template";
    }
}
