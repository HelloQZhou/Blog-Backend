package com.qzhou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzhou.domain.entity.UserRole;
import com.qzhou.mapper.UserRoleMapper;
import com.qzhou.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户和角色关联表(UserRole)表服务实现类
 *
 * @author Qzhou
 * @since 2023-04-28 20:22:44
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}

