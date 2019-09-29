package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.ChongQingConfig;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.param.personnel.management.PersonListSearchParams;
import com.znv.fssrqs.service.hbase.PhoenixService;
import com.znv.fssrqs.service.personnel.management.PersonListService;
import com.znv.fssrqs.service.personnel.management.VIIDHKSDKService;
import com.znv.fssrqs.service.personnel.management.VIIDPersonService;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.FeatureCompUtil;
import com.znv.fssrqs.util.I18nUtils;
import com.znv.fssrqs.util.TimingCounter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dongzelong on  2019/6/1 13:53.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
@RequestMapping(produces = {"application/json;charset=UTF-8"})
@Slf4j
public class PersonController {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private PhoenixService phoenixService;
    @Autowired
    private PersonListService personListService;
    @Autowired
    private VIIDPersonService personService;
    @Autowired
    private VIIDHKSDKService viidhksdkService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ChongQingConfig chongQingConfig;
//    @GetMapping(value="/VIID/Persons")
//    public ResponseVo getPersonList(@RequestHeader("Host") String host, @Valid PersonListSearchParams personListSearchParams) throws Exception {
//        if(personListSearchParams.getAlgorithmType()!=null&&personListSearchParams.getAlgorithmType().equals("1")){
//            JSONObject result =  viidhksdkService.queryHkPerson(personListSearchParams);
//            return ResponseVo.success(result);
//        }
//        JSONObject jsonObject = personListService.getPersonList(host, personListSearchParams);
//        return ResponseVo.success(jsonObject);
//    }


    /**
     * 批量激活人员接口,支持整库激活
     */
    @PostMapping("/batch/active/control/persons")
    public String batchControl(@RequestBody String body) {
        JSONObject params = JSON.parseObject(body);
        if (params.getInteger("PersonLibType") != null) {
            return batchControlByLibId(params).toJSONString();
        } else {
            return batchControlPersons(params).toJSONString();
        }
    }

    /**
     * 勾选人员进行布控
     */
    private JSONObject batchControlPersons(JSONObject params) {
        String tableName = HdfsConfigManager.getString("fss.phoenix.table.blacklist.name");
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        JSONObject insertData = new JSONObject();
        JSONObject personData = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray pIDs = params.getJSONArray("PIDs");
        int flag = params.getInteger("Flag");
        String startTime = params.getString("StartTime");
        String endTime = params.getString("EndTime");
        JSONArray errorArray = new JSONArray();
        pIDs.forEach(id -> {
            String[] spl = ((String) id).split(":");
            String personId = spl[0];
            int libId = Integer.parseInt(spl[1]);
            data.put("person_id", personId);
            data.put("flag", flag);
            data.put("control_start_time", startTime);
            data.put("control_end_time", endTime);
            data.put("modify_time", currentDate);
            personData.put("lib_id", libId);
            personData.put("original_lib_id", libId);
            personData.put("data", data);
            insertData.put("id", "31001");
            insertData.put("table_name", tableName);
            insertData.put("data", personData);
            try {
                phoenixService.update(insertData);
            } catch (Exception e) {
                errorArray.add(id);
            }
        });

        if (errorArray.size() == 0) {
            return FastJsonUtils.JsonBuilder.ok().json();
        } else {
            return FastJsonUtils.JsonBuilder.badRequest(415).property("PersonIDs", errorArray).json();
        }
    }

    /**
     * 布控整库
     */
    private JSONObject batchControlByLibId(JSONObject params) {
        JSONObject data = new JSONObject();
        data.put("lib_id", params.getInteger("LibID"));
        data.put("flag", params.getInteger("Flag"));
        data.put("personlib_type", params.getInteger("PersonLibType"));
        data.put("control_end_time", params.getString("EndTime"));
        data.put("control_start_time", params.getString("StartTime"));
        String tableName = HdfsConfigManager.getTableName("fss.phoenix.table.blacklist.name");
        JSONObject insertData = new JSONObject();
        insertData.put("id", "31011");
        insertData.put("table_name", tableName);
        insertData.put("data", data);
        phoenixService.update(insertData);
        return FastJsonUtils.JsonBuilder.ok().json();
    }


