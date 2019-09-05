package com.znv.fssrqs.controller.reid;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.service.reid.ReidUnitService;
import com.znv.fssrqs.util.FastJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ReidAnalysisUnitController {

    @Autowired
    private ReidUnitService reidUnitService;

    @GetMapping(value = "/ReID/AnalysisUnit")
    public JSONObject getReIDAnalysisUnit(@RequestParam Map mapParam) {
        if (! mapParam.containsKey("PageSize")) {
            mapParam.put("PageSize", "10");
        }
        Integer pageSize = Integer.valueOf((String)mapParam.get("PageSize"));
        Integer currentPage = 1;
        if (mapParam.containsKey("CurrentPage")) {
            currentPage = Integer.valueOf((String)mapParam.get("CurrentPage"));
        }
        Integer from = (currentPage - 1) * pageSize;
        mapParam.put("From", from);
        mapParam.put("Size", pageSize);

        return FastJsonUtils.JsonBuilder.build(10000, "Ok", reidUnitService.getReidUnit(mapParam)).json();
    }

    @PostMapping(value = "/ReID/AnalysisUnit")
    public JSONObject addReIDAnalysisUnit(@RequestBody String body) {
        JSONObject reidAnalysisUnit = JSONObject.parseObject(body);
        if (! reidAnalysisUnit.containsKey("DeviceName")) {
            throw ZnvException.badRequest("RequestParamNull", "DeviceName");
        }
        if (! reidAnalysisUnit.containsKey("ServiceIP")) {
            throw ZnvException.badRequest("RequestParamNull", "ServiceIP");
        }
        if (! reidAnalysisUnit.containsKey("HttpPort")) {
            throw ZnvException.badRequest("RequestParamNull", "HttpPort");
        }
        if (! reidAnalysisUnit.containsKey("ManufactureID")) {
            throw ZnvException.badRequest("RequestParamNull", "ManufactureID");
        }
        if (! reidAnalysisUnit.containsKey("DeviceType")) {
            throw ZnvException.badRequest("RequestParamNull", "DeviceType");
        }

        reidUnitService.addReidUnit(reidAnalysisUnit);

        return FastJsonUtils.JsonBuilder.ok().json();
    }

    @PutMapping(value = "/ReID/AnalysisUnit")
    public JSONObject updateReIDAnalysisUnit(@RequestBody String body) {
        JSONObject reidAnalysisUnit = JSONObject.parseObject(body);
        if (! reidAnalysisUnit.containsKey("DeviceID")) {
            throw ZnvException.badRequest("RequestParamNull", "DeviceID");
        }

        Integer ret = reidUnitService.updateReidUnit(reidAnalysisUnit);
        if (ret == -1) {
            throw ZnvException.error("DeviceNotExist");
        }

        return FastJsonUtils.JsonBuilder.ok().json();
    }

    @DeleteMapping(value = "/ReID/AnalysisUnit")
    public JSONObject deleteReIDAnalysisUnit(@RequestParam Map mapParam) {
        if (! mapParam.containsKey("DeviceID")) {
            throw ZnvException.error("RequestParamNull", "DeviceID");
        }

        Integer ret = reidUnitService.deleteReidUnit(mapParam);
        if (ret == -1) {
            throw ZnvException.error("DeviceNotExist");
        } else if (ret == -2) {
            throw ZnvException.error("ExistAnalysisTask");
        }

        return FastJsonUtils.JsonBuilder.ok().json();
    }
}
