package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.znv.fssrqs.entity.mysql.MapTrackSearch;
import com.znv.fssrqs.service.elasticsearch.trailsearch.EsTrailSearchService;
import com.znv.fssrqs.service.elasticsearch.trailsearch.SearchByTrailParam;
import com.znv.fssrqs.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongzelong on  2019/6/4 17:02.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 多索引检索
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MultiIndexController {
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String DEFAULT_SORT_FIELD = "enter_time";
    private final String DEFAULT_SORT_WAY = "asc";
    private final Integer DEFAULT_COARSE_CODE_NUM = 3;
    @Autowired
    private EsTrailSearchService esTrailSearchService;

    /**
     * 人员地图轨迹查询
     *
     * @param mapTrackSearch
     * @return
     */
    @PostMapping("/person/map/track")
    public String getPersonMapTrack(@RequestHeader("Host") String host, @RequestBody MapTrackSearch mapTrackSearch) {
        JSONObject ret = new JSONObject();
        if (mapTrackSearch.getStartTime() == null ||
                mapTrackSearch.getEndTime() == null ||
                mapTrackSearch.getFeatures() == null ||
                mapTrackSearch.getFeatures().isEmpty() ||
                mapTrackSearch.getSim() == null
        ) {
            ret.put("Code", 20000);
            ret.put("Message", "必填字段缺失");
            return ret.toJSONString();
        }

        JSONObject params = new JSONObject();
        String enterTimeStart = format.format(mapTrackSearch.getStartTime());
        String enterTimeEnd = format.format(mapTrackSearch.getEndTime());
        List<String> features = mapTrackSearch.getFeatures();
        List<String> list = Lists.newArrayList();
        for (String imgSrc : features) {
            JSONObject featureObject = JSON.parseObject(FaceAIUnitUtils.getImageFeature(imgSrc));
            if ("success".equalsIgnoreCase(featureObject.getString("result"))) {
                list.add(featureObject.getString("feature"));
            }
        }
        params.put("feature_value", list);
        params.put("enter_time_start", enterTimeStart);
        params.put("enter_time_end", enterTimeEnd);
        params.put("office_id", new ArrayList<>());
        params.put("camera_id", mapTrackSearch.getCameraIDs());
        float floatSimThreshold = new BigDecimal(mapTrackSearch.getSim()).floatValue() / 100;
        params.put("sim_threshold", floatSimThreshold);
        params.put("sort_field", DEFAULT_SORT_FIELD);
        params.put("sort_order", DEFAULT_SORT_WAY);
        params.put("coarse_code_num", DEFAULT_COARSE_CODE_NUM);
        SearchByTrailParam queryParams = JSON.parseObject(params.toJSONString(), new TypeReference<SearchByTrailParam>() {
        });
        Result<String, String> result = esTrailSearchService.checkParam(queryParams);
        if (result.isErr()) {
            return JSON.toJSONString(FastJsonUtils.JsonBuilder.badRequest(20000).message(result.error()));
        }
        Result<JSONObject, String> esResult = esTrailSearchService.select(queryParams);
        String remoteIp = host.split(":")[0];
        if (esResult.isErr()) {
            ret.put("Code", 50000);
            ret.put("Message", "获取ES数据失败:errorCode=" + esResult.error());
            return ret.toJSONString();
        }

        ret.put("Code", 10000);
        JSONObject retData = new JSONObject();
        JSONObject esObject = esResult.value();
        JSONArray hitsJsonArray = esObject.getJSONArray("Hits");
        retData.put("List", hitsJsonArray);
        ret.put("Data", retData);
        ret.put("Message", "ok");
        for (Object object : hitsJsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            String pictureUuid = jsonObject.getString("ImgUrl");
            if ("null".equals(pictureUuid) || StringUtils.isEmpty(pictureUuid)) {
                jsonObject.put("ImgUrl", "");
            } else {
                jsonObject.put("ImgUrl", ImageUtils.getImgUrl(remoteIp, "GetSmallPic", pictureUuid));
            }
            String bigPictureUuid = jsonObject.getString("BigPictureUuid");
            if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)) {
                jsonObject.put("BigPictureUrl", "");
            } else {
                jsonObject.put("BigPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
            }
        }
        return ret.toJSONString();
    }
}
