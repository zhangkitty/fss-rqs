package com.znv.fssrqs.controller;

import com.znv.fssrqs.annotation.ParamCheck;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.param.UserParam;
import com.znv.fssrqs.service.RedisTemplateService;
import com.znv.fssrqs.service.UserService;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/user", produces = {"application/json;charset=UTF-8"})
public class TestController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplateService redisTemplateService;
    private int a=0;

    @GetMapping(value = "/getAllUser")
    public ResponseVo getAllUser() throws Exception {
        List list = userService.findAll();
        return ResponseVo.success(list);
    }

    @GetMapping(value = "/getUser")
    public ResponseVo getUser() throws Exception {
        List list = userService.find();
        return ResponseVo.success(list);
    }

    @PostMapping(value = "/addUser")
    public ResponseVo addUser(@RequestBody @Validated UserParam userParam) {
        return ResponseVo.success("添加用户成功");
    }

    @PostMapping("/test")
    public String test(@ParamCheck(params = {"name"}) String body) {
        final Set<String> devices = redisTemplateService.getSet("MDevice");
//        final List<String> list = redisTemplateService.multiGet(devices);
//        for (String device:list){
//            final JSONObject jsonObject = JSON.parseObject(device);
//            System.out.println(jsonObject);
//        }
        a+=1;
        System.out.println(a);
        //System.out.println(redisTemplateService.getCurrentDb());
        throw ZnvException.badRequest(20000, "1111");
    }
}
