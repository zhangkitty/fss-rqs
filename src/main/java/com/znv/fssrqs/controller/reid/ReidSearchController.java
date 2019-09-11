package com.znv.fssrqs.controller.reid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.ReidMultiRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dongzelong on  2019/9/9 20:00.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
public class ReidSearchController {
    @Autowired
    private ReidMultiRetrievalService reidMultiRetrievalService;

    /**
     * 人体多维检索接口
     */
    @PostMapping("/reid/multi/dimensional/retrieval")
    public JSONObject getMultiDimensionalSearch(@RequestBody String body) {
        JSONObject params = JSON.parseObject(body);
        return reidMultiRetrievalService.getSearch(params);
    }
}
