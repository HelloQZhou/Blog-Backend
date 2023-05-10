package com.qzhou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzhou.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 角色和菜单关联表(RoleMenu)表数据库访问层
 *
 * @author Qzhou
 * @since 2023-04-28 20:18:53
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    /**
     * 根据RoleId查询所对应的MenuId集合
     * @param id
     * @return
     */
    @Select("\tSELECT menu_id\n" +
            "\t\tfrom sys_role_menu\n" +
            "\t\tWHERE role_id=#{id}")
    List<Long> selectMenuIdByRoleId(Long id);
}

