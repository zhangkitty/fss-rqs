package com.znv.fssrqs.controller;

import com.znv.fssrqs.param.UserParam;
import com.znv.fssrqs.service.UserService;
import com.znv.fssrqs.vo.Response;
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
    public Response getAllUser() throws Exception{
        List list =  userService.findAll();
        return Response.success(list);
    }

    @PostMapping(value = "/addUser")
    public Response addUser(@RequestBody @Validated  UserParam userParam){
        return Response.success("添加用户成功");
    }
}
