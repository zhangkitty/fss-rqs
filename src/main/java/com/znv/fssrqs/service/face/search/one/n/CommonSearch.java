package com.znv.fssrqs.service.face.search.one.n;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import com.znv.fssrqs.service.face.search.one.n.dto.CommonSearchParams;
import com.znv.fssrqs.service.face.search.one.n.dto.CommonSearchResultDTO;
import com.znv.fssrqs.util.FormatObject;
import com.znv.fssrqs.util.ImageUtils;
import lombok.Data;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangcaochao
 * @Description 不带图片的查询
 * @Date 2019.6.7 上午9:10
 */

@Service
@Data
@Slf4j
public class CommonSearch {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private ModelMapper modelMapper;

    // 定义全局变量 标志查询线程状态 0：未执行 1：已执行 2 ：执行成功 3:执行失败
    private Map<String, Integer> concurrentHashMap = new ConcurrentHashMap<String, Integer>();


    public JSONObject commonSearch(String host,GeneralSearchParam params) throws IOException {

        String remoteIp = host.split(":")[0];
        CommonSearchParams commonSearchParams = modelMapper.map(params,CommonSearchParams.class);

        commonSearchParams.setFrom((params.getCurrentPage()-1)*params.getPageSize());
        commonSearchParams.setAgeLowerLimit(null);
        commonSearchParams.setAgeUpLimit(null);
        commonSearchParams.setGenderType(null);
        commonSearchParams.setGlass(null);
        commonSearchParams.setRespirator(null);
        commonSearchParams.setMustache(null);
        commonSearchParams.setEmotion(null);
        commonSearchParams.setEyeOpen(null);
        commonSearchParams.setMouthOpen(null);
        commonSearchParams.setSkinColor(null);

        JSONObject paramsWithTempId = new JSONObject();
        paramsWithTempId.put("id","template_fss_arbitrarysearch");
        paramsWithTempId.put("params",commonSearchParams);

        HttpEntity httpEntity = new NStringEntity(paramsWithTempId.toJSONString()
                ,ContentType.APPLICATION_JSON);

        String esUrl = EsBaseConfig.getInstance().getEsIndexHistoryPrefix();
        esUrl = esUrl + "-*" + "/_search/template";
        Response response = elasticSearchClient.getInstance().getRestClient().performRequest("get",esUrl,Collections.emptyMap(),httpEntity);

        JSONObject result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));

        Integer Total = (Integer)result.getJSONObject("hits").get("total");

        List list = new LinkedList();

        result.getJSONObject("hits").getJSONArray("hits").forEach(v->{
            CommonSearchResultDTO commonSearchResultDTO = modelMapper.map(((JSONObject)v).get("_source"),CommonSearchResultDTO.class);

            String op_time = (String) ((JSONObject)((JSONObject) v).get("_source")).get("op_time");
            if (StringUtils.isNotEmpty(op_time)) {
                commonSearchResultDTO.setOp_time(FormatObject.formatTimeTrim(op_time));
            }

            String smallUuid = (String) ((JSONObject)((JSONObject) v).get("_source")).get("img_url");
            String imgUrl = ImageUtils.getImgUrl(remoteIp, "GetSmallPic", smallUuid);
            commonSearchResultDTO.setSmallPictureUrl(imgUrl);

            String enter_time = (String) ((JSONObject)((JSONObject) v).get("_source")).get("enter_time");
            commonSearchResultDTO.setEnter_time(FormatObject.formatTimeTrim(enter_time));
            String faceDisAppearTime = (String) ((JSONObject)((JSONObject) v).get("_source")).get("leave_time");
            commonSearchResultDTO.setLeave_time(FormatObject.formatTimeTrim(faceDisAppearTime));


            String bigPictureUuid = (String) ((JSONObject)((JSONObject) v).get("_source")).get("big_picture_uuid");
            if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)){
                commonSearchResultDTO.setBigPictureUrl("");
            }else {
                commonSearchResultDTO.setBigPictureUrl(ImageUtils.getImgUrl(null, "GetBigBgPic", bigPictureUuid));
            }
            if (StringUtils.isEmpty(commonSearchResultDTO.getOp_time())) {
                log.error("common search error: {}, {}", paramsWithTempId.toJSONString(), ((JSONObject) v).toJSONString());
            } else {
                list.add(JSONObject.parse(JSONObject.toJSONString(commonSearchResultDTO)));
            }
        });

        JSONObject ret = new JSONObject();
        ret.put("TotalSize",Total);
        ret.put("List",list);
        return  ret;
    }
}
