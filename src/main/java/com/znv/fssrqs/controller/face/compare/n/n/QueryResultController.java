package com.znv.fssrqs.controller.face.compare.n.n;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.compareservice.QueryResultService;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:10
 */

@RestController
public class QueryResultController {


    @Autowired
    private QueryResultService queryResultService;

    /**
     * n:m的分析结果查询
     * @param queryResultParams
     * @return
     */

    @RequestMapping(value = "/site/FSSAPP/pc/nvsm/pairsearch.ds",method = RequestMethod.POST)
    public ResponseVo queryResult(@RequestBody QueryResultParams queryResultParams){

        return  ResponseVo.success(queryResultService.queryResultService(queryResultParams));

    }
}
