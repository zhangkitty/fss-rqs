package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.dao.mysql.MUserDao;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.LocalUserUtil;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by dongzelong on  2019/8/19 14:20.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 用户管理
 */
@RestController
public class UserController {
    @Resource
    private MUserDao userDao;

    @GetMapping("/user")
    public String getUserById(HttpServletRequest request) {
        final JSONObject localUser = LocalUserUtil.getLocalUser();
        if (localUser == null || !localUser.containsKey("UserId")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
        }
        final String userId = localUser.getString("UserId");
        final val mUserEntity = userDao.selectById(userId);
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().object(mUserEntity).json(), new PascalNameFilter());
    }
}
