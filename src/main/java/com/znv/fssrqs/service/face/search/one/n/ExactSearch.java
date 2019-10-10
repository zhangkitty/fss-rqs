package com.znv.fssrqs.service.face.search.one.n;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.util.*;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.stereotype.Service;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.18 下午4:25
 */
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import com.znv.fssrqs.service.face.search.one.n.dto.CommonSearchParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.znv.fssrqs.util.FormatObject.formatTime;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.18 下午4:24
 */
@Service
@Slf4j
public class ExactSearch {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private ModelMapper modelMapper;

    // 定义全局变量 标志查询线程状态 0：未执行 1：已执行
    private static Map<String, Integer> concurrentHashMap = new ConcurrentHashMap<String, Integer>();

    public JSONObject startSearch(GeneralSearchParam params) throws IOException {

        String paramsHashCode =MD5Util.encode(params.toString());
        concurrentHashMap.put("eventId", 0);
        log.error(concurrentHashMap.get("eventId").toString(),"添加");
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    doSearch(params);
                    concurrentHashMap.put("eventId", 1);
                    log.error(concurrentHashMap.get("eventId").toString(),"跑完以后运行");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        JSONObject ret = new JSONObject();
        ret.put("EventID",paramsHashCode);
        return  ret;

    }

    /**
     * 查询36个搜索的结果并把结果写到"history_exact_search_result_n_project"这个索引中
     * @param params
     */
    private void doSearch(GeneralSearchParam params) throws IOException {

        CommonSearchParams commonSearchParams = modelMapper.map(params,CommonSearchParams.class);
        commonSearchParams.setFrom((params.getCurrentPage()-1)*params.getPageSize());

        String[] arr = new String[commonSearchParams.getFeatureValue().length];
        for(int i = 0 ;i<commonSearchParams.getFeatureValue().length;i++){
            arr[i] = (String) JSONObject.parseObject(FaceAIUnitUtils.getImageFeature(commonSearchParams.getFeatureValue()[i])).get("feature");
        }
        commonSearchParams.setFeatureValue(arr);
        JSONObject paramsWithTempId = new JSONObject();
        paramsWithTempId.put("id","template_fss_arbitrarysearch");
        paramsWithTempId.put("params",commonSearchParams);
        HttpEntity httpEntity = new NStringEntity(paramsWithTempId.toJSONString()
                ,ContentType.APPLICATION_JSON);

        int coarseCodeNum = 36;
        String url = "";


        for (int i = 0; i < coarseCodeNum; i++) {
            // StringBuilder indexNamePrepix = new StringBuilder("history_fss_data_n_project_v1_2");
            StringBuilder indexNamePrepix = new StringBuilder(EsBaseConfig.getInstance().getEsIndexHistoryPrefix());
            final String indexName = indexNamePrepix.append("-").append(i).toString();
            url = indexName+"/history_data/_search/template";

            Response response = elasticSearchClient.getInstance().getRestClient().performRequest("get",url,Collections.emptyMap(),httpEntity);

            JSONObject result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));

