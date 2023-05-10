package com.qzhou.controller;

import com.qzhou.annotation.SystemLog;
import com.qzhou.domain.ResponseResult;
import com.qzhou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/userInfo")
    public ResponseResult userInfo(){
        return userService.userInfo();
    }

    @PutMapping("/userInfo")
    @SystemLog(businessName = "更新用户信息")
    public ResponseResult updateUserInfo(@RequestBody entity.User user){
        return userService.updateUserInfo(user);
    }
    @PostMapping("/register")
    @SystemLog(businessName = "用户注册")
    public ResponseResult userRegister(@RequestBody entity.User user){
        return userService.userRegister(user);
    }
}
