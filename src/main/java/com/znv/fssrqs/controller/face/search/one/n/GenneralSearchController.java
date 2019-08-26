package com.znv.fssrqs.controller.face.search.one.n;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.ChongQingConfig;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.param.face.search.one.n.ExactSearchResultParams;
import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import com.znv.fssrqs.service.face.search.one.n.CommonSearch;
import com.znv.fssrqs.service.face.search.one.n.ExactSearch;
import com.znv.fssrqs.service.face.search.one.n.FastSearch;
import com.znv.fssrqs.service.hbase.PhoenixService;
import com.znv.fssrqs.util.FeatureCompUtil;
import com.znv.fssrqs.util.MD5Util;
import com.znv.fssrqs.util.TimingCounter;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

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
            FeatureCompUtil fc = new FeatureCompUtil();
            fc.setFeaturePoints(PhoenixService.getPoints());
            generalSearchParam.setSimilarityDegree(
                    fc.reversalNormalize(generalSearchParam.getSimilarityDegree().floatValue() * 0.01f));
        }
        JSONObject jsonObject = new JSONObject();
        switch (generalSearchParam.getQueryType()) {
            case 1:
                jsonObject = fastSearch.fastSearch(host,generalSearchParam);
                break;
            case 2:
                if (generalSearchParam.getFeatureValue() == null
                        || generalSearchParam.getFeatureValue().length <= 0) {
                    throw ZnvException.badRequest("NoImage");
                }
                if (generalSearchParam.getSimilarityDegree() == null) {
                    throw ZnvException.badRequest("RequestException", "SimilarityDegree");
                }

                if("-1".equals(generalSearchParam.getAgeLowerLimit())){
                    generalSearchParam.setAgeLowerLimit("0");
                }
                if("-1".equals(generalSearchParam.getAgeUpLimit())){
                    generalSearchParam.setAgeUpLimit(null);
                }
                if("-1".equals(generalSearchParam.getGlass())){
                    generalSearchParam.setGlass(null);
                }
                if("-1".equals(generalSearchParam.getRespirator())){
                    generalSearchParam.setRespirator(null);
                }
                if("-1".equals(generalSearchParam.getSkinColor())){
                    generalSearchParam.setSkinColor(null);
                }
                if("-1".equals(generalSearchParam.getMustache())){
                    generalSearchParam.setMustache(null);
                }
                if("-1".equals(generalSearchParam.getEmotion())){
                    generalSearchParam.setEmotion(null);
                }
                if("-1".equals(generalSearchParam.getEyeOpen())){
                    generalSearchParam.setEyeOpen(null);
                }
                if("-1".equals(generalSearchParam.getMouthOpen())){
                    generalSearchParam.setMouthOpen(null);
                }
                if("-1".equals(generalSearchParam.getGenderType())){
                    generalSearchParam.setGenderType(null);
                }
                generalSearchParam.setUUID(UUID.randomUUID().toString().replace("-", "").toLowerCase());
                generalSearchParam.setUUID(MD5Util.encode(generalSearchParam.toString()));
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
