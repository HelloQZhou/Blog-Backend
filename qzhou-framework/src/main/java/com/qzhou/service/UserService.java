package com.qzhou.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.UserDto;
import com.qzhou.domain.vo.UserInsertVo;


/**
 * 用户表(User)表服务接口
 *
 * @author Qzhou
 * @since 2023-04-23 11:49:59
 */
public interface UserService extends IService<entity.User> {
    /**
     * 查询个人信息
     * @return
     */
    ResponseResult userInfo();

    /**
     * 更新修改后的部分个人信息资料
     * @param user
     * @return
     */
    ResponseResult updateUserInfo(entity.User user);

    /**
     * 用户注册
     * @param user
     * @return
     */
    ResponseResult userRegister(entity.User user);

    /**
     * 分页查询用户列表
     * @param pageNum
     * @param pageSize
     * @param userName
     * @param phonenumber
     * @param status
     * @return
     */
    ResponseResult getUserList(Integer pageNum, Integer pageSize, String userName, String phonenumber, String status);

    /**
     * 新增用户
     * @param userInsertVo
     * @return
     */
    ResponseResult addUser(UserInsertVo userInsertVo);

    /**
     * 根据用户id 逻辑删除删除用户
     * @param id
     * @return
     */
    ResponseResult deleteUserById(Long id);

    /**
     * 根据用户id 得到前端所需的 user信息 roles信息 roleIds
     * @param id
     * @return
     */
    ResponseResult getUserById(Long id);

    /**
     * 修改用户信息
     * @param userInsertVo
     * @return
     */
    ResponseResult updateUserInfoFromAdmin(UserInsertVo userInsertVo);

    /**
     * 在用户管理中点击按钮修改状态
     * @return
     */
    ResponseResult changeUserStatus(UserDto userDto);
}

