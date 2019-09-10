package com.znv.fssrqs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.OkHttpUtil;
import com.znv.fssrqs.util.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dongzelong on  2019/9/10 9:50.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 人体多维检索
 */
@Service
public class ReidMultiRetrievalService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;

    public JSONObject getSearch(JSONObject params) {
        checkParams(params);
        final JSONObject templateParams = getTemplateParams(params);
        final JSONObject response = sendRequest(templateParams);
        final JSONObject result = getResult(response);
        return result;
    }

    private void checkParams(JSONObject params) {
        if (!params.containsKey("Features") || !(params.getJSONArray("Features") instanceof JSONArray)) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "PictureFieldIsEmpty");
        }

        if (!params.containsKey("StartTime") || !params.containsKey("EndTime")) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeOrEndTimeEmpty");
        }

        if (params.getString("StartTime").compareTo(params.getString("EndTime")) > 0) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeGtEndTime");
        }

        if (!params.containsKey("CameraIDs") || !(params.getJSONArray("CameraIDs") instanceof JSONArray)) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "DeviceFieldEmpty");
        }

        JSONArray features = params.getJSONArray("Features");
        if (!features.isEmpty() && features.size() >= 2) {
            final String filterType = params.getString("FilterType");
            if (StringUtils.isEmpty(filterType)) {
                throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "FilterTypeEmpty");
            }

            if (!(filterType.equals("and") && filterType.equals("or"))) {
                throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "AndOrValueError");
            }
        }
    }

    private JSONObject getTemplateParams(JSONObject requestParams) {
        JSONObject templateParams = new JSONObject();
        JSONObject params = new JSONObject();
        templateParams.put("id", "template_reid_feature_search");
        final JSONArray features = requestParams.getJSONArray("Features");
        if (features.size() > 0) {
            String url = String.format(CommonConstant.ThousandSights.GET_FEATURE_URL, "10.45.154.54", 20280);
            final List<String> featureValues = features.parallelStream().map(object -> {
                //获取千视通人体特征值
                String feature = (String) object;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("objtype", 1);
                jsonObject.put("picture", feature);
                final String res = OkHttpUtil.doPost(url, jsonObject.toJSONString());
                JSONObject result = JSON.parseObject(res);
                if (!result.getString("ret").equals("0")) {
                    throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.getString("desc"));
                }
                //return result.getString("feature");
                return "SkxUQgkAAACGNAAAAQAAAEKvcF0AAAAAAAAAAAAAAAADJYAUbQEAAAAGAAAAAAAAAQIAAAAABBIVCP/+/fv+/wD///z9/v7//QADAwH//v39/v39+/v8/f3+/fv+AAAA/wAA//8A/vz8/gYI/gAAAQAA/f0AAAD9AP7+//3+/fr9/v79AQD/AAIE/Pr7/f38AAD9/v8A+/r9//79/f79/v7++/n6/fz6//39/wAE/f3+//7/AQD9/v38BRECAQD//wD////+//37/v7/DgkBAP/9/wD//////fv9/v8B+/r9/wAA/v8AAQD///3+/wMK+/r8/fz9AAgXDAH+/v//AAACBwUAAAAAAAD////+/vz+AAAA/vz8/v3+/Pv9///+/v0AAgQGAAD9/v38AAACAwMC/Pv/AP78/P3+/vv7/fwAAAD+/v3+//7++/r6/f39/f3///8AAAcJBgQBAP79/v7+BAUHBwsI/v79//4A/Pv9/vz6//3/AP//BgP+/wAB//8AAAYI/v7+//7+/fv6/Pr8AP38/wEC/v38/fz++vr7/fz9/f4A//z9AP////8CAwD/AAIB///7/f78/fv8/v8A/Pz9//7+/Pr8/wD//vz9/v3+/gAFBAYE/v38/v3+AQD9/v8A/f3/AAD/AP39//39AAEAAP///P38/wAB/PwAAP79/fz9/v7+AgMBAAD+/fz8/v38AP/8/f39///+/wD/AQD/AAIFAwEAAAAB/Pz/AP///wUBAP39/v3+AAIB+/r8/v7///7/AP7+/f3+///9//z8/v39/f7/AAAA/v////38/fz8///+/P4AAAD/AQD/AAoJBgEAAAD9AQMCAQAA/vv8/fz9/f4AAAAABwcAAP38BgL+/v3+/gD9/v3+/Pr/AAEC/f4BAf78AwsGAP7/AQD/AAAAAAEICxAL/vz8/f39///+//7++vf5/fv7+vj7/v79/vz+/v39//8AAP7+/v7+//7/+/7+//7+/wUPCAH9+/r7/v//BQL+/gAA+/wAAAH+/v4DBQMBAgMAAAD/BwIAAP79/v3+//7+/P3/AP8A/gIDAP///v7+AP79/f4AAAD8/fz9//4A/v3/AP8A/f4AAAD//v7/AAAA/vz/AP7/AAEAAP79/fz8/v7//fz+///+DQT+/wEK/f8AAP///gYLBgQC//7//wAB/v3+//8ABgn9/v7//fz+/v39/f39/v3///3+/v79/v4AAP/+/fv8/v79/Pz9//7+AP/9/v7//v79/v38/f3/AAAA/fwAAAAF/Pz7/fz9//8BAgkKAP4AAAAA/v39/v7+AP7+AAAC/v7+////AAD+//7/Af8AAP8A/wEBAP//BwD+/wEF/v3+/wD+BP//AAD+AQD9/v7//wABAgD///4AAAD/AAD+/wIDBQH9/vz+/vz9/v39/v7+////AAQBAP/+/v0AAP///Pr9AAcOBQL//wIC////AAD++/v7/Pr7BhMHAf///wUJBAD/Af7+/wAAAQIFA/37AwD+///+/f8AAQYJBQL9/v4A/f///vz8/Pz9/v7//wUJBQAA/f79/v38/f3+AP//AP3/AAAB+/j8/v39/v39///+/f4AAAAAAAD+/vz5/fz9/wAAAQH+AAD/AP39/vz7/vz8/v7//f0AAQIA/vv9//7+/f0AAP8A+/r7/Pz8/f0AAP/+/Pv9//7+/Pz9/v39//3+/wAC+/wEBQUFAf3+AP37/wAAAQgJAAD/AAAA/v3+///9AgP7/AAD+vr//wEA//4AAP79+vv/AAIDAwD9/v7+AP37/fz7/wD8/v/+/vz9/v8A/Pj6/Pr6/wAAAPz8AQEEAgD/+/n8/f39/v39/v7+/Pz////9/v7//wD//fz+/v3++/n7/f38/wEGBAQD/vz///3/AgYAAAAA/vz/AP7+AAAAAAD+/fz9/v4A/v8CAQD///z+AAcIAAD7/f39/vz9/v7//Pv8/v//+/v/AP78AP38/v79/gAA//7+AAECAAMBAQb///7/+/r9/wED/v3/AAAA/v7//////v3/AP/+AP//AP7///z8/wIE/wD//wAA/gAAAAD/AAAAAAIB+vn9/v7++vn7/Pv8";
            }).collect(Collectors.toList());
            params.put("is_calcSim", true);
            params.put("feature_name", "Feature");
            params.put("filter_type", "or");
            //人体特征获取
            params.put("objType", 1);
            params.put("feature_value", featureValues);
        }

        params.put("is_excludes", true);
        params.put("excludes", Arrays.asList("feature"));
        //不需要归一化
        params.put("sim_threshold", requestParams.getFloatValue("Similarity") / 100);
        if (requestParams.getJSONArray("CameraIDs").size() > 0) {
            //设备列表
            params.put("is_camera", true);
            params.put("DeviceID", requestParams.getJSONArray("CameraIDs"));
        }

        //时间
        params.put("enter_time_start", requestParams.getString("StartTime"));
        params.put("enter_time_end", requestParams.getString("EndTime"));

        //上衣颜色
        if (!requestParams.getString("CoatColor").equals("-1")) {
            params.put("is_coat_color", true);
            params.put("CoatColor", requestParams.getString("CoatColor"));
        }

        //裤子颜色
        if (!requestParams.getString("TrousersColor").equals("-1")) {
            params.put("is_trousers_color", true);
            params.put("TrousersColor", requestParams.getString("TrousersColor"));
        }

        //是否戴眼镜
        if (!requestParams.getString("HasGlasses").equals("-1")) {
            params.put("is_glasses", true);
            params.put("HasGlasses", requestParams.getString("HasGlasses"));
        }

        //是否戴帽子
        if (!requestParams.getString("HasHat").equals("-1")) {
            params.put("is_hat", true);
            params.put("HasHat", requestParams.getString("HasHat"));
        }

        //是否戴口罩
        if (!requestParams.getString("HasMask").equals("-1")) {
            params.put("is_mask", true);
            params.put("HasMask", requestParams.getString("HasMask"));
        }

        //是否背包
        if (!requestParams.getString("HasBackpack").equals("-1")) {
            params.put("is_backpack", true);
            params.put("HasBackpack", requestParams.getString("HasBackpack"));
        }

        //是否打伞
        if (!requestParams.getString("HasUmbrella").equals("-1")) {
            params.put("is_umbrella", true);
            params.put("HasUmbrella", requestParams.getString("HasUmbrella"));
        }

        if (requestParams.containsKey("SortField") && !requestParams.getString("SortField").equals("-1")) {
            params.put("sortField", requestParams.getString("SortField"));
            params.put("sortOrder", requestParams.getString("sortOrder"));
        }

        params.put("from", 0);
        params.put("size", 9999);
        templateParams.put("params", params);

        System.out.println(templateParams.toJSONString());
        return templateParams;
    }

    private JSONObject sendRequest(JSONObject templateParams) {
        String url = new StringBuffer()
                .append("reid_fss_data_n_project_v1_2")
                .append("/")
                .append("reid_data")
                .append("/_search/template").toString();
        final Result<JSONObject, String> result = elasticSearchClient.postRequest(url, templateParams);
        if (result.isErr()) {
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
        }
        return result.value();
    }

    private JSONObject getResult(JSONObject response) {
        long total = response.getLongValue("total");
        long took = response.getLongValue("took");
        final JSONArray jsonArray = response.getJSONObject("hits").getJSONArray("hits");
        final List<JSONObject> list = jsonArray.parallelStream().map(object -> {
            JSONObject jsonObject = (JSONObject) object;
            final JSONObject source = jsonObject.getJSONObject("_source");
            return source;
        }).collect(Collectors.toList());
        return FastJsonUtils.JsonBuilder.ok().list(list).property("Total", total).property("Took", took).json();
    }
}
