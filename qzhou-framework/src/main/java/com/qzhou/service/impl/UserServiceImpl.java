package com.qzhou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzhou.constants.SystemConstants;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.UserDto;
import com.qzhou.domain.entity.Role;
import com.qzhou.domain.entity.UserRole;
import com.qzhou.domain.vo.*;
import com.qzhou.enums.AppHttpCodeEnum;
import com.qzhou.exception.SystemException;
import com.qzhou.mapper.UserMapper;
import com.qzhou.service.RoleService;
import com.qzhou.service.UserRoleService;
import com.qzhou.service.UserService;
import com.qzhou.utils.BeanCopyUtils;
import com.qzhou.utils.DateUtils;
import com.qzhou.utils.SecurityUtils;
import entity.User;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author Qzhou
 * @since 2023-04-23 11:49:59
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, entity.User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //根据id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        //返回给客户端
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        // 使用 updateById 会有风险 -> 若伪造发送put请求,其中有如修改密码的话,则密码也会被修改
        //此时可以在Mapper下写方法updatePartUserInfo 或者使用MP中的默认方法，需要传入修改的列
        LambdaUpdateWrapper<User> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,user.getId())
                .set(User::getAvatar,user.getAvatar())
                .set(User::getNickName,user.getNickName())
                .set(User::getSex,user.getSex());
        update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult userRegister(User user) {
        // TODO 对用户输入数据做非空判断 此处可以使用参数校验框架 Validation校验参数
        //return (str != null && str.length() > 0 && containsText(str));
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }

        //对用户输入数据做存在性判断
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        if(emailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        //对密码加密
        String encodePassWord= passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassWord);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
    }
    @Override
    public ResponseResult getUserList(Integer pageNum, Integer pageSize, String userName, String phonenumber, String status) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(Strings.hasText(userName),User::getUserName,userName);
        queryWrapper.like(Strings.hasText(phonenumber),User::getPhonenumber,phonenumber);
        queryWrapper.eq(Strings.hasText(status),User::getStatus,status);

        Page<User> page = new Page<>(pageNum, pageSize);
        page(page,queryWrapper);

        List<UserVo> userVos = BeanCopyUtils.copyBeanList(page.getRecords(), UserVo.class);

        return ResponseResult.okResult(new PageVo(userVos,page.getTotal()));
    }

    @Override
    public ResponseResult addUser(UserInsertVo userInsertVo) {
        //  在user表中加入对应数据
        // 此时需要密码加密存储
        User user = BeanCopyUtils.copyBean(userInsertVo, User.class);

        //需要对 userInsertVo 中用户名、手机号、邮箱判断
        if(!StringUtils.hasText(userInsertVo.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }

        List<User> list = list();
        for( User users:list ){
            if(userInsertVo.getEmail().equals(users.getEmail())||userInsertVo.getPhonenumber().equals(users.getPhonenumber())){
                throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
            }
        }

        String encodePassword = passwordEncoder.encode(userInsertVo.getPassword());
        user.setPassword(encodePassword);
        user.setCreateBy(SecurityUtils.getUserId());
        user.setCreateTime(DateUtils.getCurrentTime());

        save(user);

        // 在user_role 表中添加对应数据
        List<UserRole> userRolesList=new ArrayList<>();
        for(Long RoleId:userInsertVo.getRoleIds()){
            userRolesList.add(new UserRole(user.getId(),RoleId));
        }

        userRoleService.saveBatch(userRolesList);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteUserById(Long id) {
        //user 表中逻辑删除该用户
        LambdaUpdateWrapper<User> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,id).set(User::getDelFlag, SystemConstants.STATUS_ABNORMAL);
        update(updateWrapper);

        //user_role 表中删除对应用户信息
        LambdaQueryWrapper<UserRole> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,id);
        userRoleService.remove(queryWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserById(Long id) {
        //roleIds ->根据userId查询其对应的角色id集合
        LambdaQueryWrapper<UserRole> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,id);
//        List<Long> roleIds=userRoleService.list(queryWrapper).stream().map(roleId->{
//            return roleId.getRoleId();
//        }).collect(Collectors.toList());
        List<Long> roleIds=userRoleService.list(queryWrapper)
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        //roles -> 得到role集合
        List<Role> roles = roleService.list();

        //userClassInfo
        User user=userService.getById(id);

        UserClassInfoVo userClassInfoVo = BeanCopyUtils.copyBean(user, UserClassInfoVo.class);

        return ResponseResult.okResult(new UserListVo(roleIds,roles,userClassInfoVo));
    }

    @Override
    public ResponseResult updateUserInfoFromAdmin(UserInsertVo userInsertVo) {
        //修改 user表
        LambdaUpdateWrapper<User> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,userInsertVo.getId());
        User user = BeanCopyUtils.copyBean(userInsertVo, User.class);
        user.setUpdateTime(DateUtils.getCurrentTime());
        user.setUpdateBy(SecurityUtils.getUserId());
        //此时user 中为null 的字段会被更新进入数据库吗 ->不会
        update(user,updateWrapper);

        //修改 user_role 表
        //根据id删除现有的数据
        LambdaQueryWrapper<UserRole> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,userInsertVo.getId());
        userRoleService.remove(queryWrapper);

        //添加新的数据
        List<UserRole> userRoleList=new ArrayList<>();
        for (Long roleId:userInsertVo.getRoleIds()){
            userRoleList.add(new UserRole(userInsertVo.getId(),roleId));
        }
        userRoleService.saveBatch(userRoleList);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult changeUserStatus(UserDto userDto) {
        LambdaUpdateWrapper<User> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,userDto.getUserId()).set(User::getStatus,userDto.getStatus());
        update(updateWrapper);
        return ResponseResult.okResult();
    }

    private Boolean userNameExist(String name){
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,name);

        return count(queryWrapper)>0;
    }
    private Boolean nickNameExist(String nickName){
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName,nickName);

        return count(queryWrapper)>0;
    }
    private Boolean emailExist(String email){
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,email);
        return count(queryWrapper)>0;
    }
}