    @PostMapping(value = "/VIID/Persons")
    public JSONObject PostPersonList(@RequestHeader("Host") String host, @RequestBody String json) throws Exception {
        PersonListSearchParams personListSearchParams = modelMapper.map(JSONObject.parseObject(json), PersonListSearchParams.class);
        return getPersonList(host, personListSearchParams);
    }

    @GetMapping(value = "/VIID/Persons")
    public JSONObject getPersonList(@RequestHeader("Host") String host, @Valid PersonListSearchParams personListSearchParams) {
        // ！！暂时先这样写，因为上面的代码返回的值少了
        Map<String, Object> mapParam = new HashMap<>();
        if (personListSearchParams.getStartTime() != null) {
            mapParam.put("StartTime", personListSearchParams.getStartTime());
        }
        if (personListSearchParams.getEndTime() != null) {
            mapParam.put("EndTime", personListSearchParams.getEndTime());
        }
        if (personListSearchParams.getLibId() != null) {
            mapParam.put("LibId", personListSearchParams.getLibId());
        }
        if (personListSearchParams.getIsDel() != null) {
            mapParam.put("IsDel", personListSearchParams.getIsDel());
        }
        mapParam.put("CurrentPage", personListSearchParams.getCurrentPage());
        mapParam.put("PageSize", personListSearchParams.getPageSize());
        if (personListSearchParams.getName() != null) {
            mapParam.put("Name", personListSearchParams.getName());
        }
        if (personListSearchParams.getIDNumber() != null) {
            mapParam.put("IDNumber", personListSearchParams.getIDNumber());
        }
        if (personListSearchParams.getAlgorithmType() != null) {
            mapParam.put("AlgorithmType", personListSearchParams.getAlgorithmType());
        }
        if (personListSearchParams.getImgData() != null) {
            mapParam.put("ImgData", personListSearchParams.getImgData());
        }
        if (personListSearchParams.getSimThreshold() != null) {
            mapParam.put("SimThreshold", personListSearchParams.getSimThreshold());
        }
        return getPersonListByPost(host, mapParam);
    }

