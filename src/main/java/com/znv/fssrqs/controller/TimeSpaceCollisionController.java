package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.TimeSpaceCollisionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dongzelong on  2019/10/9 11:35.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 时空碰撞
 */
@RestController
@Slf4j
public class TimeSpaceCollisionController {
    @Autowired
    private TimeSpaceCollisionService timeSpaceCollisionService;

    @PostMapping("/time/space/collision/search")
    public JSONObject getTimeSpaceCollision(@RequestBody String body) {
        final JSONObject params = JSON.parseObject(body, JSONObject.class);
        return timeSpaceCollisionService.getTimeSpaceCollision(params.getJSONArray("condition"),params);
    }
}
