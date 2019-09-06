package com.znv.fssrqs.common;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.UserService;
import com.znv.fssrqs.util.DataConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;

@WebListener
@Slf4j
public class SessionListener implements HttpSessionListener {
    @Autowired
    private UserService userService;

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        JSONObject user = (JSONObject) session.getAttribute("UserLogin");
        if (user == null || user.getString("SessionId") == null) {
            return;
        }

        log.info("user {} session timeout", user.getString("UserId"));
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", user.getString("UserId"));
        params.put("sessionId", user.getString("SessionId"));
        params.put("serverId", user.getString("ServerId"));
        params.put("clientIp", user.getString("ClientIp"));
        params.put("logoutTime", DataConvertUtils.dateToStr());

        userService.upCfgUserLogout(params);
        return;
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
    }

}