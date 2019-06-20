package com.znv.fssrqs.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.LibRelationMapper;
import com.znv.fssrqs.entity.mysql.PersonLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class HkSdkService {
    @Autowired
    private LibRelationMapper libRelationService;

    public Integer addHkLib(PersonLib personLib, String libId) {
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put(CommonConstant.HkUri.ARTEMIS_PROTOCAL, CommonConstant.HkUri.ARTEMIS_PATH + CommonConstant.HkUri.ADD_LIB);
            }
        };
        /**
         * 添加海康静态库
         */
        JSONObject requestParams = new JSONObject();
        requestParams.put("listLibName", personLib.getLibName());
        requestParams.put("typeId", "1".equals(personLib.getPersonLibType()) ? 2 : 3);
        requestParams.put("describe", personLib.getDescription());
        String res = ArtemisHttpUtil.doPostStringArtemis(path, JSONObject.toJSONString(requestParams), null, null, "application/json", null);
        if (org.apache.commons.lang3.StringUtils.isEmpty(res)) {
            return -1;
        }
        JSONObject result = JSONObject.parseObject(res, JSONObject.class);
        if (result == null || CommonConstant.HkSdkErrorCode.ERROR == result.getInteger("code")) {
            return -1;
        } else {
            Map<String, Object> map = Maps.newConcurrentMap();
            map.put("fssLibId", libId);
            JSONObject data = (JSONObject) result.getJSONObject("data");
            map.put("hkLibId", data.getString("listLibId"));
            //保存关系映射
            libRelationService.insert(map);
        }
        return 1;

    }

    /**
     * 删除海康库
     *
     * @param libId   静态库ID
     * @param hklibId 海康库ID
     * @return
     */
    public int delHkLib(String libId, String hklibId) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(libId)) {
            return CommonConstant.HkSdkErrorCode.ERROR;
        }
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put(CommonConstant.HkUri.ARTEMIS_PROTOCAL, CommonConstant.HkUri.ARTEMIS_PATH + CommonConstant.HkUri.DEL_LIB);
            }
        };
        JSONObject requestParams = new JSONObject();
        requestParams.put("listLibId", hklibId);
        //删除海康静态库
        String res = ArtemisHttpUtil.doPostStringArtemis(path, JSONObject.toJSONString(requestParams), null, null, "application/json", null);
        if (org.apache.commons.lang3.StringUtils.isEmpty(res)) {
            return CommonConstant.HkSdkErrorCode.ERROR;
        }
        JSONObject result = JSONObject.parseObject(res, JSONObject.class);
        if (result == null || CommonConstant.HkSdkErrorCode.ERROR == result.getInteger("code")) {
            return CommonConstant.HkSdkErrorCode.ERROR;
        } else {
            Map<String, Object> map = Maps.newConcurrentMap();
            map.put("fssLibId", libId);
            //删除关系映射
            libRelationService.delete(map);
        }
        return CommonConstant.HkSdkErrorCode.SUCCESS;
    }
}
