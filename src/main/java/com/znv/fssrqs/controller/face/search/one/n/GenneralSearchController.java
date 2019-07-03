package com.znv.fssrqs.controller.face.search.one.n;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.ChongQingConfig;
import com.znv.fssrqs.param.face.search.one.n.ExactSearchResultParams;
import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import com.znv.fssrqs.service.face.search.one.n.CommonSearch;
import com.znv.fssrqs.service.face.search.one.n.ExactSearch;
import com.znv.fssrqs.service.face.search.one.n.FastSearch;
import com.znv.fssrqs.util.TimingCounter;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.18 下午1:53
 */

@RestController
@RequestMapping(produces = { "application/json;charset=UTF-8" })
public class GenneralSearchController {

    @Autowired
    private CommonSearch commonSearch;

    @Autowired
    private FastSearch fastSearch;

    @Autowired
    private ExactSearch exactSearch;

    @Autowired
    private ChongQingConfig chongQingConfig;

    @RequestMapping(value = "/VIID/Faces/FaceSearch" ,method = RequestMethod.POST)
    public ResponseVo faceSearch(@RequestHeader("Host") String host,@Validated @RequestBody GeneralSearchParam generalSearchParam) throws IOException {
        int flowRet = TimingCounter.getInstance().isFlowControlled("FaceSearch",
                chongQingConfig.getMaxMinuteFlow(), chongQingConfig.getMaxDayFlow());
        if (flowRet < 0) {
            if (flowRet == -1) {
                ResponseVo retObject = ResponseVo.getInstance(50000, "分钟流量控制!", null);
                return retObject;
            } else if (flowRet == -2) {
                ResponseVo retObject = ResponseVo.getInstance(50000, "天流量控制!", null);
                return retObject;
            }
        }

        if(generalSearchParam.getSimilarityDegree()!=null){
            generalSearchParam.setSimilarityDegree(generalSearchParam.getSimilarityDegree()*0.001);
        }
        JSONObject jsonObject = new JSONObject();
        switch (generalSearchParam.getQueryType()) {
            case 1:
                jsonObject = fastSearch.fastSearch(host,generalSearchParam);
                break;
            case 2:
                if(("-1").equals(generalSearchParam.getAgeLowerLimit().toString())){
                    generalSearchParam.setAgeLowerLimit("0");
                }
                if(generalSearchParam.getAgeUpLimit().toString().equals("-1")){
                    generalSearchParam.setAgeUpLimit(null);
                }
                if(generalSearchParam.getGlass().toString().equals("-1")){
                    generalSearchParam.setGlass(null);
                }
                if(generalSearchParam.getRespirator().toString().equals("-1")){
                    generalSearchParam.setRespirator(null);
                }
                if(generalSearchParam.getSkinColor().toString().equals("-1")){
                    generalSearchParam.setSkinColor(null);
                }
                if(generalSearchParam.getMustache().toString().equals("-1")){
                    generalSearchParam.setMustache(null);
                }
                if(generalSearchParam.getEmotion().toString().equals("-1")){
                    generalSearchParam.setEmotion(null);
                }
                if(generalSearchParam.getEyeOpen().toString().equals("-1")){
                    generalSearchParam.setEyeOpen(null);
                }
                if(generalSearchParam.getMouthOpen().toString().equals("-1")){
                    generalSearchParam.setMouthOpen(null);
                }
                if(generalSearchParam.getGenderType().toString().equals("-1")){
                    generalSearchParam.setGenderType(null);
                }
                jsonObject = exactSearch.startSearch(generalSearchParam);
                break;
            default:
                jsonObject = commonSearch.commonSearch(host,generalSearchParam);
                break;
        }


        return ResponseVo.success(jsonObject);
    }

    @RequestMapping(value = "/VIID/Faces/ExactSearch",method = RequestMethod.POST)
    public ResponseVo getExactSearchResult(@RequestHeader("Host") String host, @Validated @RequestBody  ExactSearchResultParams exactSearchResultParams) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("EventID",exactSearchResultParams.getEventID());
        jsonObject.put("CurrentPage",exactSearchResultParams.getCurrentPage());
        jsonObject.put("PageSize",exactSearchResultParams.getPageSize());
        jsonObject.put("SortField",exactSearchResultParams.getSortField());
        jsonObject.put("SortOrder",exactSearchResultParams.getSortOrder());
        JSONObject ret = exactSearch.queryExactSearchRet(host,jsonObject);

        return  ResponseVo.success(ret);

    }

    //参数转换
    private JSONObject parseObject(String content) {
        JSONObject params = JSON.parseObject(content);
        JSONObject jsonObject = new JSONObject();
        Set<String> keys = params.keySet();
        keys.parallelStream().forEach(key -> {
            Object value = params.get(key);
            if (value instanceof String || value instanceof Integer) {
                if (value == null || String.valueOf(value).equals("-1")) {

                } else {
                    jsonObject.put(key, params.get(key));
                }
            } else {
                jsonObject.put(key, params.get(key));
            }
        });

        return jsonObject;
    }

}
