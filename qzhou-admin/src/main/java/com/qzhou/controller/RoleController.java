package com.qzhou.controller;

import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.AddRoleDto;
import com.qzhou.domain.dto.RoleDto;
import com.qzhou.domain.vo.RoleInfoVo;
import com.qzhou.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult getRoleList(Integer pageNum,Integer pageSize,String roleName,String status){
        return roleService.getRoleList(pageNum,pageSize,roleName,status);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeRoleStatus(@RequestBody RoleDto roleDto){
        return roleService.changeRoleStatus(roleDto);
    }

    @PostMapping
    public ResponseResult addRole(@RequestBody AddRoleDto addRoleDto){
        return roleService.addRole(addRoleDto);
    }


    @GetMapping("/{id}")
    public ResponseResult getRoleInfo(@PathVariable("id") Long id){
        return roleService.getRoleInfo(id);
    }

    @PutMapping
    public ResponseResult updateRoleInfo(@RequestBody RoleInfoVo roleInfoVo){
        return roleService.updateRoleInfo(roleInfoVo);
    }


    @DeleteMapping("/{id}")
    public ResponseResult deleteRole(@PathVariable("id") Long id){
        return roleService.deleteRoleByID(id);
    }

    @GetMapping("/listAllRole")
    public ResponseResult getAllRoleListByStatusNormal(){
        return ResponseResult.okResult(roleService.list());
    }




}
