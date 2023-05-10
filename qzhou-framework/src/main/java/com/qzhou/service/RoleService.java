package com.qzhou.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.AddRoleDto;
import com.qzhou.domain.dto.RoleDto;
import com.qzhou.domain.entity.Role;
import com.qzhou.domain.vo.RoleInfoVo;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author Qzhou
 * @since 2023-04-28 20:22:24
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取角色列表
     * 可以根据 角色名称 和状态进行 模糊查询
     * @param pageNum
     * @param pageSize
     * @param roleName
     * @param status
     * @return
     */
    ResponseResult getRoleList(Integer pageNum, Integer pageSize, String roleName, String status);

    /**
     * 修改角色状态
     * @param roleDto
     * @return
     */
    ResponseResult changeRoleStatus(RoleDto roleDto);

    /**
     * 新增角色
     * @param addRoleDto
     * @return
     */
    ResponseResult addRole(AddRoleDto addRoleDto);


    /**
     * 角色信息护
     * @param id
     * @return
     */
    ResponseResult getRoleInfo(Long id);

    /**
     * 更新角色信息
     * 更新表 role role_menu
     * @param roleInfoVo
     * @return
     */
    ResponseResult updateRoleInfo(RoleInfoVo roleInfoVo);

    /**
     * 根据roleId逻辑删除role表中数据
     * 还应该要删除 表role_menu 中对应的数据
     * @param id
     * @return
     */
    ResponseResult deleteRoleByID(Long id);

}

