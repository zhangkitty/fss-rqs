package com.znv.fssrqs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.timer.SystemDeviceLoadTask;
import com.znv.fssrqs.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by dongzelong on  2019/10/11 11:27.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
@Slf4j
public class TimeSpaceCollisionService {
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private void checkParams(List<JSONObject> requestParams) {
        requestParams.parallelStream().forEach(jsonObject -> {
            if (StringUtils.isEmpty(jsonObject.getString("StartTime")) || StringUtils.isEmpty(jsonObject.getString("EndTime"))) {
                throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeOrEndTimeNotEmpty");
            }

            if (!jsonObject.containsKey("CameraIDs") || !(jsonObject.getJSONArray("CameraIDs") instanceof JSONArray)) {
                throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "CameraIDsTypeError");
            }

            if (jsonObject.getString("StartTime").compareTo(jsonObject.getString("EndTime")) > 0) {
                throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "StartTimeGtEndTime");
            }
        });
    }

    private static JSONObject getTemplateParams(JSONObject requestParams, int offset, Map<String, Object> map) {
        JSONObject templateParams = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("enter_time_start", requestParams.getString("StartTime"));
        params.put("enter_time_end", requestParams.getString("EndTime"));
        params.put("from", offset);
        params.put("size", Integer.parseInt((String) map.getOrDefault("PageSize", 0)));
        final JSONArray cameraIDs = requestParams.getJSONArray("CameraIDs");
        if (cameraIDs.size() > 0) {
            params.put("DeviceID", true);
            params.put("camera_id", cameraIDs);
        } else {
            params.put("DeviceID", false);
        }

        params.put("fused_id_aggregation", true);
        templateParams.put("id", "template_spacetime_search");
        templateParams.put("params", params);
        return templateParams;
    }

    public JSONObject getTimeSpaceCollision(List<JSONObject> list, Map<String, Object> params) {
        this.checkParams(list);
        String url = new StringBuffer().append(HdfsConfigManager.getString(CommonConstant.ElasticSearch.INDEX_TYPE_PERSON_CLUSTER_HISTORY)).append("/_search/template").toString();
        HttpRequestCountDownLatch httpRequestCountDownLatch = new HttpRequestCountDownLatch(list.size(), list, executorService);
        return httpRequestCountDownLatch.getData(url, params);
    }

    private static JSONObject getResult(JSONObject response) {
        final int took = response.getInteger("took").intValue();
        final Integer total = response.getJSONObject("hits").getInteger("total");
        final JSONArray aggBuckets = response.getJSONObject("aggregations").getJSONObject("group_by_fused").getJSONArray("buckets");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Total", total);
        jsonObject.put("Took", took);
        jsonObject.put("Buckets", aggBuckets);
        return jsonObject;
    }

    private static JSONObject processHit(JSONObject jsonObject) {
        JSONObject source = jsonObject.getJSONObject("_source");
        String smallUuid = (String) source.remove("img_url");
        String imgUrl = ImageUtils.getImgUrl(SystemDeviceLoadTask.getMBus().getIP(), "GetSmallPic", smallUuid);
        source.put("SmallPictureUrl", imgUrl);
        source.put("LeaveTime", source.remove("leave_time"));
        source.put("ImgWidth", source.remove("img_width",0));
        source.put("ImgHeight", source.remove("img_height",0));
        source.put("CameraID", source.remove("camera_id"));
        source.put("CameraType", source.remove("camera_type"));
        source.put("EnterTime", source.remove("enter_time"));
        source.put("PersonID", source.remove("person_id"));
        source.put("CameraName", source.remove("camera_name"));
        source.put("FusedID", source.remove("fused_id"));
        source.put("OfficeID", source.remove("office_id"));
        source.put("OfficeName", source.remove("office_name"));
        source.put("UUID", source.remove("uuid"));
        source.put("LeftPos", source.remove("left_pos",0));
        String bigPictureUuid = (String) source.remove("big_picture_uuid");
        if (!("null".equals(bigPictureUuid) || org.springframework.util.StringUtils.isEmpty(bigPictureUuid))) {
            source.put("BigPictureUrl", ImageUtils.getImgUrl(SystemDeviceLoadTask.getMBus().getIP(), "GetBigBgPic", bigPictureUuid));
        } else {
            source.put("BigPictureUrl", "");
        }
        source.put("LibID", source.remove("lib_id"));
        source.put("Similarity", source.remove("similarity"));
        return source;
    }

    private final static class HttpRequestCountDownLatch {
        private final int DEFAULT_BEGIN_COUNT = 1;
        private int RUNNER_COUNT;

        private List<JSONObject> list;
        private ExecutorService executor;

        public HttpRequestCountDownLatch(int RUNNER_COUNT, List<JSONObject> list, ExecutorService executor) {
            this.RUNNER_COUNT = RUNNER_COUNT;
            this.list = list;
            this.executor = executor;
        }

        public JSONObject getData(String url, Map<String, Object> params) {
            int offset = ParamUtils.getPageOffset(Integer.parseInt((String) params.getOrDefault("CurrentPage", 0)), Integer.parseInt((String) params.getOrDefault("PageSize", 0))).intValue();
            final CountDownLatch begin = new CountDownLatch(DEFAULT_BEGIN_COUNT);
            final CountDownLatch end = new CountDownLatch(RUNNER_COUNT);
            CopyOnWriteArrayList<JSONObject> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
            AtomicInteger count = new AtomicInteger(0);
            AtomicLong tooks = new AtomicLong(0);
            list.parallelStream().forEach(jsonObject -> {
                executor.submit(() -> {
                    try {
                        JSONObject templateParams = getTemplateParams(jsonObject, offset, params);
                        Result<JSONObject, String> result = null;
                        try {
                            result = SpringContextUtil.getCtx().getBean(ElasticSearchClient.class).postRequest(url, templateParams);
                            if (result.isErr()) {
                                throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, result.error());
                            }
                            final JSONObject res = getResult(result.value());
                            final JSONArray buckets = res.getJSONArray("Buckets");
                            final int took = res.getInteger("Took").intValue();
                            increment(tooks, took);
                            copyOnWriteArrayList.addAll(buckets.toJavaList(JSONObject.class));
                        } catch (Exception e) {
                            log.error("request operator engine service occur exception:", e);
                            throw ZnvException.badRequest(CommonConstant.StatusCode.INTERNAL_ERROR, "ClientException");
                        }
                        increment(count);
                    } catch (Exception e) {
                        log.error("acquire es data occur exception:", e);
                    } finally {
                        end.countDown();
                    }
                });
            });

            begin.countDown();
            try {
                final boolean isTimeOut = end.await(2L, TimeUnit.MINUTES);
                if (!isTimeOut) {
                    throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, "EsSearchTimeOut", 2);
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
            }

            if (count.get() != RUNNER_COUNT) {
                throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, "EsAccessFailed");
            }

            final int size = list.size();
            List<JSONObject> buckets = Lists.newArrayList();
            //按照融合ID进行分组
            final Map<String, List<JSONObject>> fusedIDGroupMap = copyOnWriteArrayList.parallelStream().collect(Collectors.groupingBy(object -> ((JSONObject) object).getString("key"), Collectors.toList()));
            fusedIDGroupMap.forEach((String key, List<JSONObject> list) -> {
                JSONObject jsonObject = new JSONObject();
                if (list.size() == size) {
                    if (list.size() > 1) {
                        JSONObject resultObject = list.parallelStream().reduce((object1, object2) -> {
                            final int doc_count1 = object1.getInteger("doc_count").intValue();
                            final int doc_count2 = object2.getInteger("doc_count").intValue();
                            jsonObject.put("doc_count", doc_count1 + doc_count2);
                            jsonObject.put("key", object1.getString("key"));
                            jsonObject.put("camera_person_hits", object1.getJSONObject("camera_person_hits"));
                            return jsonObject;
                        }).get();
                        resultObject.put("Key", resultObject.remove("key"));
                        resultObject.put("DocCount", resultObject.remove("doc_count"));
                        final JSONObject object = resultObject.getJSONObject("camera_person_hits").getJSONObject("hits").getJSONArray("hits").getJSONObject(0);
                        processHit(object);
                        resultObject.remove("camera_person_hits");
                        resultObject.put("Hit",object.getJSONObject("_source"));
                        buckets.add(resultObject);
                    } else {
                        final JSONObject resultObject = list.get(0);
                        resultObject.put("Key", resultObject.remove("key"));
                        resultObject.put("DocCount", resultObject.remove("doc_count"));
                        final JSONObject object = resultObject.getJSONObject("camera_person_hits").getJSONObject("hits").getJSONArray("hits").getJSONObject(0);
                        processHit(object);
                        resultObject.remove("camera_person_hits");
                        resultObject.put("Hit",object.getJSONObject("_source"));
                        buckets.add(resultObject);
                    }
                }
            });
            return FastJsonUtils.JsonBuilder.ok().list(buckets).property("Took", tooks).json();
        }

        private void increment(AtomicInteger count) {
            int oldValue = count.get();
            while (!count.compareAndSet(oldValue, oldValue + 1)) {
                oldValue = count.get();
            }
        }

        private void increment(AtomicLong atomicLong, long addValue) {
            long oldValue = atomicLong.get();
            while (!atomicLong.compareAndSet(oldValue, oldValue + addValue)) {
                oldValue = atomicLong.get();
            }
        }
    }
}
