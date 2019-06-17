package com.znv.fssrqs.controller;

import com.znv.fssrqs.param.UserParam;
import com.znv.fssrqs.service.UserService;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/user" ,produces = { "application/json;charset=UTF-8" })
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/getAllUser")
    public ResponseVo getAllUser() throws Exception{
        List list =  userService.findAll();
        return ResponseVo.success(list);
    }

    @GetMapping(value = "/getUser")
    public ResponseVo getUser() throws Exception{
        List list =  userService.find();
        return ResponseVo.success(list);
    }

    @PostMapping(value = "/addUser")
    public ResponseVo addUser(@RequestBody @Validated  UserParam userParam){
        return ResponseVo.success("添加用户成功");
    }
}
