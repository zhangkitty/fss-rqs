package com.znv.fssrqs.service.echarts;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.Echarts;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.param.echarts.PersonLibIdParam;
import com.znv.fssrqs.param.echarts.PersonListGroupQueryParam;
import com.znv.fssrqs.util.FormatObject;
import com.znv.fssrqs.util.Result;
import com.znv.fssrqs.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Data() 下午1:59
 */

@Service
@Slf4j
public class PersonLibInfoService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    private final String esurl = "person_list_data_n_project*/person_list/_search/template";
    private final String templateName = "template_person_list_group";

    public JSONObject requestSearch(PersonListGroupQueryParam queryParams, PersonLibIdParam libID) {
        List<String> list = new ArrayList<>();
        queryParams.setAddr(list);
        queryParams.setLibId(libID.getLibID());
        List<String> add = new ArrayList<>();
        if(SpringContextUtil.getCtx().getBean(Echarts.class).getAdd().split(",").length>0){
            for (String str:SpringContextUtil.getCtx().getBean(Echarts.class).getAdd().split(",")){
                add.add(str);
            }
        }
        queryParams.setAddr(add);
        long startTime = System.currentTimeMillis();
        JSONObject jsonEsResult = new JSONObject();
        try {
            // 执行第一次ES分组查询
            JSONObject obj = getAggsResult(queryParams, jsonEsResult);
            // 执行第二次ES分组查询
            obj = getAgeGroupResult(queryParams, jsonEsResult);
            // 执行第三次ES分组查询
            obj = getAddrGroupResult(queryParams, jsonEsResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        jsonEsResult.put("Time", String.valueOf(endTime - startTime));
        return jsonEsResult;
    }

    /**
     * 第三次分组聚合查询
     *
     * @param queryParams 查询参数
     * @param resObj      前两次查询结果
     * @return
     */
    private JSONObject getAddrGroupResult(PersonListGroupQueryParam queryParams, JSONObject resObj) {
        JSONArray bucketsArray = new JSONArray();
        int total = 0;
        if (queryParams.getAddr() != null && !queryParams.getAddr().isEmpty()) {
            for (String key : queryParams.getAddr()) {
                if (key == null || (key != null && key.trim().equals(""))) {
                    continue;
                }
                JSONObject obj = getTemplateParams(queryParams);
                JSONObject params = obj.getJSONObject("params");
                params.put("addr", key);
                // 查询结果
                Result<JSONObject, String> sb = elasticSearchClient.postRequest(esurl, obj);
                JSONObject jsonEsResult = sb.value();
                JSONObject buckets = new JSONObject();
                buckets.put("key", key);
                int count = jsonEsResult.getJSONObject("hits").getInteger("total");
                total += count;
                buckets.put("doc_count", count);
                bucketsArray.add(buckets);
            }
        }

        JSONObject buckets = new JSONObject();
        buckets.put("key", "其它");
        buckets.put("doc_count", 0);
        if (resObj.get("total") != null && (Integer) resObj.get("total") >= total) {
            buckets.put("doc_count", ((Integer) resObj.get("total") - total));
        } else {
            log.warn("addrGroup count exceeds totalCount: " + resObj.get("total") + ", addrGroup count: " + total);
        }
        bucketsArray.add(buckets);

        bucketsArray.sort(Comparator.comparing(obj -> ((JSONObject) obj).getInteger("doc_count")).reversed());

        JSONObject addrGroup = new JSONObject();
        addrGroup.put("buckets", bucketsArray);
        resObj.put("AddrGroup", addrGroup);
        return resObj;
    }

    /**
     * 第二次分组聚合查询
     *
     * @param queryParams 查询参数
     * @param resObj      getAggsResult查询结果
     * @return
     */
    private JSONObject getAgeGroupResult(PersonListGroupQueryParam queryParams, JSONObject resObj) {
        if (queryParams.getAgeGroup()) {
            JSONArray bucketsArray = new JSONArray();
            for (int i = 0; i < 5; i++) {
                JSONObject obj = getTemplateParams(queryParams);
                JSONObject params = obj.getJSONObject("params");
                params.put("age_group", true);
                Date nowDate = new Date();
                String key = "";
                if (i == 0) {
                    params.put("teenage_end", FormatObject.formatAgeDate(nowDate, 0));
                    params.put("teenage_start", FormatObject.formatAgeDate(nowDate, 15));
                    key = "teenage";
                } else if (i == 1) {
                    params.put("youth_end", FormatObject.formatAgeDate(nowDate, 15));
                    params.put("youth_start", FormatObject.formatAgeDate(nowDate, 36));
                    key = "youth";
                } else if (i == 2) {
                    params.put("midlife_end", FormatObject.formatAgeDate(nowDate, 36));
                    params.put("midlife_start", FormatObject.formatAgeDate(nowDate, 65));
                    key = "midlife";
                } else if (i == 3) {
                    params.put("old_end", FormatObject.formatAgeDate(nowDate, 65));
                    params.put("old_start", FormatObject.formatAgeDate(nowDate, 150));
                    key = "old";
                }else {
                    params.put("old_end", FormatObject.formatAgeDate(nowDate, 150));
                }
                // 查询结果
                Result<JSONObject, String> sb = elasticSearchClient.postRequest(esurl, obj);
                JSONObject jsonEsResult = sb.value();
                JSONObject buckets = new JSONObject();
                buckets.put("key", key);
                buckets.put("doc_count", jsonEsResult.getJSONObject("hits").getInteger("total"));
                bucketsArray.add(buckets);
            }

            JSONObject ageGroup = new JSONObject();
            ageGroup.put("buckets", bucketsArray);
            resObj.put("AgeGroup", ageGroup);
        }

        return resObj;
    }

    /**
     * 第一次分组聚合查询
     *
     * @param queryParams 查询参数
     * @param resObj      空json类
     * @return
     */
    private JSONObject getAggsResult(PersonListGroupQueryParam queryParams, JSONObject resObj) {
        JSONObject obj = getTemplateParams(queryParams);
        JSONObject params = obj.getJSONObject("params");
        if (queryParams.getSexGroup() || queryParams.getFlagGroup() || queryParams.getTimeGroup()) {
            params.put("is_aggs", true);
            params.put("sex_group", queryParams.getSexGroup());
            params.put("flag_group", queryParams.getFlagGroup());
            params.put("time_group", queryParams.getTimeGroup());
        }
        // 查询结果
        Result<JSONObject, String> sb = elasticSearchClient.postRequest(esurl, obj);
        JSONObject jsonEsResult = sb.value();
        // 从es查询结果中获取hits
        resObj.put("Total", jsonEsResult.getJSONObject("hits").getInteger("total"));
        if (jsonEsResult.getJSONObject("aggregations") != null) {
            JSONObject aggResult = jsonEsResult.getJSONObject("aggregations");
            if (aggResult.getJSONObject("time_group") != null) {
                resObj.put("TimeGroup", aggResult.getJSONObject("time_group"));
            }
            if (aggResult.getJSONObject("sex_group") != null) {
                resObj.put("SexGroup", aggResult.getJSONObject("sex_group"));
            }
            if (aggResult.getJSONObject("flag_group") != null) {
                resObj.put("FlagGroup", aggResult.getJSONObject("flag_group"));
            }
        }
        return resObj;
    }

    /**
     * 模板查询参数封装
     *
     * @param inParam
     * @return
     */
    private JSONObject getTemplateParams(PersonListGroupQueryParam inParam) {
        JSONObject params = new JSONObject();
        if (inParam.getPersonLibType() != null && !inParam.getPersonLibType().isEmpty()) {
            params.put("is_personlib", true);
            params.put("personlib_type", inParam.getPersonLibType());
        }
        if (inParam.getLibId() != null && !inParam.getLibId().isEmpty()) {
            params.put("is_lib", true);
            params.put("lib_id", inParam.getLibId());
        }
        params.put("is_del", inParam.getIsDel());
        params.put("from", 0);
        params.put("size", 0);
        JSONObject obj = new JSONObject();
        obj.put("id", templateName);
        obj.put("params", params);
        return obj;
    }
}
