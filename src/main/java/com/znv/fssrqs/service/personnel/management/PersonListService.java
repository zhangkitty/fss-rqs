package com.znv.fssrqs.service.personnel.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.ChongQingConfig;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.config.PersonConfig;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.param.personnel.management.PersonListSearchParams;
import com.znv.fssrqs.service.hbase.PhoenixService;
import com.znv.fssrqs.service.personnel.management.dto.OnePersonListDTO;
import com.znv.fssrqs.service.personnel.management.dto.STPersonListSearchDTO;
import com.znv.fssrqs.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
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

    @Autowired
    private PhoenixService phoenixService;

    @Autowired
    private ChongQingConfig chongQingConfig;

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
            if (onePersonListDTO.getCreate_time() != null) {
                onePersonListDTO.setCreate_time(FormatObject.formatTime(onePersonListDTO.getCreate_time()));
            }
            if (onePersonListDTO.getModify_time() != null) {
                onePersonListDTO.setModify_time(FormatObject.formatTime(onePersonListDTO.getModify_time()));
            }
            if (onePersonListDTO.getControl_start_time() != null) {
                onePersonListDTO.setControl_start_time(FormatObject.formatTime(onePersonListDTO.getControl_start_time()));
            }
            if (onePersonListDTO.getControl_end_time() != null) {
                onePersonListDTO.setControl_end_time(FormatObject.formatTime(onePersonListDTO.getControl_end_time()));
            }

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


    /**
     * ！！从老模块移植过来，待优化！！
     * 人员查询（含静态库1：N检索）
     * @param host
     * @param params
     * @return
     */
    public JSONObject getPersonList(String host, JSONObject params) {
        JSONObject ret = new JSONObject();
        JSONObject queryInfo = new JSONObject();

        // 处理图片为特征值 如果有图片把特征值加进去
        String searchFeatures = params.getString("imgData");
        if (!StringUtils.isEmpty(searchFeatures)) {
            // 获取特征值
            String feature = FaceAIUnitUtils.getImageFeature(searchFeatures);
            JSONObject parseObject = JSONObject.parseObject(feature);
            if (feature == null){
                ret.put("StatusString", "未获取到图片特征值！");
                return ret;
            }
            if (!"success".equals(parseObject.getString("result"))) {
                ret.put("StatusString", "获取图片特征值失败！");
                return ret;
            }
            params.put("feature_value", parseObject.getString("feature"));
        }
        JSONObject obj = new JSONObject();
        obj.put("id", EsBaseConfig.getInstance().getPersonListTemplateName());
        obj.put("params", params);
        if (params.containsKey("imgData")) {
            params.remove("imgData");
        }
        String esUrl = EsBaseConfig.getInstance().getIndexPersonListName() + "/" + EsBaseConfig.getInstance().getIndexPersonListType() + "/_search/template";
        Result<JSONObject, String> result = elasticSearchClient.postRequest(esUrl, obj);
        if (result.isErr()) {
            throw new RuntimeException("获取es数据失败:" + result.error());
        }
        JSONObject esResult = result.value().getJSONObject("hits");

        String remoteIp = host.split(":")[0]; // 内外网映射，在设置ImageUrl时会使用，先预留着
        JSONArray data = new JSONArray();
        int total = esResult.getInteger("total");
        JSONArray list = esResult.getJSONArray("hits");
        for (int k = 0; k < list.size(); k++) {
            JSONObject jsonObject = list.getJSONObject(k).getJSONObject("_source");
            String personId = jsonObject.getString("person_id");
            String libId = jsonObject.getString("lib_id");
            JSONObject personInfo = null;

            if (StringUtils.isNotEmpty(personId)
                    && personId.length() == SpringContextUtil.getCtx().getBean(PersonConfig.class).getPersonIdLength()) {
                personInfo = buildFssPersonData(remoteIp, jsonObject, personId, true);
            } else {
                // 静态库1:N检索，需要获取用户信息
                if (params.containsKey("feature_value")) {
                    personInfo = getChongqingPersonInfo(libId, personId);
                } else {
                    personInfo = new JSONObject();
                    personInfo.put("LibId", libId);
                    personInfo.put("PersonID", personId);
                }
                if (personInfo != null) {
                    personInfo.put("ImageUrl", getChongqingPersonImgUrl(libId, personId));
                }
            }

            if (params.containsKey("feature_value")) {
                Double score = list.getJSONObject(k).getDoubleValue("_score");
                if (score >= 1.0) {
                    personInfo.put("Sim", "100%" );
                } else {
                    personInfo.put("Sim", ("" + score * 100).substring(0, 5) + "%" );
                }
            }
            data.add(personInfo);
        }
        JSONObject aggs = new JSONObject();
        if (result.value().containsKey("aggregations")) {
            JSONObject aggregations = result.value().getJSONObject("aggregations");
            JSONArray types = aggregations.getJSONObject("personlib_types").getJSONArray("buckets");
            JSONObject typesTotal = new JSONObject();
            JSONObject libsTotal = new JSONObject();
            aggs.put("PersonlibTypes", typesTotal);
            aggs.put("LibIds", libsTotal);
            for (int m = 0; m < types.size(); m++) {
                JSONObject type = types.getJSONObject(m);
                typesTotal.put(type.getString("key"), type.getIntValue("doc_count"));
                JSONArray libs = type.getJSONObject("lib_ids").getJSONArray("buckets");
                for (int n = 0; n < libs.size(); n++) {
                    JSONObject lib = libs.getJSONObject(n);
                    libsTotal.put(lib.getString("key"), lib.getIntValue("doc_count"));
                }
            }
        }

        ret.put("PersonList", data);
        ret.put("Aggregations", aggs);
        ret.put("TotalRows", total);
        return ret;
    }

    /**
     * 单个人员查询
     * @param host
     * @param libId
     * @param personId
     * @return
     */
    public JSONObject getPerson(String host, String libId, String personId){
        JSONArray arrayList = new JSONArray();
        JSONObject requestdata = new JSONObject();
        requestdata.put("lib_id", libId);
        requestdata.put("person_id", personId);
        arrayList.add(requestdata);

        String tableName = ConfigManager.getTableName(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME);
        JSONObject insertData = new JSONObject();
        insertData.put("id", CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST);
        insertData.put("table_name", tableName);
        insertData.put("data", arrayList);

        JSONObject ret = phoenixService.searchPersonList(insertData);

        String remoteIp = host.split(":")[0];
        JSONObject retData = new JSONObject();

        JSONArray dataPersonArray = ret.getJSONArray("data");
        if (dataPersonArray == null || dataPersonArray.isEmpty()) {
            retData.put("PersonObject", null);
            return retData;
        }
        JSONObject personObject = null;
        JSONObject jsonObject = dataPersonArray.getJSONObject(0);
        if (personId.length() == SpringContextUtil.getCtx().getBean(PersonConfig.class).getPersonIdLength()) {
            personObject = buildFssPersonData(remoteIp, jsonObject, personId, false);
        } else {
            personObject = getChongqingPersonInfo(libId, personId);
            personObject.put("ImageUrl", getChongqingPersonImgUrl(libId, personId));
        }

        if (personObject != null) {
            retData.put("PersonObject", personObject);
        }
        return retData;
    }

    private JSONObject buildFssPersonData(String remoteIp, JSONObject jsonObject, String personId, Boolean isDatasourceEs) {
        JSONObject personObject = new JSONObject();

        String libId = null;
        if (jsonObject.containsKey("lib_id")) {
            libId = jsonObject.getString("lib_id");
            personObject.put("LibID", libId);
        }

        // 重庆的常口库，需要特殊处理
        if (CommonConstant.ChongQingLib.RESIDENT.equals(libId)
                || CommonConstant.ChongQingLib.SECOND_GENERATION_ID_CARD.equals(libId)) {
            personObject.put("IsDel", 0);
        }
        String imgUrl = null;
        try {
            imgUrl = ImageUtils.getImgUrl(remoteIp, "get_fss_personimage",
                    Base64Util.encode(String.format("%s&%s&%s",
                            personId, libId, UUID.randomUUID()).getBytes("UTF-8")));
        } catch (Exception e) {
            log.error("buildFssPersonDataByHBase, getImgUrl exception {}", e);
        }
        personObject.put("ImageUrl", imgUrl);

        if (jsonObject.containsKey("person_id")) {
            personObject.put("PersonID", jsonObject.getString("person_id"));
        }
        if (jsonObject.containsKey("person_name")) {
            personObject.put("Name", jsonObject.getString("person_name"));
        }
        if (jsonObject.containsKey("birth")) {
            personObject.put("Birth", jsonObject.getString("birth"));
        }
        if (jsonObject.containsKey("nation")) {
            personObject.put("EthicCode", CountryCodeUtil.ethnicCodeTransToGB1400(jsonObject.getString("nation")));
        }
        if (jsonObject.containsKey("country")) {
            personObject.put("NationalityCode", CountryCodeUtil.countryCodeTransToGB1400(jsonObject.getString("country")));
        }
        if (jsonObject.containsKey("positive_url")) {
            personObject.put("PositiveUrl", jsonObject.getString("positive_url"));
        }
        if (jsonObject.containsKey("negative_url")) {
            personObject.put("NegativeUrl", jsonObject.getString("negative_url"));
        }
        if (jsonObject.containsKey("addr")) {
            personObject.put("Addr", jsonObject.getString("addr"));
        }
        if (jsonObject.containsKey("tel")) {
            personObject.put("Tel", jsonObject.getString("tel"));
        }
        if (jsonObject.containsKey("nature_residence")) {
            personObject.put("NatureResidence", jsonObject.getString("nature_residence"));
        }
        if (jsonObject.containsKey("room_number")) {
            personObject.put("RoomNumber", jsonObject.getString("room_number"));
        }
        if (jsonObject.containsKey("door_open")) {
            personObject.put("DoorOpen", jsonObject.getInteger("door_open"));
        }
        if (jsonObject.containsKey("sex")) {
            personObject.put("GenderCode", jsonObject.getString("sex"));
        }
        if (jsonObject.containsKey("image_name")) {
            personObject.put("ImageName", jsonObject.getString("image_name"));
        }
        if (jsonObject.containsKey("feature")) {
            personObject.put("Feature", jsonObject.getBytes("feature"));
        }
        if (jsonObject.containsKey("card_id")) {
            personObject.put("IDNumber", jsonObject.getString("card_id"));
        }
        if (jsonObject.containsKey("flag")) {
            personObject.put("Flag", jsonObject.getInteger("flag"));
        }
        if (jsonObject.containsKey("comment")) {
            personObject.put("Comment", jsonObject.getString("comment"));
        }
        if (jsonObject.containsKey("control_start_time")) {
            if (isDatasourceEs) {
                personObject.put("ControlStartTime", FormatObject.formatTime(jsonObject.getString("control_start_time")));
            } else {
                personObject.put("ControlStartTime", jsonObject.getString("control_start_time"));
            }
        }
        if (jsonObject.containsKey("control_end_time")) {
            if (isDatasourceEs) {
                personObject.put("ControlEndTime", FormatObject.formatTime(jsonObject.getString("control_end_time")));
            } else {
                personObject.put("ControlEndTime", jsonObject.getString("control_end_time"));
            }
        }
        if (jsonObject.containsKey("is_del")) {
            personObject.put("IsDel", jsonObject.getInteger("is_del"));
        }
        if (jsonObject.containsKey("create_time")) {
            if (isDatasourceEs) {
                personObject.put("CreateTime", FormatObject.formatTime(jsonObject.getString("create_time")));
            } else {
                personObject.put("CreateTime", jsonObject.getString("create_time"));
            }
        }
        if (jsonObject.containsKey("modify_time")) {
            if (isDatasourceEs) {
                personObject.put("ModifyTime", FormatObject.formatTime(jsonObject.getString("modify_time")));
            } else {
                personObject.put("ModifyTime", jsonObject.getString("modify_time"));
            }
        }
        if (jsonObject.containsKey("community_id")) {
            personObject.put("CommunityID", jsonObject.getString("community_id"));
        }
        if (jsonObject.containsKey("community_name")) {
            personObject.put("CommunityName", jsonObject.getString("community_name"));
        }
        if (jsonObject.containsKey("control_community_id")) {
            personObject.put("ControlCommunityID", jsonObject.getString("control_community_id"));
        }
        if (jsonObject.containsKey("control_person_id")) {
            personObject.put("ControlPersonID", jsonObject.getString("control_person_id"));
        }
        if (jsonObject.containsKey("control_event_id")) {
            personObject.put("ControlEventID", jsonObject.getString("control_event_id"));
        }
        if (jsonObject.containsKey("image_id")) {
            personObject.put("ImageID", jsonObject.getString("image_id"));
        }
        if (jsonObject.containsKey("personlib_type")) {
            personObject.put("PersonlibType", jsonObject.getInteger("personlib_type"));
        }
        if (jsonObject.containsKey("control_police_category")) {
            personObject.put("ControlPoliceCategory", jsonObject.getString("control_police_category"));
        }
        if (jsonObject.containsKey("control_person_tel")) {
            personObject.put("ControlPersonTel", jsonObject.getString("control_person_tel"));
        }
        if (jsonObject.containsKey("control_person_name")) {
            personObject.put("ControlPersonName", jsonObject.getString("control_person_name"));
        }
        if (jsonObject.containsKey("belong_police_station")) {
            personObject.put("BelongPoliceStation", jsonObject.getString("belong_police_station"));
        }
        if (jsonObject.containsKey("card_type")) {
            personObject.put("CardType", jsonObject.getInteger("card_type"));
        }
        if (jsonObject.containsKey("source_id")) {
            personObject.put("SourceID", jsonObject.getString("source_id"));
        }
        if (jsonObject.containsKey("alarm_level")) {
            personObject.put("PersonAlarmLevel", jsonObject.getInteger("alarm_level"));
        }
        if (jsonObject.containsKey("description")) {
            personObject.put("Description", jsonObject.getString("description"));
        }
        personObject.put("InfoKind", 0);

        return  personObject;
    }


    /**
     * 重庆N项目，人员信息在公安网品高湖，根据personId去获取
     * @param libId
     * @param personId
     * @return
     */
    private JSONObject getChongqingPersonInfo(String libId, String personId) {
        if (StringUtils.isEmpty(libId) || StringUtils.isEmpty(personId)) {
            log.error("getChongqingPersonInfo failed! personId or libId is empty! libId: {}, personId: {}",
                    libId, personId);
            return null;
        }

        JSONObject personInfo = PersonInfoCache.getInstance().getPersonInfo(personId);
        if (personInfo != null) {
            return personInfo;
        }

        JSONObject requestParam = new JSONObject();
        if (CommonConstant.ChongQingLib.RESIDENT.equals(libId)
                || CommonConstant.ChongQingLib.SECOND_GENERATION_ID_CARD.equals(libId)
                || CommonConstant.ChongQingLib.RUN_CRIMINAL_CHONGQING.equals(libId)) {
            requestParam.put("dataServiceCode", "cqdsjbzack");
        } else {
            log.error("getChongqingPersonInfo failed! libId illegal: {}", libId);
            return null;
        }

        requestParam.put("rowType", "map");
        JSONObject dataServiceParams = new JSONObject();
        dataServiceParams.put("rid", personId);
        requestParam.put("dataServiceParams", dataServiceParams);

        log.info("request baseInfo: {}", requestParam.toJSONString());
        String s3BaseInfo = HttpUtils.postJsonString(chongQingConfig.getEdsUrl(), requestParam.toJSONString());

        if (StringUtils.isNotEmpty(s3BaseInfo)) {
            JSONObject baseObj = JSONObject.parseObject(s3BaseInfo);
            if (HttpStatus.SC_OK == baseObj.getIntValue("resultCode")) {
                JSONArray resArr = baseObj.getJSONArray("rows");
                if (resArr != null && !resArr.isEmpty()) {
                    personInfo = parsePersonInfoByResp(resArr.getJSONObject(0), libId);
                    if (personInfo != null) {
                        PersonInfoCache.getInstance().cachePersonInfo(personId, personInfo);
                    }
                } else {
                    log.error("baseInfo resArr is empty. psersonId {}", personId);
                }
            }
        } else {
            log.error("baseInfo is empty. psersonId {}", personId);
        }

        return personInfo;
    }

    /**
     * 重庆N项目，人员的照片在公安网品高湖，根据personId去获取
     * @param libId
     * @param personId
     * @return
     */
    private String getChongqingPersonImgUrl(String libId, String personId) {
        if (StringUtils.isEmpty(libId) || StringUtils.isEmpty(personId)) {
            log.error("getChongqingPersonImgUrl failed! personId or libId is empty! libId: {}, personId: {}",
                    libId, personId);
            return null;
        }

        String imgUrl = PersonInfoCache.getInstance().getPersonImage(personId);
        if (imgUrl != null) {
            return imgUrl;
        }

        JSONObject requestParam = new JSONObject();
        if (CommonConstant.ChongQingLib.RESIDENT.equals(libId)) { // 常口库
            requestParam.put("dataServiceCode", "cqdsjbedzsy");
        } else if (CommonConstant.ChongQingLib.SECOND_GENERATION_ID_CARD.equals(libId)) { // 二代证库
            requestParam.put("dataServiceCode", "cqdsjbedzsy");
        } else if (CommonConstant.ChongQingLib.RUN_CRIMINAL.equals(libId)) {
            requestParam.put("dataServiceCode", "cqdsjbztry");
        } else if (CommonConstant.ChongQingLib.RUN_CRIMINAL_CHONGQING.equals(libId)) {
            requestParam.put("dataServiceCode", "cqdsjbedzsy");
        } else if (CommonConstant.ChongQingLib.BASE_TERRORIST.equals(libId)) {
            requestParam.put("dataServiceCode", "cqdsjbskry");
        } else if (CommonConstant.ChongQingLib.MAJOR_TERRORIST.equals(libId)) {
            requestParam.put("dataServiceCode", "cqdsjbfkzdrysy");
        } else {
            log.error("getChongqingPersonImgUrl failed! libId illegal: " + libId);
            return null;
        }
        requestParam.put("rowType", "map");
        JSONObject dataServiceParams = new JSONObject();
        dataServiceParams.put("rid", personId);
        requestParam.put("dataServiceParams", dataServiceParams);
        log.info("request image: libId {}, {}", libId, requestParam.toJSONString());
        String imageInfo = HttpUtils.postJsonString(chongQingConfig.getEdsUrl(), requestParam.toJSONString());

        imgUrl = parseImgUrlByResp(personId, imageInfo);
        if (imgUrl != null) {
            PersonInfoCache.getInstance().cachePersonImage(personId, imgUrl);
        }

        return imgUrl;
    }

    private String parseImgUrlByResp(String personId, String imageInfo) {
        if (StringUtils.isEmpty(imageInfo)) {
            log.error("getChongqingPersonImgUrl failed. imageInfo isEmpty. personId: {}.", personId);
            return null;
        }

        JSONObject imgObj = JSONObject.parseObject(imageInfo);
        if (imgObj == null
                || !imgObj.containsKey("resultCode")
                || !imgObj.containsKey("rows")
                || HttpStatus.SC_OK != imgObj.getIntValue("resultCode")
        ) {
            log.error("imageInfo illegal. personId: {}, imageInfo: {}", personId, imageInfo);
            return null;
        }

        JSONArray resArr = imgObj.getJSONArray("rows");
        if (resArr == null || resArr.isEmpty()) {
            log.error("imageInfo is empty. personId: {}, imageInfo: {}", personId, imageInfo);
            return null;
        }
        String imgUrl = null;
        for (int indexResArr = 0; indexResArr < resArr.size(); indexResArr++) {
            // 处理每一条图片记录
            JSONObject obj = resArr.getJSONObject(indexResArr);
            String rId = obj.getString("rid");
            String picPath = obj.getString("pic_path");

            if (StringUtils.isEmpty(picPath)) {
                continue;
            }
            imgUrl = String.format("%s/%s", chongQingConfig.getImageUrl(), picPath);
        }

        return imgUrl;
    }

    private JSONObject parsePersonInfoByResp(JSONObject jsonObject, String libId) {
        JSONObject personObject = new JSONObject();
        personObject.put("PersonID", jsonObject.getString("rid"));
        personObject.put("Name", jsonObject.getString("xm"));
        personObject.put("GenderCode", jsonObject.getString("xb"));
        personObject.put("IDNumber", jsonObject.getString("sfzhm"));
        personObject.put("LibID", libId);
        personObject.put("Addr", jsonObject.getString("csddz"));
        String csrq = jsonObject.getString("csrq");
        if (!StringUtils.isEmpty(csrq)) {
            personObject.put("Birth", DataConvertUtils.formatDate(csrq, "yyyyMMdd", "yyyy-MM-dd"));
        }
        personObject.put("NationalityCode", jsonObject.getString("gj"));
        personObject.put("EthicCode", jsonObject.getString("mz"));
        personObject.put("Tel", jsonObject.getString("ljfs"));

        return personObject;
    }
}
