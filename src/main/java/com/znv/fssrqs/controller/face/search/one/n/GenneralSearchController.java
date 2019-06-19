package com.znv.fssrqs.controller.face.search.one.n;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import com.znv.fssrqs.service.face.search.one.n.CommonSearch;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.18 下午1:53
 */

@RestController
@RequestMapping(produces = { "application/json;charset=UTF-8" })
public class GenneralSearchController {

    @Autowired
    private CommonSearch commonSearch;

    @RequestMapping(value = "/VIID/Faces/FaceSearch" ,method = RequestMethod.POST)
    public ResponseVo faceSearch(@Validated @RequestBody GeneralSearchParam generalSearchParam) throws IOException {
        JSONObject jsonObject = commonSearch.commonSearch(generalSearchParam);
        return ResponseVo.success(jsonObject);
    }
}