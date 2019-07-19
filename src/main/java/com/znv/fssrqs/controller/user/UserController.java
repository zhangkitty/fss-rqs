package com.znv.fssrqs.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.UserService;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value="/user", produces = { "application/json;charset=UTF-8" })
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public ResponseVo login(HttpServletRequest request, @RequestHeader("Host") String host, @RequestBody String body){
        JSONObject loginObject = JSONObject.parseObject(body);

        // 更新session
        HttpSession session = request.getSession();
        session.invalidate();
        session = request.getSession();

        HashMap<String, Object> params = new HashMap<>();
        params.put("userName", loginObject.getString("UserName"));
        params.put("userPwd", loginObject.getString("UserPwd"));
        params.put("sessionId", session.getId());
        params.put("serverId", loginObject.getString("ServerId"));
        params.put("loginTime", DataConvertUtils.dateToStr());
        String remoteIp = host.split(":")[0];
        params.put("clientIp", remoteIp);
        params.put("loginClientType", "1"); // 1 WEB
        Map<String, Object> ret =  userService.upCfgUserlogin(params);
        JSONObject retData = new JSONObject();
        if (ret.containsKey("user_id")) {
            retData.put("UserId", ret.get("user_id"));
        }
        if (ret.containsKey("user_name")) {
            retData.put("UserName", ret.get("user_name"));
        }
        if (ret.containsKey("precinct_id")) {
            retData.put("PrecinctId", ret.get("precinct_id"));
        }
        if (ret.containsKey("user_level")) {
            retData.put("UserLevel", ret.get("user_level"));
        }
        if (ret.containsKey("client_type")) {
            retData.put("ClientType", ret.get("client_type"));
        }
        if (ret.containsKey("user_state")) {
            retData.put("UserState", ret.get("user_state"));
        }

        session.setAttribute("UserLogin", retData);
        return ResponseVo.success(retData);
    }
}
