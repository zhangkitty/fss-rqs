package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.dao.mysql.MUserDao;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.LocalUserUtil;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by dongzelong on  2019/8/19 14:20.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 用户管理
 */
@RestController
@RequestMapping(value="/user", produces = { "application/json;charset=UTF-8" })
public class UserController {
    @Resource
    private MUserDao userDao;

    @GetMapping("/")
    public String getUserById(HttpServletRequest request) {
        final JSONObject localUser = LocalUserUtil.getLocalUser();
        if (localUser == null || !localUser.containsKey("UserId")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
        }
        final String userId = localUser.getString("UserId");
        final val mUserEntity = userDao.selectById(userId);
        return JSON.toJSONString(FastJsonUtils.JsonBuilder.ok().object(mUserEntity).json(), new PascalNameFilter());
    }

    @PostMapping("/password")
    public ResponseVo fixPassWord(HttpServletRequest request, @RequestBody JSONObject requestBody){
        HttpSession session = request.getSession();
        JSONObject userLoginObject = (JSONObject) session.getAttribute("UserLogin");
        String userId = userLoginObject.getString("UserId");
        Integer result = userDao.updateUserInfo(userId,requestBody.getString("password"));

        if(result>0){
            return ResponseVo.success(null);
        }else {
            return ResponseVo.error("失败");
        }
    }
}
