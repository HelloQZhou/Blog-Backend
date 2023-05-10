package com.qzhou.controller;

import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.UserDto;
import com.qzhou.domain.vo.UserInsertVo;
import com.qzhou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseResult getUserList(Integer pageNum, Integer pageSize,String userName,String phonenumber,String status){

        return userService.getUserList(pageNum,pageSize,userName,phonenumber,status);
    }

    @PostMapping
    public ResponseResult addUser(@RequestBody UserInsertVo userInsertVo){
        return userService.addUser(userInsertVo);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteUserById(@PathVariable("id") Long id){
        return userService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public ResponseResult getUserById(@PathVariable("id") Long id){
        return userService.getUserById(id);
    }
    @PutMapping
    public ResponseResult updateUserInfo(@RequestBody UserInsertVo userInsertVo){
        return userService.updateUserInfoFromAdmin(userInsertVo);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeUserStatus(@RequestBody UserDto userDto){
        return userService.changeUserStatus(userDto);
    }
}
