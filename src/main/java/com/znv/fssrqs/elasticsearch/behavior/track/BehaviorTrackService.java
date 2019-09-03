package com.znv.fssrqs.elasticsearch.behavior.track;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.param.behavior.search.FastSearchParam;
import com.znv.fssrqs.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.znv.fssrqs.elasticsearch.lopq.LOPQModel.predictCoarseOrder;

/**
 * Created by dongzelong on  2019/8/22 11:54.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
@Slf4j
public class BehaviorTrackService {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    @Autowired
    private ElasticSearchClient elasticSearchClient;

    private void paramCheck(FastSearchParam fastSearchParam) {
        if (StringUtils.isBlank(fastSearchParam.getStartTime()) || StringUtils.isBlank(fastSearchParam.getEndTime())) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeOrEndTimeNotEmpty");
        }

        if (fastSearchParam.getStartTime().compareTo(fastSearchParam.getEndTime()) > 0) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeNotGtEndTime");
        }

        if (fastSearchParam.getFeatures() == null || fastSearchParam.getFeatures().isEmpty()) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "ImageFeatureNotEmpty");
        }

        if (fastSearchParam.getSimThreshold() / 100 < 0.5f) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "SimNotLtFifty");
        }

        if (fastSearchParam.getPageSize() < 1) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "QueryNumNotLtOne");
        }
    }

    private JSONObject getTemplateParams(String body, FeatureCompUtil fc) {
        FastSearchParam fastSearchParam = JSONObject.parseObject(body, FastSearchParam.class);
        paramCheck(fastSearchParam);
        JSONObject queryParams = new JSONObject();
        JSONObject searchParams = new JSONObject();
        queryParams.put("id", HdfsConfigManager.getString(CommonConstant.ElasticSearch.ES_SEARCH_TEMPLATE_FASTFEATURE_ID));
        queryParams.put("params", searchParams);
        searchParams.put("coarse_code_num", fastSearchParam.getCoarseCodeNum());
        searchParams.put("enter_time_start", fastSearchParam.getStartTime());
        searchParams.put("enter_time_end", fastSearchParam.getEndTime());
        if (fastSearchParam.getOfficeIDs() != null && fastSearchParam.getOfficeIDs().size() > 0) {
            searchParams.put("office_id", fastSearchParam.getOfficeIDs());
            searchParams.put("is_office", true);
        }


        if (fastSearchParam.getCameraIDs() != null && fastSearchParam.getCameraIDs().size() > 0) {
            searchParams.put("camera_id", fastSearchParam.getCameraIDs());
            searchParams.put("is_camera", true);
        }
        searchParams.put("sim_threshold", fc.reversalNormalize(fastSearchParam.getSimThreshold() / 100));
        searchParams.put("feature_name", "rt_feature.feature_high");
        if (fastSearchParam.getFeatures() != null && fastSearchParam.getFeatures().size() > 0) {
            List<String> list = new ArrayList<String>();
            for (String imgSrc : fastSearchParam.getFeatures()) {
                String feature = FaceAIUnitUtils.getImageFeature(imgSrc);
                JSONObject featureJson = JSON.parseObject(feature);
                if (!"success".equalsIgnoreCase(featureJson.getString("result"))) {
                    list.add("");
                } else {
                    list.add(featureJson.getString("feature"));
                }
            }
            searchParams.put("feature_value", list);
        }
        searchParams.put("filter_type", fastSearchParam.getFilterType());
        //searchParams.put("is_lopq", fastSearchParam.isIsLopq());
        searchParams.put("sortField", fastSearchParam.getSortField());
        searchParams.put("sortOrder", fastSearchParam.getSortOrder());
        int from = ParamUtils.getPageOffset(fastSearchParam.getCurrentPage(), fastSearchParam.getPageSize());
        if (from + fastSearchParam.getPageSize() >= 10000) {
            throw ZnvException.badRequest("EsDefaultSplitPageError");
        }
        searchParams.put("from", from);
        searchParams.put("size", fastSearchParam.getPageSize());
        //排除字段
        String excludes[] = {"rt_feature"};
        searchParams.put("is_excludes", true);
        searchParams.put("excludes", excludes);
        return queryParams;
    }


    private JSONObject startSearch(JSONObject queryParams, FeatureCompUtil fc) {
        int coarseCentersNum = queryParams.getJSONObject("params").getIntValue("coarse_code_num");
        int[][] coarseCodeOrder;
        String indexNamePrefix = EsBaseConfig.getInstance().getEsIndexHistoryPrefix();
        StringBuffer indexNameSb = new StringBuffer();
        JSONArray featureValue = queryParams.getJSONObject("params").getJSONArray("feature_value");
        for (int i = 0; i < featureValue.size(); i++) {
            try {
                coarseCodeOrder = predictCoarseOrder(fc.getFloatArray((byte[]) new org.apache.commons.codec.binary.Base64().decode(featureValue.get(i))), coarseCentersNum);
            } catch (Exception e) {
                throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, "GetCoarseCodeFailed");
            }

            if (i < featureValue.size() - 1) {
                for (int j = 0; j < coarseCodeOrder.length; j++) {
                    indexNameSb.append(indexNamePrefix + "-" + coarseCodeOrder[j][0] + ",");
                }
            } else {
                for (int j = 0; j < coarseCodeOrder.length - 1; j++) {
                    indexNameSb.append(indexNamePrefix + "-" + coarseCodeOrder[j][0] + ",");
                }
                indexNameSb.append(indexNamePrefix + "-" + coarseCodeOrder[coarseCodeOrder.length - 1][0]);
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append(indexNameSb).append('/').append(EsBaseConfig.getInstance().getEsIndexHistoryType()).append("/_search/template");
        Map<String, Result> map = new HashMap<>();
        try {
            CountDownLatch latch = new CountDownLatch(1);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Result<JSONObject, String> result = elasticSearchClient.postRequest(sb.toString(), queryParams);
                    map.put("result", result);
                    latch.countDown();
                }
            });
            boolean ret = latch.await(2L, TimeUnit.MINUTES);
            if (!ret) {
                throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, "EsSearchTimeOut", 2);
            }
        } catch (InterruptedException e) {
            log.error("thread is interrupted:", e);
        }

        Result<JSONObject, String> result = map.get("result");
        if (result.isErr()) {
            String value = result.error();
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, value);
        }
        if (result.value().getBoolean("timed_out")) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.INTERNAL_ERROR, "EsSearchTimeOut");
        }
        return result.value();
    }


    private JSONObject getResult(JSONObject result, String remoteIp, FeatureCompUtil fc) {
        JSONArray esHits = result.getJSONObject("hits").getJSONArray("hits");
        List outHits = new CopyOnWriteArrayList();
        esHits.parallelStream().forEach(object -> {
            final JSONObject hit = (JSONObject) object;
            JSONObject outHit = new JSONObject();
            float score = hit.getFloatValue("_score");
            outHit.put("Score", fc.Normalize(score));
            JSONObject source = hit.getJSONObject("_source");
            outHit.put("BigPictureUuid", source.getString("big_picture_uuid"));
            outHit.put("ImgUrl", source.getString("img_url"));
            outHit.put("CameraID", source.getString("camera_id"));
            outHit.put("CameraName", source.getString("camera_name"));
            outHit.put("OfficeID", source.getString("office_id"));
            outHit.put("OfficeName", source.getString("office_name"));
            outHit.put("EnterTime", source.getString("enter_time"));
            outHit.put("LeaveTime", source.getString("leave_time"));
            outHit.put("OpTime", source.getString("op_time"));
            outHit.put("LibID", source.getInteger("lib_id"));
            outHit.put("PersonID", source.getString("person_id"));
            outHit.put("IsAlarm", source.getString("is_alarm"));
            outHit.put("Similarity", source.getFloatValue("similarity"));
            if (source.containsKey("img_width")) {
                outHit.put("ImgWidth", source.getIntValue("img_width"));
            }

            if (source.containsKey("img_height")) {
                outHit.put("ImgHeight", source.getIntValue("img_height"));
            }
            if (source.containsKey("left_pos")) {
                outHit.put("LeftPos", source.getIntValue("left_pos"));
            }
            if (source.containsKey("top")) {
                outHit.put("Top", source.getIntValue("top"));
            }

            //社区1:N添加device_kind返回
            if (source.containsKey("device_kind")) {
                outHit.put("DeviceKind", source.getInteger("device_kind"));
            }
            //社区1:N添加event_id返回
            if (source.containsKey("door_event_id")) {
                outHit.put("DoorEventID", source.getString("door_event_id"));
            }
            outHit.put("UUID", source.getString("uuid"));
            outHit.put("CoarseID", source.getInteger("coarse_id"));
            String smallUuid = source.getString("img_url");
            if ("null".equals(smallUuid) || StringUtils.isEmpty(smallUuid)) {
                outHit.put("SmallPictureUrl", "");
            } else {
                outHit.put("SmallPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetSmallPic", smallUuid));
            }

            String bigPictureUuid = source.getString("big_picture_uuid");
            if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)) {
                outHit.put("BigPictureUrl", "");
            } else {
                outHit.put("BigPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
            }
            outHits.add(outHit);
        });

        final int total = result.getJSONObject("hits").getInteger("total");
        final int took = result.getInteger("took");
        return FastJsonUtils.JsonBuilder.ok().list(outHits).property("Total", total).property("Took", took).json();
    }

    /**
     * 行为轨迹检索
     *
     * @param host
     * @return
     */
    public JSONObject selectBehaviorTrack(String host, String body) {
        String remoteIp = host.split(":")[0];
        FeatureCompUtil fc = new FeatureCompUtil();
        fc.setFeaturePoints(HdfsConfigManager.getPoints());
        JSONObject queryParams = getTemplateParams(body, fc);
        JSONObject requestResult = this.startSearch(queryParams, fc);
        final JSONObject result = this.getResult(requestResult, remoteIp, fc);
        return result;
    }
}