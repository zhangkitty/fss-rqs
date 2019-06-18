package com.znv.fssrqs.controller.personnel.management;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.znv.fssrqs.param.personnel.management.PersonListSearchParams;
import com.znv.fssrqs.service.personnel.management.PersonListService;
import com.znv.fssrqs.service.personnel.management.VIIDHKSDKService;
import com.znv.fssrqs.service.personnel.management.dto.HKPersonListSearchDTO;
import com.znv.fssrqs.service.personnel.management.dto.STPersonListSearchDTO;
import com.znv.fssrqs.vo.ResponseVo;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.06.10 下午3:05
 */

@RestController
@RequestMapping(produces = { "application/json;charset=UTF-8" })
public class PersonListController {

    @Autowired
    private PersonListService personListService;

    @Autowired
    private VIIDHKSDKService viidhksdkService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value="/VIID/Persons")
    public ResponseVo getPersonList(@Valid PersonListSearchParams personListSearchParams) throws Exception {
        if(personListSearchParams.getAlgorithmType()!=null&&personListSearchParams.getAlgorithmType().equals("1")){
            JSONObject result =  viidhksdkService.queryHkPerson(personListSearchParams);
            return ResponseVo.success(result);
        }
        JSONObject jsonObject = personListService.getPersonList(personListSearchParams);
        return ResponseVo.success(jsonObject);
    }

    @RequestMapping(value="/VIID/Persons",method = RequestMethod.POST)
    public ResponseVo PostPersonList(@RequestBody String json) throws Exception {

        PersonListSearchParams personListSearchParams = modelMapper.map(JSONObject.parseObject(json),PersonListSearchParams.class);

        if(personListSearchParams.getAlgorithmType()!=null&&personListSearchParams.getAlgorithmType().equals("1")){
            JSONObject result =  viidhksdkService.queryHkPerson(personListSearchParams);
            return ResponseVo.success(result);
        }
        JSONObject jsonObject = personListService.getPersonList(personListSearchParams);
        return ResponseVo.success(jsonObject);
    }
}
