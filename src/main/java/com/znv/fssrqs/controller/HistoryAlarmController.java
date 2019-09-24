package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.dao.mysql.PersonLibMapper;
import com.znv.fssrqs.service.elasticsearch.history.alarm.HistoryAlarmService;
import com.znv.fssrqs.service.hbase.PhoenixService;
import com.znv.fssrqs.util.Base64Util;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.ImageUtils;
import com.znv.fssrqs.vo.SearchRetrieval;
import com.znv.fssrqs.vo.TrackSearch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by dongzelong on  2019/6/26 10:46.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 历史告警数据管理
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Slf4j
public class HistoryAlarmController {
    private HttpServletResponse response;
    private HttpSession session;
    private HttpServletRequest request;
    private static String policeTypesStr = "{\"0\":\"图侦\",\"1\":\"刑侦\",\"2\":\"治安\",\"3\":\"国保\",\"4\":\"户籍\",\"5\":\"网警\",\"6\":\"经侦\",\"7\":\"派出所\",\"8\":\"缉毒\",\"9\":\"反恐\",\"10\":\"技侦\",\"11\":\"缉私\"}";
    private static JSONObject allPoliceTypes = JSON.parseObject(policeTypesStr);
    @Autowired
    private HistoryAlarmService historyAlarmService;
    @Autowired
    private PhoenixService phoenixService;
    private PersonLibMapper personLibMapper;

    @ModelAttribute
    public void bindModel(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession();
    }

    /**
     * 战果统计
     */
    @PostMapping("/alarm/result/statistics")
    public String getHistoryAlarm(@RequestHeader("Host") String host, @RequestBody String body) {
        SearchRetrieval searchRetrieval = JSON.parseObject(body, SearchRetrieval.class);
        return historyAlarmService.getAllByCondition(host, searchRetrieval);
    }