    /**
     * ！！从老模块移植过来，待优化！！
     * 人员查询（含静态库1：N检索）
     *
     * @param host
     * @param mapParam
     * @return
     */
    @PostMapping(value = "/VIID/QueryPersons")
    public JSONObject getPersonListByPost(@RequestHeader("Host") String host, @RequestBody Map mapParam) {
        JSONObject retObject = new JSONObject();
        JSONArray personList = new JSONArray();
        retObject.put("Data", personList);
        int flowRet = TimingCounter.getInstance().isFlowControlled("FaceSearch", chongQingConfig.getMaxMinuteFlow(), chongQingConfig.getMaxDayFlow());
        if (flowRet < 0) {
            if (flowRet == -1) {
                retObject.put("Code", 50000);
                retObject.put("Message", "分钟流量控制!");
                return retObject;
            } else if (flowRet == -2) {
                retObject.put("Code", 50000);
                retObject.put("Message", "天流量控制!");
                return retObject;
            }
        }

        if (mapParam == null
                || !mapParam.containsKey("CurrentPage")
                || !mapParam.containsKey("PageSize")) {
            retObject.put("Code", 20000);
            retObject.put("Message", "参数错误，CurrentPage或PageSize为空!");
            return retObject;
        }

        int size = -1;
        if (mapParam.get("PageSize") instanceof Integer) {
            size = (Integer) mapParam.get("PageSize");
        } else if (mapParam.get("PageSize") instanceof String) {
            size = Integer.valueOf((String) mapParam.get("PageSize"));
        }

        if (size < 0 || size > 100) {
            retObject.put("Code", 20000);
            retObject.put("Message", "参数错误，PageSize非法!");
            return retObject;
        }

        int from = -1;
        if (mapParam.get("CurrentPage") instanceof Integer) {
            from = ((Integer) (mapParam.get("CurrentPage")) - 1) * size;
        } else if (mapParam.get("CurrentPage") instanceof String) {
            from = (Integer.valueOf((String) mapParam.get("CurrentPage")) - 1) * size;
        }
        if (from < 0) {
            retObject.put("Code", 20000);
            retObject.put("Message", "参数错误，CurrentPage非法!");
            return retObject;
        }


        if (mapParam.containsKey("LibID")) {
            if (!(mapParam.get("LibID") instanceof List)) {
                retObject.put("Code", 20000);
                retObject.put("Message", "参数错误，LibID需要为数组!");
                return retObject;
            }
        }

        JSONObject transformedParams = new JSONObject();
        transformedParams.put("from", from);
        transformedParams.put("size", size);
        if (mapParam.containsKey("StartTime")) {
            transformedParams.put("start_time", mapParam.get("StartTime"));
        }
        if (mapParam.containsKey("EndTime")) {
            transformedParams.put("end_time", mapParam.get("EndTime"));
        }
        if (mapParam.containsKey("IsDel")) {
            transformedParams.put("is_del", mapParam.get("IsDel"));
        }
        if (mapParam.containsKey("Name")) {
            transformedParams.put("person_name", mapParam.get("Name"));
        }
        if (mapParam.containsKey("IDNumber")) {
            transformedParams.put("person_name", mapParam.get("IDNumber")); // 老接口ID搜索也是传的这个字段
        }
        if (mapParam.containsKey("Addr")) {
            transformedParams.put("addr", mapParam.get("Addr"));
        }
        if (mapParam.containsKey("GenderCode")) {
            transformedParams.put("sex", mapParam.get("GenderCode"));
        }
        if (mapParam.containsKey("LibID") && !((ArrayList) mapParam.get("LibID")).isEmpty()) {
            transformedParams.put("lib_id", mapParam.get("LibID"));
            transformedParams.put("is_lib", true);
        } else {
            transformedParams.put("is_lib", false);
        }

        transformedParams.put("minimum_should_match", 1);
        transformedParams.put("lib_aggregation", true);
        transformedParams.put("order_type", "desc");
        if (mapParam.containsKey("ImgData")
                && mapParam.containsKey("SimThreshold")) {
            if (!(mapParam.get("SimThreshold") instanceof Double)) {
                throw ZnvException.badRequest("RequestException", "SimThreshold");
            }
            FeatureCompUtil fc = new FeatureCompUtil();
            fc.setFeaturePoints(HdfsConfigManager.getPoints());
            transformedParams.put("imgData", mapParam.get("ImgData"));
            transformedParams.put("sim_threshold",
                    fc.reversalNormalize(((Double) mapParam.get("SimThreshold")).floatValue()));
            transformedParams.put("feature_name", "feature.feature_high");
            transformedParams.put("is_calcSim", true);
        } else {
            transformedParams.put("sort_order2", "desc");
            transformedParams.put("sort_order1", "desc");
            transformedParams.put("sort_field2", "person_id");
            transformedParams.put("sort_field1", "modify_time");
        }

        retObject.put("Code", 10000);
        retObject.put("Message", "ok");

        JSONObject data = null;
        if (mapParam.containsKey("AlgorithmType")) {
            List<Integer> algorithmType = (List) mapParam.get("AlgorithmType");
            if (algorithmType.size() > 1) {
                Integer isAlgorithmIntersection = (Integer) mapParam.get("IsAlgorithmIntersection");
                data = multiAlgorithmPersonList(host, transformedParams, algorithmType, isAlgorithmIntersection);
                retObject.put("Data", data);
                return retObject;
            } else if (algorithmType.size() > 0) {
                switch (algorithmType.get(0)) {
                    case 0: {// 默认算法
                        data = personListService.getPersonList(host, transformedParams);
                        break;
                    }
                    case 1: {// 海康算法
                        data = viidhksdkService.queryHkPerson(transformedParams);
                        break;
                    }
                    default:
                        break;
                }

                retObject.put("Data", data);
                return retObject;
            }
        }

        data = personListService.getPersonList(host, transformedParams);
        retObject.put("Data", data);
        return retObject;
    }

