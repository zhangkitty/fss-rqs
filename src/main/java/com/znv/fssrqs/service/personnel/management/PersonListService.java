package com.znv.fssrqs.service.personnel.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.param.personnel.management.PersonListSearchParams;
import com.znv.fssrqs.service.personnel.management.dto.OnePersonListDTO;
import com.znv.fssrqs.service.personnel.management.dto.STPersonListSearchDTO;
import com.znv.fssrqs.util.*;
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
import java.util.Collections;
import java.util.UUID;

/**
 * @author zhangcaochao
 * @Description 人员列表查询
 * @Date 2019.06.10 下午3:07
 */

@Service
@Slf4j
public class PersonListService {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private ModelMapper modelMapper;

    public JSONObject getPersonList(String host, PersonListSearchParams personListSearchParams) throws IOException {

        JSONObject paramsWithTempId = new JSONObject();

        STPersonListSearchDTO stPersonListSearchDTO = modelMapper.map(personListSearchParams,STPersonListSearchDTO.class);
        Integer from = (Integer.valueOf(personListSearchParams.getCurrentPage())-1)*Integer.valueOf(personListSearchParams.getPageSize());
        stPersonListSearchDTO.setFrom(from.toString());

        paramsWithTempId.put("params",stPersonListSearchDTO);
        paramsWithTempId.put("id","template_person_list_search");
        HttpEntity httpEntity = new NStringEntity(paramsWithTempId.toJSONString()
                ,ContentType.APPLICATION_JSON);

        Response response = elasticSearchClient.getInstance().getRestClient().performRequest("GET","/_search/template",Collections.emptyMap(),httpEntity);
        JSONObject result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        Integer Total = (Integer)result.getJSONObject("hits").get("total");
        JSONObject PersonlibTypes = new JSONObject();
        JSONObject LibIds  = new JSONObject();
        JSONArray PersonList = new JSONArray();
        result.getJSONObject("aggregations").getJSONObject("personlib_types").getJSONArray("buckets").forEach(v->{
            ((JSONArray)((JSONObject)((JSONObject)v).get("lib_ids")).get("buckets")).forEach(t->LibIds.put(((JSONObject)t).get("key").toString(),((JSONObject)t).get("doc_count")));
            PersonlibTypes.put(((JSONObject)v).get("key").toString(),((JSONObject)v).get("doc_count"));
        });
        result.getJSONObject("hits").getJSONArray("hits").forEach(v->{
            OnePersonListDTO onePersonListDTO = modelMapper.map(((JSONObject)v).get("_source"),OnePersonListDTO.class);
            onePersonListDTO.setCreate_time(FormatObject.formatTime(onePersonListDTO.getCreate_time()));
            onePersonListDTO.setModify_time(FormatObject.formatTime(onePersonListDTO.getModify_time()));
            onePersonListDTO.setControl_start_time(FormatObject.formatTime(onePersonListDTO.getControl_start_time()));
            onePersonListDTO.setControl_end_time(FormatObject.formatTime(onePersonListDTO.getControl_end_time()));

            onePersonListDTO.setCountry(CountryCodeUtil.countryCodeTransToGB1400(onePersonListDTO.getCountry()));
            onePersonListDTO.setNation(CountryCodeUtil.ethnicCodeTransToGB1400(onePersonListDTO.getNation()));

            // 重庆的常口库
            if (CommonConstant.ChongQingLib.RESIDENT.equals(onePersonListDTO.getLib_id())
                    || CommonConstant.ChongQingLib.SECOND_GENERATION_ID_CARD.equals(onePersonListDTO.getLib_id())) {
                onePersonListDTO.setIs_del(0);
            }
            String remoteIp = host.split(":")[0];
            String imgUrl = null;
            try {
                imgUrl = ImageUtils.getImgUrl(remoteIp, "get_fss_personimage",
                        Base64Util.encode(String.format("%s&%s&%s",
                                onePersonListDTO.getPerson_id(),
                                onePersonListDTO.getLib_id(),
                                UUID.randomUUID()).getBytes("UTF-8")));
                onePersonListDTO.setImage_url(imgUrl);
            } catch (Exception e) {
                log.error("getPersonList, getImgUrl exception {}", e);
            }

            String sim = onePersonListDTO.getScore();
            if (!StringUtils.isEmpty(sim) && !"0.0".equals(sim)) {
                onePersonListDTO.setSim(("" + Double.parseDouble(sim) * 100).substring(0, 5) + "%");
            }

            PersonList.add(JSONObject.parse(JSONObject.toJSONString(onePersonListDTO)));
        });

        JSONObject jsonObject = new JSONObject();
        JSONObject Aggregations = new JSONObject();

        jsonObject.put("PersonList",PersonList);
        jsonObject.put("Total",Total);

        Aggregations.put("PersonlibTypes",PersonlibTypes);
        Aggregations.put("LibIds",LibIds);
        jsonObject.put("Aggregations",Aggregations);

        return jsonObject;
    }
}
