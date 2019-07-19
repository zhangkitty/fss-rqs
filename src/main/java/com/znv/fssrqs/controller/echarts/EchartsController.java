package com.znv.fssrqs.controller.echarts;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.param.echarts.PersonLibIdParam;
import com.znv.fssrqs.param.echarts.PersonListGroupQueryParam;
import com.znv.fssrqs.service.echarts.PersonLibInfoService;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.7.16 下午1:38
 */

@RestController
@RequestMapping(produces = { "application/json;charset=UTF-8" })
public class EchartsController {

    @Autowired
    private PersonLibInfoService personLibInfoService;

    @RequestMapping(value = "/personlib/reportinfo",method = RequestMethod.POST)
    public ResponseVo getPersonLibInfo(@RequestBody PersonLibIdParam libID){
        JSONObject jsonObject = personLibInfoService.requestSearch(new PersonListGroupQueryParam(),libID);
        return ResponseVo.success(jsonObject);
    }
}