    private JSONObject multiAlgorithmPersonList(String host, JSONObject params, List<Integer> algorithmType, Integer isAlgorithmIntersection) {
        JSONObject data = new JSONObject();
        for (int i = 0; i < algorithmType.size(); i++) {
            switch (algorithmType.get(i)) {
                case 0: {// 默认算法
                    data.put(String.valueOf(i), personListService.getPersonList(host, params));
                    break;
                }
                case 1: {// 海康算法
                    data.put(String.valueOf(i), viidhksdkService.queryHkPerson(params));
                    break;
                }
                default:
                    break;
            }
        }

        JSONObject ret = new JSONObject();
        if (isAlgorithmIntersection != null && 1 == isAlgorithmIntersection) {
            Map<String, JSONObject> personIdMap = new HashMap<>();
            int algorithmNumber = algorithmType.size();
            // 交集，先遍历一轮，对personId计数
            for (int i = 0; i < algorithmNumber; i++) {
                if (data.containsKey(String.valueOf(i)) &&
                        data.getJSONObject(String.valueOf(i)).containsKey("PersonList")) {
                    JSONArray personList = data.getJSONObject(String.valueOf(i)).getJSONArray("PersonList");
                    for (Object person : personList) {
                        JSONObject personObject = (JSONObject) person;
                        String personId = personObject.getString("PersonID");
                        if (personIdMap.containsKey(personId)) {
                            JSONObject personsObject = personIdMap.get(personId);
                            Integer count = personIdMap.get(personId).getInteger("count");
                            count += 1;
                            personsObject.put("count", count);
                            personsObject.put(String.valueOf(i), personObject);
                            personIdMap.put(personId, personsObject);
                        } else {
                            JSONObject personsObject = new JSONObject();
                            personsObject.put("count", new Integer(1));
                            personsObject.put(String.valueOf(i), personObject);
                            personIdMap.put(personId, personsObject);
                        }
                    }
                }
            }

            // 以默认算法为基础，如果personId的计数为算法数量，则返回结果
            JSONArray personList = new JSONArray();
            if (data.containsKey(String.valueOf(0)) &&
                    data.getJSONObject(String.valueOf(0)).containsKey("PersonList")) {
                JSONArray personListBase = data.getJSONObject(String.valueOf(0)).getJSONArray("PersonList");
                for (Object person : personListBase) {
                    JSONObject personObject = (JSONObject) person;
                    String personId = personObject.getString("PersonID");
                    if (personIdMap.containsKey(personId) && personIdMap.get(personId).getIntValue("count") >= algorithmNumber) {
                        JSONObject personsObject = personIdMap.get(personId);

                        for (int j = 0; j < algorithmNumber; j++) {
                            personList.add(personsObject.getJSONObject(String.valueOf(j)));
                        }
                    }
                }
            }

            JSONObject jsonData = new JSONObject();
            jsonData.put("PersonList", personList);
            jsonData.put("TotalRows", personList.size() / algorithmType.size());
            return jsonData;
        } else {
            JSONArray personList = new JSONArray();
            Integer totalRows = 0;
            for (int i = 0; i < algorithmType.size(); i++) {
                personList.addAll(data.getJSONObject(String.valueOf(i)).getJSONArray("PersonList"));
                if (data.getJSONObject(String.valueOf(i)).getJSONArray("PersonList").size() > totalRows) {
                    totalRows = data.getJSONObject(String.valueOf(i)).getJSONArray("PersonList").size();
                }
            }
            JSONObject jsonData = new JSONObject();
            jsonData.put("PersonList", personList);
            jsonData.put("TotalRows", totalRows);
            return jsonData;
        }
    }

    @GetMapping(value = "/VIID/Person/{LibID}/{PersonID}")
    public JSONObject getPerson(@RequestHeader("Host") String host,
                                @PathVariable("LibID") String LibID,
                                @PathVariable("PersonID") String PersonID) {
        JSONObject retObject = new JSONObject();
        retObject.put("Data", "");
        try {
            JSONObject data = personListService.getPerson(host, LibID, PersonID);
            retObject.put("Data", data);
            retObject.put("Code", 10000);
            retObject.put("Message", "ok");
            return retObject;
        } catch (Exception e) {
            log.error("", e);
            retObject.put("Code", 50000);
            retObject.put("Message", e.getMessage());
            return retObject;
        }
    }

