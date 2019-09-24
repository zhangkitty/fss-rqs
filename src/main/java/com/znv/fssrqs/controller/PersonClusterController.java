package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.elasticsearch.person.cluster.PersonClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 人员聚类融合统计查询
     */
    @PostMapping("/ReID/cluster/statistics")
    public JSONObject getPersonFusedStatistics(@RequestBody String body) {
        JSONObject requestParams = JSON.parseObject(body);
        return personClusterService.getPersonAggs(requestParams);
    }

    @PostMapping("/ReID/cluster/track/search")
    public JSONObject getPersonFusedDetail(@RequestBody String body) {
        return null;
    }
}
