package com.znv.fssrqs.controller.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.entity.mysql.PersonLib;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.param.UserParam;
import com.znv.fssrqs.service.UserService;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.vo.ResponseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value="/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public ResponseVo login(HttpServletRequest request, @RequestHeader("Host") String host, @RequestBody String body){
        JSONObject loginObject = JSONObject.parseObject(body);

        HashMap<String, Object> params = new HashMap<>();
        params.put("userName", loginObject.getString("UserName"));
        params.put("userPwd", loginObject.getString("UserPwd"));
        String sessionId = UUID.randomUUID().toString();
        params.put("sessionId", sessionId);
        params.put("serverId", loginObject.getString("ServerId"));
        params.put("loginTime", DataConvertUtils.dateToStr());
        String remoteIp = host.split(":")[0];
        params.put("clientIp", remoteIp);
        params.put("loginClientType", "1"); // 1 WEB
        Map<String, Object> ret =  userService.upCfgUserlogin(params);
        HttpSession session = request.getSession();
        session.invalidate();

        session = request.getSession();
        return ResponseVo.success(ret);
    }
}