    @PostMapping(value = "/VIID/Person")
    public JSONObject addPerson(@RequestBody String body) {
        JSONObject retObject = new JSONObject();
        retObject.put("Data", "");
        JSONObject jsonObject = FastJsonUtils.toJSONObject(body);
        if (jsonObject == null
                || !jsonObject.containsKey("Person")
        ) {
            retObject.put("Code", 20000);
            retObject.put("Message", "新增失败，Person为空!");
            return retObject;
        }

        JSONObject data = personService.addPerson(jsonObject.getJSONObject("Person"));
        retObject.put("ResponseStatus", data);
        JSONObject responseStatus = new JSONObject();
        responseStatus.put("ResponseStatus", data);
        retObject.put("Data", responseStatus);
        retObject.put("Code", 10000);
        retObject.put("Message", "ok");
        return retObject;
    }

    @PutMapping(value = "/VIID/Person")
    public JSONObject updatePerson(@RequestBody String body) {
        JSONObject retObject = new JSONObject();
        retObject.put("Data", "");
        JSONObject jsonObject = FastJsonUtils.toJSONObject(body);
        if (jsonObject == null
                || !jsonObject.containsKey("Person")
        ) {
            retObject.put("Code", 20000);
            retObject.put("Message", "更新失败，Person为空!");
            return retObject;
        }

        JSONObject data = personService.updatePerson(jsonObject.getJSONObject("Person"));
        retObject.put("ResponseStatus", data);
        JSONObject responseStatus = new JSONObject();
        responseStatus.put("ResponseStatus", data);
        retObject.put("Data", responseStatus);
        retObject.put("Code", 10000);
        retObject.put("Message", "ok");
        return retObject;
    }

    @DeleteMapping(value = "/VIID/Person")
    public JSONObject deletePerson(@RequestParam Map mapParam) {
        JSONObject retObject = new JSONObject();
        retObject.put("Code", 20000);
        if (mapParam == null
                || !mapParam.containsKey("LibID")
                || !mapParam.containsKey("PersonID")) {
            retObject.put("Message", "错误, LibID和PersonID不能为空!");
            return retObject;
        }

        String LibID = (String) mapParam.get("LibID");
        String PersonID = (String) mapParam.get("PersonID");
        JSONObject data = personService.deletePerson(LibID, PersonID);
        retObject.put("ResponseStatus", data);
        retObject.put("Code", 10000);
        retObject.put("Message", "ok");
        return retObject;
    }

    @DeleteMapping(value = "/VIID/Persons")
    public JSONObject deletePersons(@RequestParam Map mapParam) {
        JSONObject retObject = new JSONObject();
        retObject.put("Code", 20000);
        if (mapParam == null
                || !mapParam.containsKey("LibID")
                || !mapParam.containsKey("IDList")) {
            retObject.put("Message", "错误, LibID和IDList不能为空!");
            return retObject;
        }
        String libID = (String) mapParam.get("LibID");
        String[] personIDs = ((String) mapParam.get("IDList")).split(",");
        if (personIDs.length < 1) {
            retObject.put("Message", "错误, IDList不能为空!");
            return retObject;
        }

        JSONObject data = new JSONObject();
        JSONObject responseStatusList = new JSONObject();
        JSONArray statusObjectArray = new JSONArray();
        responseStatusList.put("ResponseStatusObject", statusObjectArray);
        data.put("ResponseStatusList", responseStatusList);
        retObject.put("Data", data);
        for (String personID : personIDs) {
            JSONObject deleteStatusObject = personService.deletePerson(libID, personID);
            statusObjectArray.add(deleteStatusObject);
        }
        retObject.put("Code", 10000);
        retObject.put("Message", "ok");
        retObject.put("ResponseStatusList", responseStatusList);
        return retObject;
    }

    /**
     * 批量删除人员
     */
    @PostMapping("/batch/delete/persons")
    @Transactional(transactionManager = "transactionManager")
    public JSONObject deletePersons(@RequestBody String body, HttpServletRequest request) {
        List<JSONObject> persons = JSON.parseArray(body, JSONObject.class);
        if (persons.size() == 0) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "PersonListEmpty");
        }
        List<JSONObject> result = personService.batchDeletePersons(persons);
        if (result.size() > 0) {
            return FastJsonUtils.JsonBuilder.error(CommonConstant.StatusCode.INTERNAL_ERROR, I18nUtils.i18n(request.getLocale(), "PartPersonDeleteSuccess")).json();
        }
        return FastJsonUtils.JsonBuilder.ok().json();
    }
}
