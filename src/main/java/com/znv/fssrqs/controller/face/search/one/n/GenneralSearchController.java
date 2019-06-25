package com.znv.fssrqs.controller.face.search.one.n;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.param.face.search.one.n.ExactSearchResultParams;
import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import com.znv.fssrqs.service.face.search.one.n.CommonSearch;
import com.znv.fssrqs.service.face.search.one.n.ExactSearch;
import com.znv.fssrqs.service.face.search.one.n.FastSearch;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private FastSearch fastSearch;

    @Autowired
    private ExactSearch exactSearch;

    @RequestMapping(value = "/VIID/Faces/FaceSearch" ,method = RequestMethod.POST)
    public ResponseVo faceSearch(@RequestHeader("Host") String host,@Validated @RequestBody GeneralSearchParam generalSearchParam) throws IOException {

        JSONObject jsonObject = new JSONObject();
        switch (generalSearchParam.getQueryType()) {
            case 1:
                jsonObject = fastSearch.fastSearch(host,generalSearchParam);
                break;
            case 2:
                jsonObject = exactSearch.startSearch(generalSearchParam);
                break;
            default:
                jsonObject = commonSearch.commonSearch(generalSearchParam);
                break;
        }


        return ResponseVo.success(jsonObject);
    }

    @RequestMapping(value = "/VIID/Faces/ExactSearch",method = RequestMethod.POST)
    public ResponseVo getExactSearchResult(@RequestHeader("Host") String host, @Validated @RequestBody  ExactSearchResultParams exactSearchResultParams) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("EventID",exactSearchResultParams.getEventID());
        jsonObject.put("CurrentPage",exactSearchResultParams.getCurrentPage());
        jsonObject.put("PageSize",exactSearchResultParams.getPageSize());
        jsonObject.put("SortField",exactSearchResultParams.getSortField());
        jsonObject.put("SortOrder",exactSearchResultParams.getSortOrder());
        JSONObject ret = exactSearch.queryExactSearchRet(host,jsonObject);

        return  ResponseVo.success(ret);

    }
}
