package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.google.common.collect.Lists;
import com.znv.fssrqs.config.HkSdkConfig;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.LibRelationMapper;
import com.znv.fssrqs.entity.mysql.PersonLib;
import com.znv.fssrqs.entity.mysql.UserGroup;
import com.znv.fssrqs.entity.mysql.UserLibRelation;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.service.HkSdkService;
import com.znv.fssrqs.service.PersonStaticLibService;
import com.znv.fssrqs.service.UserGroupService;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.LocalUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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
@RequestMapping(produces = {"application/json;charset=UTF-8"})
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
    @Autowired
    private UserGroupService userGroupService;

    /**
     * 人员静态库查询
     */
    @GetMapping("/person/static/libs")
    public String getLibs(@RequestParam Map<String, Object> params)
            throws BusinessException {
        JSONObject user = LocalUserUtil.getLocalUser();
//        if (user == null || !user.containsKey("UserId")) {
//            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
//        }
        params.put("UserID","11000000000");
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().list(personLibService.getUserLibTreeByUserId(params)).json(), new PascalNameFilter());
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

    @GetMapping("/person/static/lib")
    public JSONObject getUserLibByUserId(@RequestParam Map<String, Object> params) {
        JSONObject user = LocalUserUtil.getLocalUser();
        if (user == null || !user.containsKey("UserId")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
        }
        String userId = LocalUserUtil.getLocalUser().getString("UserId");
        List<Object> list = Lists.newArrayList();
        UserGroup userGroup = userGroupService.queryUserGroupByUserId(userId);
        String personLibType = (String) params.get("PersonLibType");
        if (userGroup != null) {
            int roleId = userGroup.getRoleID();
            if (roleId == 1) {
                List<PersonLib> personLibs = personLibService.queryLibByLibType(personLibType);
                if (personLibs != null && personLibs.size() > 0) {
                    for (PersonLib personLib : personLibs) {
                        list.add(personLib);
                    }
                }
            } else {
                List<UserLibRelation> userLibs = personLibService.queryUserLibByGoupId(userGroup.getUserGroupID(), personLibType);
                if (userLibs != null && userLibs.size() > 0) {
                    for (UserLibRelation userLibRelation : userLibs) {
                        list.add(userLibRelation);
                    }
                }
            }
        }
        return FastJsonUtils.JsonBuilder.ok().list(list).json();
    }
}
