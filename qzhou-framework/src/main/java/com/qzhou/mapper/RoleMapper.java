package com.qzhou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzhou.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author Qzhou
 * @since 2023-04-28 20:22:24
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("SELECT role_key\n" +
            "\tFROM sys_role r LEFT JOIN sys_user_role ur ON ur.role_id=r.id\n" +
            "\tWHERE ur.user_id=#{userId} AND\n" +
            "  r.`status` = 0 AND\n" +
            "  r.`del_flag` = 0")
    List<String> selectRoleIdByUserId(Long userId);
}

