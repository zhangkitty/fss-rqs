package com.znv.fssrqs.service.face.search.one.n;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.elasticsearch.util.FeatureCompUtil;
import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import com.znv.fssrqs.service.face.search.one.n.dto.CommonSearchParams;
import com.znv.fssrqs.service.face.search.one.n.dto.CommonSearchResultDTO;
import com.znv.fssrqs.util.FaceAIUnitUtils;
import com.znv.fssrqs.util.FormatObject;
import com.znv.fssrqs.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public JSONObject fastSearch(String host,GeneralSearchParam params) throws IOException {
        FeatureCompUtil fc = new FeatureCompUtil();

        String remoteIp = host.split(":")[0];

        CommonSearchParams commonSearchParams = modelMapper.map(params,CommonSearchParams.class);

        commonSearchParams.setFrom((params.getCurrentPage()-1)*params.getPageSize());

        String[] arr = new String[commonSearchParams.getFeatureValue().length];
        for(int i = 0 ;i<commonSearchParams.getFeatureValue().length;i++){
            arr[i] = (String) JSONObject.parseObject(FaceAIUnitUtils.getImageFeature(commonSearchParams.getFeatureValue()[i])).get("feature");
        }
        commonSearchParams.setFeatureValue(arr);
        JSONObject paramsWithTempId = new JSONObject();
        paramsWithTempId.put("id","template_fast_feature_search");
        paramsWithTempId.put("params",commonSearchParams);

        String url = calculateIndex(params,commonSearchParams);

        System.out.println(url);

        HttpEntity httpEntity = new NStringEntity(paramsWithTempId.toJSONString()
                ,ContentType.APPLICATION_JSON);

        Response response = elasticSearchClient.getInstance().getRestClient().performRequest("get",url,Collections.emptyMap(),httpEntity);

        JSONObject result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));

        Integer Total = (Integer)result.getJSONObject("hits").get("total");

        List list = new LinkedList();

        result.getJSONObject("hits").getJSONArray("hits").forEach(v->{
            CommonSearchResultDTO commonSearchResultDTO = modelMapper.map(((JSONObject)v).get("_source"),CommonSearchResultDTO.class);
            String smallUuid = (String) ((JSONObject)((JSONObject) v).get("_source")).get("img_url");
            String imgUrl = ImageUtils.getImgUrl(remoteIp, "GetSmallPic", smallUuid);
            commonSearchResultDTO.setSmallPictureUrl(imgUrl);

            String op_time = (String) ((JSONObject)((JSONObject) v).get("_source")).get("op_time");
            commonSearchResultDTO.setOp_time(FormatObject.formatTimeTrim(op_time));
            String enter_time = (String) ((JSONObject)((JSONObject) v).get("_source")).get("enter_time");
            commonSearchResultDTO.setEnter_time(FormatObject.formatTimeTrim(enter_time));
            String faceDisAppearTime = (String) ((JSONObject)((JSONObject) v).get("_source")).get("leave_time");
            commonSearchResultDTO.setLeave_time(FormatObject.formatTimeTrim(faceDisAppearTime));

            String bigPictureUuid = (String) ((JSONObject)((JSONObject) v).get("_source")).get("big_picture_uuid");
            if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)){
                commonSearchResultDTO.setBigPictureUrl("");
            }else {
                commonSearchResultDTO.setBigPictureUrl(ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
            }
            commonSearchResultDTO.setSimilarity(fc.Normalize(((JSONObject) v).getFloatValue("_score")));
            list.add(JSONObject.parse(JSONObject.toJSONString(commonSearchResultDTO)));
        });

        JSONObject ret = new JSONObject();
        ret.put("TotalSize",Total);
        ret.put("List",list);
        return  ret;

    }


    //计算索引的迁移很麻烦
    private String calculateIndex(GeneralSearchParam params,CommonSearchParams commonSearchParams){
        FeatureCompUtil fc = new FeatureCompUtil();
        String indexName="";
        String indexNamePrepix = "history_fss_data_n_project_v1_2";
        int coarseCentersNum = 3;


        int[][] coarseCodeOrder = null;

        String[] featureValue = commonSearchParams.getFeatureValue();
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
