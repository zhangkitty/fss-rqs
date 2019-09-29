package com.znv.fssrqs.service.personnel.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.HkPersonRelationMap;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.param.personnel.management.PersonListSearchParams;
import com.znv.fssrqs.service.personnel.management.dto.HKPersonListSearchDTO;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VIIDHKSDKService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HkPersonRelationMap hkPersonRelationMap;

    public JSONObject queryHkPerson(PersonListSearchParams personListSearchParams) throws Exception {
        log.info("queryHkPerson params:{}", personListSearchParams);
        HKPersonListSearchDTO hkPersonListSearchDTO = modelMapper.map(personListSearchParams, HKPersonListSearchDTO.class);

        // 海康的接口的入口参数
        JSONObject jsonObject = (JSONObject) JSONObject.parse(JSONObject.toJSONString(hkPersonListSearchDTO));
        Object libId = personListSearchParams.getLibId();
        if (personListSearchParams.getLibId() == null) {
            jsonObject.put("listLibIds", -1);
        } else {
            jsonObject.put("listLibIds", ((List) libId).stream().map(String::valueOf).collect(Collectors.joining(",")));
        }

        Map<String, String> path = new HashMap<>(2);
        path.put(CommonConstant.HkUri.ARTEMIS_PROTOCAL,
                CommonConstant.HkUri.ARTEMIS_PATH + CommonConstant.HkUri.QUERY_PERSON);

        String res = ArtemisHttpUtil.doPostStringArtemis(path,
                JSONObject.toJSONString(jsonObject),
                null, null, "application/json", null);
        log.info("queryHkPerson res:{}", res);
        if (StringUtils.isEmpty(res)) {
            throw new RuntimeException("海康库的响应消息无内容！");
        }
        JSONObject hkResult = JSONObject.parseObject(res);
        if (hkResult == null
                || !hkResult.containsKey("data")
                || !hkResult.containsKey("msg")
                || !hkResult.containsKey("code")) {
            throw new RuntimeException("海康库的响应消息格式不正确！");
        }
        if (0 != hkResult.getIntValue("code")) {
            throw new RuntimeException("海康库的响应失败！错误码："
                    + hkResult.getIntValue("code")
                    + "，错误描述：" + hkResult.getString("msg"));
        }

        JSONArray list = (JSONArray) hkResult.getJSONObject("data").get("list");
        JSONArray data = new JSONArray();
        if (list != null && !list.isEmpty()) {
            list.stream().forEach(obj -> {
                JSONObject jsonObj = (JSONObject) obj;
                JSONObject o = new JSONObject();
                o.put("PersonID", jsonObj.getString("humanId"));
                o.put("Name", jsonObj.getString("humanName"));
                o.put("GenderCode", jsonObj.getInteger("sex"));
                o.put("ImageUrl", jsonObj.getString("facePicUrl"));
                o.put("LibID", jsonObj.getString("listLibId"));
                Double sim = jsonObj.getDouble("similarity");
                if (sim != null && sim.doubleValue() != 0.0) {
                    o.put("Sim", ("" + sim * 100).substring(0, 5) + "%");
                }
                data.add(o);
            });
        }

        JSONObject jsonData = new JSONObject();
        jsonData.put("PersonList", data);
        int total = hkResult.getJSONObject("data").getIntValue("total");
        jsonData.put("TotalRows", total);
        return jsonData;
    }

    public JSONObject queryHkPerson(JSONObject params) {
        Map<String, String> path = new HashMap<>(2);
        path.put(CommonConstant.HkUri.ARTEMIS_PROTOCAL,
                CommonConstant.HkUri.ARTEMIS_PATH + CommonConstant.HkUri.QUERY_PERSON);

        JSONObject requestParams = new JSONObject();

        String humanName = params.getString("person_name");
        if (StringUtils.isNotEmpty(humanName)) {
            requestParams.put("humanName", humanName);
        }

        String imgData = params.getString("imgData");
        if (StringUtils.isNotEmpty(imgData)) {
            requestParams.put("picBase64", imgData);
        }

        String cardId = params.getString("card_id");
        if (StringUtils.isNotEmpty(cardId)) {
            requestParams.put("credentialsNum", cardId);
        }

        String sex = params.getString("sex");
        if (StringUtils.isEmpty(sex)) {
            requestParams.put("sex", -1);
        } else {
            requestParams.put("sex", Integer.valueOf(sex));
        }
        Object libId = params.get("lib_id");
        if (libId == null) {
            requestParams.put("listLibIds", -1);
        } else {
            requestParams.put("listLibIds", ((List) libId).stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        int from = params.getIntValue("from");
        int size = params.getIntValue("size");
        requestParams.put("pageNo", (from + size) / size);
        requestParams.put("pageSize", size);

        String beginBirthDate = params.getString("beginBirthDate");
        String endBirthDate = params.getString("endBirthDate");
        if (StringUtils.isNotEmpty(beginBirthDate) && StringUtils.isNotEmpty(endBirthDate)) {
            requestParams.put("beginBirthDate", beginBirthDate);
            requestParams.put("endBirthDate", endBirthDate);
        }
        Integer credentialsType = params.getInteger("credentialsType");
        if (credentialsType != null) {
            requestParams.put("credentialsType", credentialsType);
        }

        Double sim_threshold = params.getDouble("sim_threshold");
        if (sim_threshold != null) {
            requestParams.put("similarityMin", sim_threshold);
            requestParams.put("similarityMax", 1.0);
        }

        String res = ArtemisHttpUtil.doPostStringArtemis(path, JSONObject.toJSONString(requestParams), null, null, "application/json", null);

        log.info("queryHkPerson res:{}", res);
        if (StringUtils.isEmpty(res)) {
            throw ZnvException.error("HKAccessFailed");
        }
        JSONObject hkResult = JSONObject.parseObject(res);
        if (hkResult == null
                || !hkResult.containsKey("data")
                || !hkResult.containsKey("msg")
                || !hkResult.containsKey("code")) {
            log.error("海康库的响应失败！");
            throw ZnvException.error("HKReturnError");
        }
        if (0 != hkResult.getIntValue("code")) {
            log.error("海康库的响应失败！错误码：{}，错误描述：{}.",
                    hkResult.getIntValue("code"),
                    hkResult.getString("msg"));
            throw ZnvException.error("HKReturnError");
        }

        JSONArray list = (JSONArray) hkResult.getJSONObject("data").get("list");
        JSONArray data = new JSONArray();
        if (list != null && !list.isEmpty()) {
            list.stream().forEach(obj -> {
                JSONObject jsonObj = (JSONObject) obj;
                JSONObject o = new JSONObject();

                o.put("AlgorithmType", 0);
                String hkPersonId = jsonObj.getString("humanId");
                o.put("RelatedPersonID", hkPersonId);
                Map<String, Object> map = hkPersonRelationMap.getByHkPersonId(hkPersonId);
                if (map != null && !map.isEmpty()) {
                    // PersonID作为交集使用
                    o.put("PersonID", (String) map.get("fss_person_id"));
                }

                o.put("Name", jsonObj.getString("humanName"));
                o.put("GenderCode", jsonObj.getInteger("sex"));
                o.put("ImageUrl", jsonObj.getString("facePicUrl"));
                o.put("LibID", jsonObj.getString("listLibId"));
                Double sim = jsonObj.getDouble("similarity");
                if (sim != null && sim.doubleValue() != 0.0) {
                    o.put("Sim", ("" + sim * 100).substring(0, 5) + "%");
                }
                data.add(o);
            });
        }

        JSONObject jsonData = new JSONObject();
        jsonData.put("PersonList", data);
        int total = hkResult.getJSONObject("data").getIntValue("total");
        jsonData.put("TotalRows", total);
        return jsonData;
    }

    public JSONObject addHkPerson(JSONObject params, String hkLlibId) {
        Map<String, String> path = new HashMap<>(2);
        path.put(CommonConstant.HkUri.ARTEMIS_PROTOCAL,
                CommonConstant.HkUri.ARTEMIS_PATH + CommonConstant.HkUri.ADD_PERSON);
        JSONObject requestParams = new JSONObject();
        requestParams.put("humanName", params.getString("Name"));
        JSONArray jsonImageArray = params.getJSONArray("SubImageList");
        requestParams.put("picBase64", jsonImageArray.getJSONObject(0).getString("Data"));
        requestParams.put("birthday", params.getString("Birth"));
        requestParams.put("humanAddress", params.getString("Addr"));
        requestParams.put("sex", params.getIntValue("Sex"));
        String credentialsNum = params.getString("IDNumber");
        if (StringUtils.isNotEmpty(credentialsNum)) {
            requestParams.put("credentialsNum", credentialsNum);
            requestParams.put("credentialsType", 1); // 0-未知，1-身份证，2-警官证
        }
        requestParams.put("listLibId", hkLlibId);
        String res = ArtemisHttpUtil.doPostStringArtemis(path, JSONObject.toJSONString(requestParams), null, null, "application/json", null);
        log.info("addHkPerson res:{}", res);
        if (StringUtils.isEmpty(res)) {
            return null;
        }
        JSONObject restPerson = JSONObject.parseObject(res);
        return restPerson;
    }

    public JSONObject delHkPerson(String id) {
        log.info("delHkPerson ids:{}", id);
        Map<String, String> path = new HashMap<>(2);
        path.put(CommonConstant.HkUri.ARTEMIS_PROTOCAL,
                CommonConstant.HkUri.ARTEMIS_PATH + CommonConstant.HkUri.DEL_PERSON);
        JSONObject requestParams = new JSONObject();
        List<String> error = Lists.newArrayList();
        requestParams.put("humanId", id);
        String res = ArtemisHttpUtil.doPostStringArtemis(path, JSONObject.toJSONString(requestParams), null, null, "application/json", null);
        log.info("delHkPerson res:{}", res);
        if (StringUtils.isEmpty(res)) {
            return null;
        }
        JSONObject restPerson = JSONObject.parseObject(res);
        return restPerson;
    }
}
