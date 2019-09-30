package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.elasticsearch.person.cluster.PersonClusterService;
import com.znv.fssrqs.elasticsearch.person.cluster.PersonDetailService;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.I18nUtils;
import com.znv.fssrqs.util.ImageUtils;
import com.znv.fssrqs.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by dongzelong on  2019/9/6 12:45.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
public class PersonClusterController {
    @Autowired
    private PersonClusterService personClusterService;
    @Autowired
    private PersonDetailService personDetailService;

    /**
     * 人员聚类融合统计查询
     */
    @PostMapping("/ReID/cluster/statistics")
    public JSONObject getPersonFusedStatistics(@RequestBody String body) {
        JSONObject requestParams = JSON.parseObject(body);
        return personClusterService.getPersonAggs(requestParams);
    }

    @PostMapping("/ReID/cluster/track/search")
    public JSONObject  getPersonFusedDetail(@RequestHeader("Host") String host, @RequestBody String body, HttpServletRequest request) {
        JSONObject requestParams = JSON.parseObject(body);
        Result<JSONObject, String> esResult =  personClusterService.getPersonTask(requestParams);
        String remoteIp = host.split(":")[0];
        if (esResult.isErr()) {
            return FastJsonUtils.JsonBuilder.error(CommonConstant.StatusCode.INTERNAL_ERROR).message(I18nUtils.i18n(request.getLocale(),esResult.error())).json();
        }

        JSONObject esObject = esResult.value();
        JSONArray hitsJsonArray = esObject.getJSONArray("Hits");
        hitsJsonArray.parallelStream().forEach(object->{
            JSONObject jsonObject = (JSONObject) object;
            String pictureUuid = jsonObject.getString("SmallPictureUrl");
            if ("null".equals(pictureUuid) || StringUtils.isEmpty(pictureUuid)) {
                jsonObject.put("SmallPictureUrl", "");
            } else {
                jsonObject.put("SmallPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetSmallPic", pictureUuid));
            }
            String bigPictureUuid = jsonObject.getString("BigPictureUuid");
            if ("null".equals(bigPictureUuid) || StringUtils.isEmpty(bigPictureUuid)) {
                jsonObject.put("BigPictureUrl", "");
            } else {
                jsonObject.put("BigPictureUrl", ImageUtils.getImgUrl(remoteIp, "GetBigBgPic", bigPictureUuid));
            }
        });
        return FastJsonUtils.JsonBuilder.ok().list(hitsJsonArray).json();
    }

    @GetMapping("/ReID/cluster/fused/{fusedId}/detail")
    public JSONObject getReidFusedDetail(@PathVariable(value = "fusedId",required = true) String fusedId, @RequestParam Map<String, Object> params) {
        params.put("FusedID", fusedId);
        return personDetailService.getPersonDetail(params);
    }
}
