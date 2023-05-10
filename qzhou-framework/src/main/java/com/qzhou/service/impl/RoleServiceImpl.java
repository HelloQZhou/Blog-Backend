package com.qzhou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzhou.constants.SystemConstants;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.AddRoleDto;
import com.qzhou.domain.dto.RoleDto;
import com.qzhou.domain.entity.Role;
import com.qzhou.domain.entity.RoleMenu;
import com.qzhou.domain.vo.PageVo;
import com.qzhou.domain.vo.RoleInfoVo;
import com.qzhou.domain.vo.RoleVo;
import com.qzhou.mapper.RoleMapper;
import com.qzhou.mapper.RoleMenuMapper;
import com.qzhou.service.RoleMenuService;
import com.qzhou.service.RoleService;
import com.qzhou.utils.BeanCopyUtils;
import com.qzhou.utils.DateUtils;
import com.qzhou.utils.SecurityUtils;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author Qzhou
 * @since 2023-04-28 20:22:24
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public ResponseResult getRoleList(Integer pageNum, Integer pageSize, String roleName, String status) {

        LambdaQueryWrapper<Role> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(Strings.hasText(roleName),Role::getRoleName,roleName);
        queryWrapper.eq(Strings.hasText(status),Role::getStatus,status);

        Page<Role> rolePage = new Page<>(pageNum, pageSize);
        page(rolePage,queryWrapper);

        List<RoleVo> roleVos = BeanCopyUtils.copyBeanList(rolePage.getRecords(), RoleVo.class);
        return ResponseResult.okResult(new PageVo(roleVos,rolePage.getTotal()));
    }

    @Override
    public ResponseResult changeRoleStatus(RoleDto roleDto) {
        LambdaUpdateWrapper<Role> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Role::getId,roleDto.getRoleId()).set(Role::getStatus,roleDto.getStatus());
        update(updateWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult addRole(AddRoleDto addRoleDto) {
        Role role = BeanCopyUtils.copyBean(addRoleDto, Role.class);
        Long userId = SecurityUtils.getUserId();
        Date currentTime = DateUtils.getCurrentTime();
        role.setCreateBy(userId);
        role.setCreateTime(currentTime);
        role.setUpdateBy(userId);
        role.setUpdateTime(currentTime);
        //添加数据 在 role 表
        save(role);

        //添加数据 在 role_menu 表
        List<RoleMenu> roleMenuList=new ArrayList<>();
        for (Long menuId:addRoleDto.getMenuIds())
            roleMenuList.add(new RoleMenu(role.getId(),menuId));

        roleMenuService.saveBatch(roleMenuList);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getRoleInfo(Long id) {
        Role role = getById(id);
        RoleInfoVo roleInfoVo = BeanCopyUtils.copyBean(role, RoleInfoVo.class);
        return ResponseResult.okResult(roleInfoVo);
    }

    @Override
    public ResponseResult updateRoleInfo(RoleInfoVo roleInfoVo) {
        //更新 role 表
        Role role = BeanCopyUtils.copyBean(roleInfoVo, Role.class);
        role.setUpdateTime(DateUtils.getCurrentTime());
        role.setUpdateBy(SecurityUtils.getUserId());

        LambdaUpdateWrapper<Role> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Role::getId,roleInfoVo.getId());
        update(role,updateWrapper);

        //更新 role_menu 表 (如果此时有数据的话)
        //根据roleId删除对应数据库中存在的数据，然后在加入新的数据
        if(roleInfoVo.getMenuIds()!=null) {
            Long Role_id = roleInfoVo.getId();
            LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RoleMenu::getRoleId, Role_id);
            roleMenuService.remove(queryWrapper);

            List<RoleMenu> roleMenuList = new ArrayList<>();
            for (Long menuId : roleInfoVo.getMenuIds()) {
                roleMenuList.add(new RoleMenu(Role_id, menuId));
            }

            roleMenuService.saveBatch(roleMenuList);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteRoleByID(Long id) {
        //修改role 表中del_flag=1
        LambdaUpdateWrapper<Role> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Role::getId,id).set(Role::getDelFlag, SystemConstants.STATUS_ABNORMAL);
        update(updateWrapper);

        //删除 表role_menu 中对应数据
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId, id);
        roleMenuService.remove(queryWrapper);

        return ResponseResult.okResult();
    }

}

