package com.znv.fssrqs.service.elasticsearch.trailsearch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.FeatureCompUtil;
import com.znv.fssrqs.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.znv.fssrqs.elasticsearch.lopq.LOPQModel.predictCoarseOrder;
import static com.znv.fssrqs.util.Result.err;

/**
 * Created by dongzelong on  2019/6/25 11:13.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 轨迹查询
 */
@Service
@Slf4j
public class EsTrailSearchService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    private final int DEFAULT_COARSE_CENTERS_NUM = 1;
    private static final int DEFAULT_SIZE = 10000;
    private static final int DEFAULT_FROM = 0;

    public Result<JSONObject, String> select(SearchByTrailParam queryParams) {
        FeatureCompUtil featureCompUtil = new FeatureCompUtil();
        JSONObject templateObject = getTemplateObject(queryParams);
        int coarseCentersNum = queryParams.getCoarseCodeNum() != 0 ? queryParams.getCoarseCodeNum() : DEFAULT_COARSE_CENTERS_NUM;
        //计算粗分类的标签coarse_id
        int[][] coarseCodeOrder;
        String fssEsIndexHistoryPrefix = EsBaseConfig.getInstance().getEsIndexHistoryPrefix();
        List<String> featureValue = queryParams.getFeatureValue();
        StringBuffer indexName = new StringBuffer();
        for (int i = 0; i < featureValue.size(); i++) {
            try {
                coarseCodeOrder = predictCoarseOrder(featureCompUtil.getFloatArray(new org.apache.commons.codec.binary.Base64().decode(featureValue.get(i))), coarseCentersNum);
            } catch (Exception e) {
                throw new RuntimeException("获取粗分类异常:", e);
            }
            if (i < featureValue.size() - 1) {
                for (int j = 0; j < coarseCodeOrder.length; j++) {
                    indexName.append(fssEsIndexHistoryPrefix + "-" + coarseCodeOrder[j][0] + ",");
                }
            } else {
                for (int j = 0; j < coarseCodeOrder.length - 1; j++) {
                    indexName.append(fssEsIndexHistoryPrefix + "-" + coarseCodeOrder[j][0] + ",");
                }
                indexName.append(fssEsIndexHistoryPrefix + "-" + coarseCodeOrder[coarseCodeOrder.length - 1][0]);
            }
        }

        //多索引查询
        String multiIndexTemplateUrl = indexName + "/" + EsBaseConfig.getInstance().getEsIndexHistoryType() + "/_search/template";
        Result<JSONObject, String> resultObject = elasticSearchClient.postRequest(multiIndexTemplateUrl, templateObject);
        if (resultObject.isErr()) {
            return resultObject;
        }
        JSONObject jsonEsResult = resultObject.value();
        if (jsonEsResult.getBoolean("timed_out")) {
            log.error("查询es超时");
            return Result.err("查询es超时");
        }
        JSONArray esHits = jsonEsResult.getJSONObject("hits").getJSONArray("hits");
        List<SearchByTrailHit> outHits = new ArrayList<>();
        featureCompUtil.setFeaturePoints(HdfsConfigManager.getPoints());
        for (int i = 0; i < esHits.size(); i++) {
            SearchByTrailHit outHit = new SearchByTrailHit();
            JSONObject hit = esHits.getJSONObject(i);
            float score = hit.getFloatValue("_score");
            JSONObject source = hit.getJSONObject("_source");
            //若某人从摄像头A->摄像头B，然后出去了，又回到摄像头B，则只返回摄像头A和B，摄像头A下1次，摄像头B下面2次；
            //若某人从摄像头A->摄像头B->摄像头A，则返回摄像头A,B,A，分别各一次
            if (i >= 1 && (source.getString("camera_id").equals(outHits.get(outHits.size() - 1).getCameraId()))) {
                outHit = outHits.get(outHits.size() - 1);
                //当前摄像机逗留次数+1
                outHit.setStayNum(outHit.getStayNum() + 1);
                outHit.setEnterTime(DataConvertUtils.parseDateStr2DateStr(source.getString("enter_time")));
                outHit.setLeaveTime(DataConvertUtils.parseDateStr2DateStr(source.getString("leave_time")));
                String gpsXy = source.getString("gps_xy");
                if (Objects.nonNull(gpsXy)) {
                    String[] gps = gpsXy.split(",");
                    outHit.setGpsy(gps[0]);
                    outHit.setGpsx(gps[1]);
                }
                outHit.setImgUrl(source.getString("img_url"));
                outHit.setBigPictureUuid(source.getString("big_picture_uuid"));
                outHit.setUuid(source.getString("uuid"));
                outHit.setCoarseId(source.getInteger("coarse_id"));
                outHit.setLibId(source.getInteger("lib_id"));
                outHit.setPersonId(source.getString("person_id"));
                String scoreStr = String.valueOf(score);
                //获取最后一位数字为图片编号
                outHit.setImageNo((int) ((Double.parseDouble("0." + scoreStr.substring(scoreStr.length() - 1)) * 10)));
                outHit.setScore(featureCompUtil.Normalize(score));
            } else {
                String scoreStr = String.valueOf(score);
                outHit.setImageNo((int) ((Double.parseDouble("0." + scoreStr.substring(scoreStr.length() - 1)) * 10)));
                outHit.setScore(featureCompUtil.Normalize(score));
                outHit.setCameraId(source.getString("camera_id"));
                String gpsXy = source.getString("gps_xy");
                if (gpsXy != null) {
                    String[] gps = gpsXy.split(",");
                    outHit.setGpsy(gps[0]);
                    outHit.setGpsx(gps[1]);
                }
                outHit.setEnterTime(DataConvertUtils.parseDateStr2DateStr(source.getString("enter_time")));
                outHit.setLeaveTime(DataConvertUtils.parseDateStr2DateStr(source.getString("leave_time")));
                outHit.setPersonId(source.getString("person_id"));
                outHit.setLibId(source.getInteger("lib_id"));
                outHit.setImgUrl(source.getString("img_url"));
                outHit.setBigPictureUuid(source.getString("big_picture_uuid"));
                outHit.setUuid(source.getString("uuid"));
                outHit.setCoarseId(source.getInteger("coarse_id"));
                //逗留次数
                outHit.setStayNum(1);
                outHits.add(outHit);
            }
        }
        int total = jsonEsResult.getJSONObject("hits").getInteger("total");
        int took = jsonEsResult.getInteger("took");
        JSONObject resObject = new JSONObject();
        resObject.put("Total", total);
        resObject.put("Took", took);
        resObject.put("Hits", outHits);
        return Result.ok(resObject);
    }

    /**
     * @param inParam
     */
    public Result<String, String> checkParam(SearchByTrailParam inParam) {
        if (StringUtils.isEmpty(inParam.getEnterTimeStart())) {
            return err("开始时间不能为空");
        }
        if (StringUtils.isEmpty(inParam.getEnterTimeEnd())) {
            return err("结束时间不能为空");
        }
        if (inParam.getEnterTimeStart().compareTo(inParam.getEnterTimeEnd()) > 0) {
            return err("开始时间不能大于结束时间");
        }

        if (inParam.isLopq() && (inParam.getFeatureValue() == null || inParam.getFeatureValue().isEmpty())) {
            return err("特征值为不能为空");
        }
        return Result.ok("ok");
    }

    private JSONObject getTemplateObject(SearchByTrailParam searchByTrailParam) {
        JSONObject jsonObject = new JSONObject();
        //排除字段
        String excludes[] = {"rt_feature"};
        jsonObject.put("enter_time_start", searchByTrailParam.getEnterTimeStart());
        jsonObject.put("enter_time_end", searchByTrailParam.getEnterTimeEnd());
        //特征名称
        jsonObject.put("feature_name", "rt_feature.feature_high");
        FeatureCompUtil fc = new FeatureCompUtil();
        fc.setFeaturePoints(HdfsConfigManager.getPoints());
        float sim = (float) searchByTrailParam.getSimThreshold();
        //反归一化
        jsonObject.put("sim_threshold", fc.reversalNormalize(sim));//脚本中未归一化
        jsonObject.put("feature_value", searchByTrailParam.getFeatureValue());
        jsonObject.put("filter_type", searchByTrailParam.getFilterType());
        jsonObject.put("from", DEFAULT_FROM);
        jsonObject.put("size", DEFAULT_SIZE);
        //排序字段
        jsonObject.put("sortField", searchByTrailParam.getSortField());
        jsonObject.put("sortOrder", searchByTrailParam.getSortOrder());
        jsonObject.put("is_excludes", true);
        jsonObject.put("excludes", excludes);
        if (searchByTrailParam.getOfficeId() != null && !searchByTrailParam.getOfficeId().isEmpty()) {
            jsonObject.put("office_id", searchByTrailParam.getOfficeId());
            jsonObject.put("is_office", true);
        }
        //摄像机ID列表
        if (searchByTrailParam.getCameraId() != null && !searchByTrailParam.getCameraId().isEmpty()) {
            jsonObject.put("camera_id", searchByTrailParam.getCameraId());
            jsonObject.put("is_camera", true);
        }

        String templateName = EsBaseConfig.getInstance().getFastTemplateName();
        JSONObject templateObject = new JSONObject();
        templateObject.put("id", templateName);
        templateObject.put("params", jsonObject);
        return templateObject;
    }
}
