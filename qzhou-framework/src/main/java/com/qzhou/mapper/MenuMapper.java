package com.qzhou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzhou.domain.entity.Menu;
import com.qzhou.domain.vo.MenuVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author Qzhou
 * @since 2023-04-28 20:21:49
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 不是管理员 ->根据 userId 查询 permissions
     * @param userId
     * @return
     */
    @Select("SELECT\n" +
            "\tDISTINCT m.perms \n" +
            "FROM\n" +
            "\tsys_user_role ur\n" +
            "\tLEFT JOIN sys_role_menu rm ON ur.role_id=rm.role_id\n" +
            "\tLEFT JOIN sys_menu m ON m.id = rm.menu_id\n" +
            "WHERE \n" +
            "\tur.user_id=#{userId} AND\n" +
            "\tm.menu_type IN ('C','F') AND\n" +
            "\tm.`status`=0 AND\n" +
            "\tm.del_flag=0")
    List<String> selectPermissionsByUserId(Long userId);

    /**
     * 是管理员 ->查询 permissions
     * @return
     */
    @Select("SELECT\n" +
            "\tDISTINCT perms \n" +
            "FROM\n" +
            "\tsys_menu\n" +
            "WHERE\n" +
            "\tmenu_type IN ('C','F') AND\n" +
            "\t`status` =0 AND\n" +
            "\tdel_flag=0")
    List<String> selectPermissionsIfIsAdmin();

    /**
     * 根据UserId查询menu，以tree的形式
     * @param userId
     * @return
     */
    @Select(" SELECT\n" +
            "          DISTINCT m.id, m.parent_id, m.menu_name, m.path, m.component, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame,  m.menu_type, m.icon, m.order_num, m.create_time\n" +
            "        FROM\n" +
            "            `sys_user_role` ur\n" +
            "            LEFT JOIN `sys_role_menu` rm ON ur.`role_id` = rm.`role_id`\n" +
            "            LEFT JOIN `sys_menu` m ON m.`id` = rm.`menu_id`\n" +
            "        WHERE\n" +
            "            ur.`user_id` = 5 AND\n" +
            "            m.`menu_type` IN ('C','M') AND\n" +
            "            m.`status` = 0 AND\n" +
            "            m.`del_flag` = 0\n" +
            "        ORDER BY\n" +
            "            m.parent_id,m.order_num")
    List<Menu> selectRouterMenuByUserId(Long userId);

    /**
     * 查询所有RouterMenu列表
     * @return
     */
    @Select(" SELECT\n" +
            "          DISTINCT m.id, m.parent_id, m.menu_name, m.path, m.component, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame,  m.menu_type, m.icon, m.order_num, m.create_time\n" +
            "        FROM\n" +
            "            `sys_menu` m\n" +
            "        WHERE\n" +
            "            m.`menu_type` IN ('C','M') AND\n" +
            "            m.`status` = 0 AND\n" +
            "            m.`del_flag` = 0\n" +
            "        ORDER BY\n" +
            "            m.parent_id,m.order_num")
    List<Menu> selectAllRouterMenu();

    /**
     * 查询根菜单
     * @return
     */
    @Select(" SELECT\n" +
            "          DISTINCT m.id, m.parent_id, m.menu_name, m.path, m.component, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame,  m.menu_type, m.icon, m.order_num, m.create_time\n" +
            "        FROM\n" +
            "            `sys_menu` m\n" +
            "        WHERE\n" +
            "            m.`menu_type` IN ('C','M') AND\n" +
            "            m.`status` = 0 AND\n" +
            "            m.`del_flag` = 0 AND\n" +
            "\t\t\t\t\t\tm.parent_id=0\n" +
            "        ORDER BY\n" +
            "            m.parent_id,m.order_num")
    List<MenuVo> selectMenuRootTree();


    /**
     * 根据MenuId查询子菜单
     * @param menuId
     * @return
     */
    @Select("\tSELECT *\n" +
            "\tFROM sys_menu\n" +
            "\tWHERE parent_id=\n" +
            "(\tSELECT id\n" +
            "\tfrom sys_menu\n" +
            "\tWHERE id=#{menuId}\t)")
    List<Menu> selectChildrenMenuById(Long menuId);

    /**
     * 查询第一层 所有的路由菜单
     * @return
     */
    @Select(" SELECT\n" +
            "          DISTINCT m.id, m.parent_id, m.menu_name, m.path, m.component, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame,  m.menu_type, m.icon, m.order_num, m.create_time\n" +
            "        FROM\n" +
            "            `sys_menu` m\n" +
            "        WHERE\n" +
            "            m.`menu_type` IN ('C','M') AND\n" +
            "\t\t\t\t\t\tm.parent_id=0 AND\n" +
            "            m.`status` = 0 AND\n" +
            "            m.`del_flag` = 0\n" +
            "        ORDER BY\n" +
            "            m.parent_id,m.order_num")
    List<Menu> selectFirstFloorAllRouterMenu();

    /**
     * 查询出除了第一层以外的所有菜单
     * @return
     */
    @Select(" SELECT\n" +
            "          DISTINCT m.id, m.parent_id, m.menu_name, m.path, m.component, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame,  m.menu_type, m.icon, m.order_num, m.create_time\n" +
            "        FROM\n" +
            "            `sys_menu` m\n" +
            "        WHERE\n" +
            "\t\t\t\t\t\tm.parent_id !=0 AND\n" +
            "            m.`status` = 0 AND\n" +
            "            m.`del_flag` = 0\n" +
            "        ORDER BY\n" +
            "            m.parent_id,m.order_num")
    List<Menu> selectOtherFloorAllMenu();

    /**
     * 根据id 查询第一层 所有的路由菜单
     * @param userId
     * @return
     */
    @Select("\t\t\t\t\tSELECT\n" +
            "          DISTINCT m.id, m.parent_id, m.menu_name, m.path, m.component, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame,  m.menu_type, m.icon, m.order_num, m.create_time\n" +
            "        FROM\n" +
            "            `sys_user_role` ur\n" +
            "            LEFT JOIN `sys_role_menu` rm ON ur.`role_id` = rm.`role_id`\n" +
            "            LEFT JOIN `sys_menu` m ON m.`id` = rm.`menu_id`\n" +
            "        WHERE\n" +
            "            ur.`user_id` = #{userId} AND\n" +
            "           m.parent_id=0 AND\n" +
            "            m.`status` = 0 AND\n" +
            "            m.`del_flag` = 0\n" +
            "        ORDER BY\n" +
            "            m.parent_id,m.order_num")
    List<Menu> selectFirstFloorAllRouterMenuByUserId(Long userId);

    /**
     * 根据id 查询出除了第一层以外的所有菜单
     * @param userId
     * @return
     */
    @Select("\t\tSELECT\n" +
            "          DISTINCT m.id, m.parent_id, m.menu_name, m.path, m.component, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame,  m.menu_type, m.icon, m.order_num, m.create_time\n" +
            "        FROM\n" +
            "            `sys_user_role` ur\n" +
            "            LEFT JOIN `sys_role_menu` rm ON ur.`role_id` = rm.`role_id`\n" +
            "            LEFT JOIN `sys_menu` m ON m.`id` = rm.`menu_id`\n" +
            "        WHERE\n" +
            "            ur.`user_id` = #{userId} AND\n" +
            "           m.parent_id !=0 AND\n" +
            "            m.`status` = 0 AND\n" +
            "            m.`del_flag` = 0\n" +
            "        ORDER BY\n" +
            "            m.parent_id,m.order_num")
    List<Menu> selectOtherFloorAllMenuByUserId(Long userId);
}