            JSONArray esHits = result.getJSONObject("hits").getJSONArray("hits");
            if (esHits.size() > 0) {
                this.bulkWriteToEs("history_exact_search_result_n_project", "history_data", esHits, MD5Util.encode(params.toString()), i, indexName);
            }

        }
    }

    //index:要写数据的索引名； type:要写数据的type； esHits：要写的数据； eventId：事务id，由web发送，标志一次查询； searchNum：查询索引的顺序号；indexName：查询索引的名称
    public void bulkWriteToEs(String index, String type, JSONArray esHits, String eventId, Object searchNum, String indexName) {
        try {
            FeatureCompUtil fc = new FeatureCompUtil();
            int len = esHits.size();
            //  if(len > 0){ //已在调用函数中做了判断1
            TransportClient client = elasticSearchClient.getClient();
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            int statusCode = 0;
            for (int i = 0; i < len; i++) {
                JSONObject hit = esHits.getJSONObject(i);
                float score = fc.Normalize(hit.getFloatValue("_score"));
                // System.out.println("未归一化之前的score："+hit.getFloatValue("_score"));
                //System.out.println("归一化之后的score："+ score);
                JSONObject source = hit.getJSONObject("_source");
                String enterTime = source.getString("enter_time");
                String uuid = source.getString("uuid");
                String docId = formatTime(enterTime) + uuid;
                if (i == len - 1) {
                    statusCode = 1;
                }
                bulkRequest.add(client.prepareIndex(index, type, String.valueOf(docId))
                        .setSource(jsonBuilder()
                                .startObject()
                                .field("score", score)
                                .field("big_picture_uuid", source.getString("big_picture_uuid"))
                                .field("img_url", source.getString("img_url"))
                                .field("enter_time", enterTime)
                                .field("leave_time", source.getString("leave_time"))
                                .field("op_time", source.getString("op_time"))
                                .field("lib_id", source.getInteger("lib_id"))
                                .field("person_id", source.getString("person_id"))
                                .field("is_alarm", source.getString("is_alarm"))
                                .field("similarity", source.getFloatValue("similarity"))
                                .field("camera_id", source.getString("camera_id"))
                                .field("camera_name", source.getString("camera_name"))
                                .field("office_id", source.getString("office_id"))
                                .field("office_name", source.getString("office_name"))
                                .field("img_width", source.getIntValue("img_width"))
                                .field("img_height", source.getIntValue("img_height"))
                                .field("left_pos", source.getIntValue("left_pos"))
                                .field("top", source.getIntValue("top"))
                                .field("index_name", indexName)
                                .field("search_number", searchNum)
                                .field("status_code", statusCode)
                                .field("event_id", eventId)
                                //社区1:N添加device_kind和door_event_id
                                .field("device_kind", source.getInteger("device_kind"))
                                .field("uuid", source.getString("uuid"))
                                .field("coarse_id", source.getIntValue("coarse_id"))
                                .field("door_event_id", source.getString("door_event_id"))
                                .endObject()
                        )
                );
            }
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                // process failures by iterating through each bulk response item
                log.error(bulkRequest.toString());
                // System.out.println(bulkRequest.toString());
            }
            //}
        } catch (Exception e) {
            log.error("es批量写数据异常", e);
        }
    }


    public JSONObject queryExactSearchRet(String host, JSONObject contentparams) throws IOException {
        JSONObject ret = new JSONObject();
        String eventId = contentparams.getString("EventID");
        String sortField = contentparams.getString("SortField");
        if (StringUtils.isNotEmpty(sortField) && sortField.equals("_score")) {
            sortField = "score";
        }
        String sortOrder = contentparams.getString("SortOrder");
        int currentPage = contentparams.getInteger("CurrentPage");
        int pageSize = contentparams.getInteger("PageSize");
        int from = (currentPage - 1) * pageSize;
        int size = pageSize;
        String ip = elasticSearchClient.getHost();
        Integer port = elasticSearchClient.getPort();
        String index = "history_exact_search_result_n_project";
        String url = String.format("%s:%s/%s/%s/%s", "http://"+ip, port, index, "history_data", "_search");
        String remoteIp = host.split(":")[0];
        JSONObject params = new JSONObject();
        JSONObject bool = new JSONObject();
        JSONObject filter = new JSONObject();
        JSONArray sort = new JSONArray();
        JSONObject type = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("order", sortOrder);
        type.put(sortField, order);
        sort.add(type);
        filter.put("filter", JSON.parse("{\"term\":{\"event_id\":\"" + eventId + "\"}}"));
        bool.put("bool", filter);
        params.put("from", from);
        params.put("size", size);
        params.put("query", bool);
        params.put("sort", sort);
        try {
            String result = HttpUtils.sendPostData(params.toJSONString(), url);
            JSONObject resultJson = JSONObject.parseObject(result);
            resultJson = resultJson.getJSONObject("hits");
            ret.put("Code", 10000);
            // ret.put("queryStatus", concurrentHashMap.get(eventId));
            JSONArray hitsJsonarray = resultJson.getJSONArray("hits");
            JSONArray hitsArray = new JSONArray();
            for (Object object : hitsJsonarray) {
                JSONObject retJson = (JSONObject) object;
                JSONObject o = retJson.getJSONObject("_source");
                JSONObject resObj = new JSONObject();

                resObj.put("OfficeID", o.getString("office_id"));
                resObj.put("Score", o.getDoubleValue("score"));
                resObj.put("IsAlarm", o.getString("is_alarm"));
                resObj.put("LibID", o.getIntValue("lib_id"));
                resObj.put("Top", o.getIntValue("top"));
                resObj.put("Similarity", o.getDoubleValue("similarity"));
                resObj.put("DeviceName", o.getString("camera_name"));
                resObj.put("PersonID", o.getString("person_id"));
                resObj.put("ImgWidth", o.getIntValue("img_width"));
                resObj.put("ImgHeight", o.getIntValue("img_height"));
                resObj.put("DeviceID", o.getString("camera_id"));
                resObj.put("LeftPos", o.getIntValue("left_pos"));
                resObj.put("OfficeName", o.getString("office_name"));
                resObj.put("DeviceKind", o.getString("device_kind"));
                resObj.put("CoarseId", o.getString("coarse_id"));
                resObj.put("BigPictureUuid", o.getString("big_picture_uuid"));
                resObj.put("SmallPictureUuid", o.getString("img_url"));
                resObj.put("Uuid", o.getString("uuid"));

                String smallUuid = o.getString("img_url");
                resObj.put("OpTime", formatTime(o.getString("op_time")));
                resObj.put("EnterTime", formatTime(o.getString("enter_time")));
                resObj.put("FaceDisAppearTime", formatTime(o.getString("leave_time")));
                String imgUrl = ImageUtils.getImgUrl(remoteIp, "GetSmallPic", smallUuid);
                resObj.put("SmallPictureUrl", imgUrl);
                String bigPictureUuid = o.getString("big_picture_uuid");
                if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)) {
                    resObj.put("BigPictureUrl", "");
                } else {
                    resObj.put("BigPictureUrl", "");
                    resObj.put("BigPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
                }

                hitsArray.add(resObj);
            }
            JSONObject data = new JSONObject();
            data.put("List", hitsArray);
            data.put("TotalSize", resultJson.getIntValue("total"));
            data.put("QueryStatus", concurrentHashMap.get("eventId"));
            return data;
        } catch (Exception e) {
            throw e;
        }
    }



}

