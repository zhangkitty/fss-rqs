package com.znv.fssrqs.controller.reid;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.reid.ReidTraceService;
import com.znv.fssrqs.vo.ResponseVo;
import jdk.nashorn.internal.scripts.JO;
import org.mortbay.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午11:17
 */

@RestController
public class ReidTraceController {

    @Autowired
    private ReidTraceService reidTraceService;

    @RequestMapping(value = "/reid-trace",method = RequestMethod.POST)
    public ResponseVo getTraces(@RequestBody String request){
        String fusedId;
        try {
            JSONObject jsonObject = JSONObject.parseObject(request);
            fusedId = jsonObject.getString("FusedId");
        }catch (Exception e){
            return ResponseVo.error("入参有误");
        }
        ArrayList arrayList = reidTraceService.getTrace(fusedId);
        return ResponseVo.success(arrayList);
    }
}
