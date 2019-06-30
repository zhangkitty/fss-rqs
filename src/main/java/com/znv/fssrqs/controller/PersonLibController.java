package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.config.HkSdkConfig;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.entity.mysql.PersonLib;
import com.znv.fssrqs.service.HkSdkService;
import com.znv.fssrqs.dao.mysql.LibRelationMapper;
import com.znv.fssrqs.service.PersonStaticLibService;
import com.znv.fssrqs.util.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dongzelong on  2019/6/1 13:53.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
@Slf4j
public class PersonLibController {
    @Autowired
    private PersonStaticLibService personLibService;
    @Autowired
    private HkSdkConfig hkSdkConfig;
    @Autowired
    private HkSdkService hksdkService;
    @Resource
    private LibRelationMapper libRelationService;

    /**
     * 人员静态库查询
     */
    @GetMapping("/person/static/libs")
    public String getAll(@RequestParam Map<String, Object> params) {
        String userId = "11000000000";
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().list(personLibService.getUserLibTreeByUserId(userId)).json(), new PascalNameFilter());
    }

    /**
     * 新增静态库
     */
    @PostMapping("/person/static/lib")
    public String add(@RequestBody String body) {
        PersonLib personLib = JSON.parseObject(body, PersonLib.class);
        personLib.setLibID(-1);
        HashMap<String, Object> result = personLibService.configLib(personLib);
        Long ret = (Long) result.get("ret");
        if (1L == ret) {
            Long libId = (Long) result.get("lib_id");
            if (hkSdkConfig.getIsSwitch()) {
                Integer errorCode = hksdkService.addHkLib(personLib, String.valueOf(libId));
                if (errorCode == -1) {
                    return FastJsonUtils.JsonBuilder.error().message("保存海康静态库失败").json().toJSONString();
                }
            }
            return FastJsonUtils.JsonBuilder.ok().property("LibID", libId).json().toJSONString();
        } else {
            return FastJsonUtils.JsonBuilder.badRequest(400).message("静态库名称重复").json().toJSONString();
        }
    }

    /**
     * 修改静态库
     *
     * @param body
     * @param libId
     * @return
     */
    @PutMapping("/person/static/lib/{libId}")
    public String modify(@RequestBody String body, @PathVariable("libId") Integer libId) {
        PersonLib personLib = JSON.parseObject(body, PersonLib.class);
        personLib.setLibID(libId);
        HashMap<String, Object> result = personLibService.configLib(personLib);
        Long ret = (Long) result.get("ret");
        if (1L == ret) {
            return FastJsonUtils.JsonBuilder.ok().property("LibID", libId).json().toJSONString();
        } else {
            return FastJsonUtils.JsonBuilder.badRequest(400).message("静态库名称重复").json().toJSONString();
        }
    }

    /**
     * 删除静态库
     */
    @DeleteMapping(value = "/person/static/lib/{libId}")
    public String delById(@PathVariable(value = "libId") Integer libId, @RequestParam Map<String, Object> params) {
        HashMap<String, Object> result = personLibService.deleteById(libId);
        Long ret = (Long) result.get("ret");
        //0-失败，1-成功
        if (1L == ret) {
            if (hkSdkConfig.getIsSwitch()) {
                Map<String, Object> map = libRelationService.selectOne(libId);
                if (!Objects.isNull(map)) {
                    String hkLibID = (String) map.get("hk_lib_id");
                    int errorCode = hksdkService.delHkLib(String.valueOf(libId), hkLibID);
                    if (errorCode == CommonConstant.HkSdkErrorCode.ERROR) {
                        return FastJsonUtils.JsonBuilder.error().message("删除海康静态库失败").json().toJSONString();
                    }
                }
            }
            return FastJsonUtils.JsonBuilder.ok().json().toJSONString();
        } else {
            return FastJsonUtils.JsonBuilder.badRequest(400).message("人员库下有人员信息或数据异常，不能删除!").json().toJSONString();
        }
    }
}
