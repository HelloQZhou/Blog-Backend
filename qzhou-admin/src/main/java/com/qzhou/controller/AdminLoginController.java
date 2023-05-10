package com.qzhou.controller;

import com.qzhou.annotation.SystemLog;
import com.qzhou.domain.ResponseResult;
import com.qzhou.service.AdminLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "登录",value = "后台用户登录相关的接口")
public class AdminLoginController {
    @Autowired
    private AdminLoginService adminLoginService;

    @PostMapping("/user/login")
    @ApiOperation("管理员用户登录")
    @SystemLog(businessName = "管理员用户登录")
    public ResponseResult login(@RequestBody entity.User user){
        return adminLoginService.login(user);
    }

    @GetMapping("/getInfo")
    @SystemLog(businessName = "获取该管理员用户信息")
    @ApiOperation("获取该管理员用户信息(基本信息、权限信息、角色信息)")
    public ResponseResult getInfo(){
        return adminLoginService.getInfo();
    }

    @GetMapping("/getRouters")
    @SystemLog(businessName = "获取该管理员用户的路由信息")
    @ApiOperation("获取该管理员用户的路由信息")
    public ResponseResult getRouters(){
        return adminLoginService.getRouters();
    }

    @PostMapping("/user/logout")
    @SystemLog(businessName = "管理员用户退出")
    @ApiOperation("管理员用户退出")
    public ResponseResult logout(){
       return adminLoginService.logout();
    }
}
