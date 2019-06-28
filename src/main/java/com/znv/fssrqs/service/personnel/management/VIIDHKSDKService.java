package com.znv.fssrqs.service.personnel.management;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.znv.fssrqs.common.Consts;
import com.znv.fssrqs.param.personnel.management.PersonListSearchParams;
import com.znv.fssrqs.service.personnel.management.dto.HKPersonListSearchDTO;
import com.znv.fssrqs.util.WriteNullListAsEmptyFilter;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VIIDHKSDKService {

    @Autowired
    private ModelMapper modelMapper;

    public JSONObject queryHkPerson (PersonListSearchParams personListSearchParams) throws Exception{
        log.info("queryHkPerson params:{}", personListSearchParams);
        HKPersonListSearchDTO hkPersonListSearchDTO = modelMapper.map(personListSearchParams,HKPersonListSearchDTO.class);

        // 海康的接口的入口参数
        JSONObject jsonObject = (JSONObject)JSONObject.parse(JSONObject.toJSONString(hkPersonListSearchDTO));
        Object libId = personListSearchParams.getLibId();
        if (personListSearchParams.getLibId() == null) {
            jsonObject.put("listLibIds", -1);
        } else {
            jsonObject.put("listLibIds", ((List) libId).stream().map(String::valueOf).collect(Collectors.joining(",")));
        }

        //返回值
        JSONObject ret = new JSONObject();

        Map<String, String> path = ImmutableMap.<String, String>builder().put(Consts.HKURI.ARTEMIS_PROTOCAL, Consts.HKURI.ARTEMIS_PATH + Consts.HKURI.QUERY_PERSON).build();

        String res = ArtemisHttpUtil.doPostStringArtemis(path, JSONObject.toJSONString(jsonObject), null, null, "application/json", null);
        log.info("queryHkPerson res:{}", res);

        if (StringUtils.isEmpty(res)) {
            throw new Exception("hk response is null");
        }

        //todo
        //这里的适配可能要做一些改变
        ResponseVo restPerson = JSONObject.parseObject(res, ResponseVo.class);

        if (restPerson == null || Consts.HkSdkErrCode.ERROR == Integer.valueOf(restPerson.getCode()) ) {
            throw new Exception("hk person is null or error");
        }

        JSONArray list =  ((JSONObject)restPerson.getData()).getJSONArray("list");
        JSONArray data = new JSONArray();
        if (list != null && !list.isEmpty()) {
            list.stream().forEach(obj -> {
                JSONObject jsonObj = (JSONObject) obj;
                JSONObject o = new JSONObject();
                o.put(Consts.FinalKeyCode.FCPID, jsonObj.getString("humanId"));
                o.put(Consts.FinalKeyCode.PERSONNAME, jsonObj.getString("humanName"));
                o.put("age", jsonObj.getInteger("age"));
                o.put("sex", jsonObj.getInteger("sex"));
                o.put(Consts.FinalKeyCode.PERSONID, jsonObj.getString("credentialsNum"));
                o.put(Consts.FinalKeyCode.CONTROLLEVEL, jsonObj.getString("listLibId"));
                o.put(Consts.FinalKeyCode.IMG_URL, jsonObj.getString("facePicUrl"));
                Double sim = jsonObj.getDouble("similarity");
                if (sim != null && sim.doubleValue() != 0.0) {
                    o.put("sim", ("" + sim * 100).substring(0, 5) + "%");
                }
                JSONObject extData = new JSONObject();
                extData.put("age", jsonObj.getInteger("age"));
                extData.put("sublib", jsonObj.getString("listLibId"));
                o.put(Consts.FinalKeyCode.IMAGE_NAME, JSON.toJSONString(extData, new WriteNullListAsEmptyFilter()));
                data.add(o);
            });
        }

        ret.put("curPage", ((JSONObject)restPerson.getData()).getIntValue("pageNo"));
        ret.put("data", data);
        ret.put(Consts.FssSdkKeyCode.ERROR_CODE, Consts.HkSdkErrCode.SUCCESS);
        ret.put("pageSize", ((JSONObject)restPerson.getData()).getIntValue("pageSize"));
        ret.put(Consts.FinalKeyCode.SUCCESS, true);
        int total = ((JSONObject)restPerson.getData()).getIntValue("total");
//        int i = total % size;
//        int j = total / size;
//        if (i == 0) {
//            ret.put("totalPages", j);
//        } else {
//            ret.put("totalPages", j + 1);
//        }

        ret.put("totalRows", total);
        return ret;

    }


    public JSONObject addHkPerson(JSONObject params, String hkLlibId) {
        Map<String, String> path = ImmutableMap.<String, String>builder().put(Consts.HKURI.ARTEMIS_PROTOCAL, Consts.HKURI.ARTEMIS_PATH + Consts.HKURI.ADD_PERSON).build();
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
        Map<String, String> path = ImmutableMap.<String, String>builder().put(Consts.HKURI.ARTEMIS_PROTOCAL, Consts.HKURI.ARTEMIS_PATH + Consts.HKURI.DEL_PERSON).build();
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
