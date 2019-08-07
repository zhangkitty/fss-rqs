package com.znv.fssrqs.controller.personnel.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.ChongQingConfig;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.param.personnel.management.PersonListSearchParams;
import com.znv.fssrqs.service.personnel.management.PersonListService;
import com.znv.fssrqs.service.personnel.management.VIIDHKSDKService;
import com.znv.fssrqs.service.personnel.management.VIIDPersonService;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.FeatureCompUtil;
import com.znv.fssrqs.util.TimingCounter;
import com.znv.fssrqs.vo.ResponseVo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.06.10 下午3:05
 */

@RestController
@RequestMapping(produces = { "application/json;charset=UTF-8" })
public class PersonListController {

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


    @RequestMapping(value="/VIID/Persons",method = RequestMethod.POST)
    public JSONObject PostPersonList(@RequestHeader("Host") String host, @RequestBody String json) throws Exception {

        PersonListSearchParams personListSearchParams = modelMapper.map(JSONObject.parseObject(json),PersonListSearchParams.class);

        return getPersonList(host, personListSearchParams);
    }

    @GetMapping(value="/VIID/Persons")
    public JSONObject getPersonList(@RequestHeader("Host") String host,
                                    @Valid PersonListSearchParams personListSearchParams) throws Exception {
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
     * @param host
     * @param mapParam
     * @return
     */
    @RequestMapping(value = "/VIID/QueryPersons", method = RequestMethod.POST)
    public JSONObject getPersonListByPost(@RequestHeader("Host") String host,
                                          @RequestBody Map mapParam
    ){
        JSONObject retObject = new JSONObject();
        JSONArray personList = new JSONArray();
        retObject.put("Data", personList);

        int flowRet = TimingCounter.getInstance().isFlowControlled("FaceSearch",
                chongQingConfig.getMaxMinuteFlow(), chongQingConfig.getMaxDayFlow());
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
        if ( mapParam.get("PageSize") instanceof Integer) {
            size = (Integer)mapParam.get("PageSize");
        } else if (mapParam.get("PageSize") instanceof String) {
            size = Integer.valueOf((String)mapParam.get("PageSize"));
        }

        if (size < 0 || size > 100){
            retObject.put("Code", 20000);
            retObject.put("Message", "参数错误，PageSize非法!");
            return retObject;
        }

        int from = -1;
        if ( mapParam.get("CurrentPage") instanceof Integer) {
            from = ((Integer)(mapParam.get("CurrentPage")) - 1) * size;
        } else if (mapParam.get("CurrentPage") instanceof String) {
            from = (Integer.valueOf((String)mapParam.get("CurrentPage")) - 1) * size;
        }
        if (from < 0) {
            retObject.put("Code", 20000);
            retObject.put("Message", "参数错误，CurrentPage非法!");
            return retObject;
        }


        if (mapParam.containsKey("LibID")) {
            if (! (mapParam.get("LibID") instanceof List)) {
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
        if (mapParam.containsKey("LibID")) {
            transformedParams.put("lib_id", mapParam.get("LibID"));
            transformedParams.put("is_lib", true);
        }

        transformedParams.put("minimum_should_match",1);
        transformedParams.put("lib_aggregation",true);
        transformedParams.put("order_type","desc");
        if (mapParam.containsKey("ImgData")
                && mapParam.containsKey("SimThreshold") ) {
            FeatureCompUtil fc = new FeatureCompUtil();
            fc.setFeaturePoints(HdfsConfigManager.getPoints());
            transformedParams.put("imgData", mapParam.get("ImgData"));
            transformedParams.put("sim_threshold",
                    fc.reversalNormalize(((Double)mapParam.get("SimThreshold")).floatValue()) );
            transformedParams.put("feature_name","feature.feature_high");
            transformedParams.put("is_calcSim", true);
        } else {
            transformedParams.put("sort_order2","desc");
            transformedParams.put("sort_order1","desc");
            transformedParams.put("sort_field2","person_id");
            transformedParams.put("sort_field1","modify_time");
        }

        retObject.put("Code", 10000);
        retObject.put("Message", "ok");

        JSONObject data = null;
        if (mapParam.containsKey("AlgorithmType")) {
            Integer algorithmType = (Integer) mapParam.get("AlgorithmType");
            if (1 == algorithmType) {
                try {
                    data = viidhksdkService.queryHkPerson(transformedParams);
                    retObject.put("Data", data);
                    return retObject;
                } catch (Exception e) {
                    retObject.put("Code", 50000);
                    retObject.put("Message", e.getMessage());
                    return retObject;
                }
            }
        }

        try {
            data = personListService.getPersonList(host, transformedParams);
            retObject.put("Data", data);
            return retObject;
        } catch (Exception e) {
            retObject.put("Code", 50000);
            retObject.put("Message", e.getMessage());
            return retObject;
        }
    }

    @RequestMapping(value = "/VIID/Person/{LibID}/{PersonID}", method = RequestMethod.GET)
    public JSONObject getPerson(@RequestHeader("Host") String host,
                                @PathVariable("LibID") String LibID,
                                @PathVariable("PersonID") String PersonID){
        JSONObject retObject = new JSONObject();
        retObject.put("Data", "");
        try {
            JSONObject data = personListService.getPerson(host, LibID, PersonID);
            retObject.put("Data", data);
            retObject.put("Code", 10000);
            retObject.put("Message", "ok");
            return retObject;
        } catch (Exception e) {
            retObject.put("Code", 50000);
            retObject.put("Message", e.getMessage());
            return retObject;
        }
    }

    @RequestMapping(value = "/VIID/Person", method = RequestMethod.POST)
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

    @RequestMapping(value = "/VIID/Person", method = RequestMethod.PUT)
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

        String LibID = (String)mapParam.get("LibID");
        String PersonID = (String)mapParam.get("PersonID");
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
        String libID = (String)mapParam.get("LibID");
        String[] personIDs = ((String)mapParam.get("IDList")).split(",");
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

}