    /**
     * 时间轴轨迹查询,实时采集页面为历史表数据查询
     *
     * @param host
     * @param body
     */
    @PostMapping("/time/track/search")
    public String queryHistoryAlarm(@RequestHeader("Host") String host, @RequestBody String body) {
        String remoteIp = host.split(":")[0];
        TrackSearch trackSearch = JSONObject.parseObject(body, TrackSearch.class);
        //单值条件串
        JSONObject termQuery = new JSONObject();
        //多值条件串
        JSONObject multiQuery = new JSONObject();
        //范围条件串
        JSONObject rangeQuery = new JSONObject();
        JSONObject queryParams = new JSONObject();
        queryParams.put("query_term", termQuery);
        queryParams.put("query_multi", multiQuery);
        queryParams.put("query_range", rangeQuery);
        parseArrayStrParam(queryParams, trackSearch.getCameraIDs(), "camera_id");
        parseArrayStrParam(queryParams, trackSearch.getOfficeIDs(), "office_id");
        if (!StringUtils.isEmpty(trackSearch.getStartTime())) {
            rangeQuery.put("start_time", trackSearch.getStartTime());
        }

        if (!StringUtils.isEmpty(trackSearch.getEndTime())) {
            rangeQuery.put("end_time", trackSearch.getEndTime());
        }
        int count = trackSearch.getTotalRows(); // 总条数
        int totalPage = trackSearch.getTotalPage(); // 总页码
        count = count == 0 ? -1 : count;
        totalPage = totalPage == 0 ? -1 : totalPage;

        queryParams.put("page_no", trackSearch.getCurrentPage());
        queryParams.put("page_size", trackSearch.getPageSize());
        queryParams.put("total_page", totalPage);
        queryParams.put("count", count);
        queryParams.put("id", "31005");
        queryParams.put("table_name", HdfsConfigManager.getString("fss.phoenix.table.history.name"));
        JSONObject hbaseResult = phoenixService.query(queryParams);
        hbaseResult.put("TotalRows", hbaseResult.getIntValue("Count"));
        hbaseResult.remove("Count");
        JSONArray jsonArray = hbaseResult.getJSONArray("Data");
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (StringUtils.isEmpty(jsonObject.getString("IsAlarm"))) {
                    String alarmTYpe = jsonObject.getString("AlarmType");
                    String optTime = jsonObject.getString("OpTime");
                    String libId = jsonObject.getString("LibId");
                    String personId = jsonObject.getString("PersonId");
                    String paramsStr = String.format("%s&%s&%s&%s", personId, libId, alarmTYpe, optTime);
                    //人员图片ID
                    jsonObject.put("PersonImgUrl", ImageUtils.getImgUrl(remoteIp, "get_fss_personimage", Base64Util.encodeString(paramsStr)));
                }

                String smallUuid = jsonObject.getString("ImgUrl");
                String imgUrl = ImageUtils.getImgUrl(remoteIp, "GetSmallPic", smallUuid);
                //小图地址
                jsonObject.put("SmallPictureUrl", imgUrl);
                String bigPictureUuid = jsonObject.getString("BigPictureUuid");
                if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)) {
                    jsonObject.put("BigPictureUrl", "");
                } else {
                    jsonObject.put("BigPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
                }
                jsonObject.put("CameraID", jsonObject.getString("CameraId"));
                jsonObject.remove("CameraId");
                jsonObject.put("CurrentTime", DataConvertUtils.dateToStr(new Date()));
                jsonObject.put("PersonID", jsonObject.getString("PersonId"));
                jsonObject.remove("PersonId");
            }
        }
        return FastJsonUtils.JsonBuilder.ok().object(hbaseResult).json().toJSONString();
    }

    /**
     * 事件流查询
     *
     * @param host
     * @param body
     * @return
     */
    @PostMapping("/event/stream")
    public String getEventStream(@RequestHeader("Host") String host, @RequestBody String body) {
        String remoteIp = host.split(":")[0];
        TrackSearch trackSearch = JSONObject.parseObject(body, TrackSearch.class);
        //单值条件串
        JSONObject termQuery = new JSONObject();
        //多值条件串
        JSONObject multiQuery = new JSONObject();
        //范围条件串
        JSONObject rangeQuery = new JSONObject();
        JSONObject queryParams = new JSONObject();
        queryParams.put("query_term", termQuery);
        queryParams.put("query_multi", multiQuery);
        queryParams.put("query_range", rangeQuery);
        parseArrayStrParam(queryParams, trackSearch.getCameraIDs(), "camera_id");
        parseArrayStrParam(queryParams, trackSearch.getOfficeIDs(), "office_id");
        getEventStreamParams(trackSearch, queryParams);
        if (!StringUtils.isEmpty(trackSearch.getStartTime())) {
            rangeQuery.put("start_time", trackSearch.getStartTime());
        }

        if (!StringUtils.isEmpty(trackSearch.getEndTime())) {
            rangeQuery.put("end_time", trackSearch.getEndTime());
        }
        int count = trackSearch.getTotalRows(); // 总条数
        int totalPage = trackSearch.getTotalPage(); // 总页码
        count = count == 0 ? -1 : count;
        totalPage = totalPage == 0 ? -1 : totalPage;
        queryParams.put("page_no", trackSearch.getCurrentPage());
        queryParams.put("page_size", trackSearch.getPageSize());
        queryParams.put("total_page", totalPage);
        queryParams.put("count", count);
        //查询告警表
        queryParams.put("id", "31006");
        queryParams.put("table_name", HdfsConfigManager.getString("fss.phoenix.table.alarm.name"));
        JSONObject queryRange = queryParams.getJSONObject("query_range");
        parseArrayStrParam(queryParams, trackSearch.getEventIDs(), "ControlEventID");
        Integer orderType = trackSearch.getOrderType();
        String orderField = null;
        //默认按照告警时间排序 0-相似度,1-告警时间；
        if (Objects.isNull(orderType)) {
            orderField = "1";
        } else {
            if ("1".equals(orderType)) {
                orderField = "0";
            } else {
                orderField = "1";
            }
        }
        Integer orderWay = trackSearch.getOrderWay();
        //默认按照降序 //0-降序,1-升序;
        queryRange.put("order_type", Objects.isNull(orderWay) ? "0" : orderWay);
        queryRange.put("order_field", orderField);
        JSONObject hbaseResult = phoenixService.query(queryParams);

        hbaseResult.put("TotalRows", hbaseResult.getIntValue("Count"));
        hbaseResult.remove("Count");
        JSONArray jsonArray = hbaseResult.getJSONArray("Data");
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (StringUtils.isEmpty(jsonObject.getString("IsAlarm"))) {
                    String alarmTYpe = jsonObject.getString("AlarmType");
                    String optTime = jsonObject.getString("OpTime");
                    String libId = jsonObject.getString("LibId");
                    String personId = jsonObject.getString("PersonId");
                    String paramsStr = String.format("%s&%s&%s&%s", personId, libId, alarmTYpe, optTime);
                    //人员图片ID
                    jsonObject.put("PersonImgUrl", ImageUtils.getImgUrl(remoteIp, "get_fss_personimage", Base64Util.encodeString(paramsStr)));
                }

                String smallUuid = jsonObject.getString("ImgUrl");
                String imgUrl = ImageUtils.getImgUrl(remoteIp, "GetSmallPic", smallUuid);
                //小图地址
                jsonObject.put("ImgUrl", imgUrl);
                String bigPictureUuid = jsonObject.getString("BigPictureUuid");
                if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)) {
                    jsonObject.put("BigPictureUrl", "");
                } else {
                    jsonObject.put("BigPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
                }
                jsonObject.put("CameraID", jsonObject.getString("CameraId"));
                jsonObject.remove("CameraId");
                jsonObject.put("CurrentTime", DataConvertUtils.dateToStr(new Date()));
                jsonObject.put("PersonID", jsonObject.getString("PersonId"));
                jsonObject.remove("PersonId");
            }
        }
        return FastJsonUtils.JsonBuilder.ok().object(hbaseResult).json().toJSONString();
    }

    private void parseArrayStrParam(JSONObject alarmSearch, List<String> list, String condKey) {
        if (list != null && list.size() > 0) {
            if (list.size() == 1) {
                alarmSearch.getJSONObject("query_term").put(condKey, list.get(0));
            } else {
                alarmSearch.getJSONObject("query_multi").put(condKey, list);
            }
        }
    }

    private void getEventStreamParams(TrackSearch trackSearch, JSONObject alarmSearch) {
        List<JSONObject> filterConditions = trackSearch.getFilterCondition();
        JSONObject queryTerm = alarmSearch.getJSONObject("query_term");
        JSONObject queryRange = alarmSearch.getJSONObject("query_range");
        if (filterConditions != null) {
            for (JSONObject obj : filterConditions) {
                String queryParamName = obj.getString("Name");
                String queryValue = obj.getString("Value");
                String operator = obj.getString("Operator");
                switch (operator) {
                    case "eq":
                        if ("person_id".equals(queryParamName)) {
                            queryTerm.put("person_id", queryValue);
                        } else {
                            queryTerm.put(queryParamName, queryValue);
                        }
                        break;
                    case "between":
                        String[] split = queryValue.split(",");
                        if (split.length == 2) {
                            queryRange.put("start_time", split[0]);
                            queryRange.put("end_time", split[1]);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
