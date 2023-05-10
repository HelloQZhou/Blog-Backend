package com.qzhou.domain.entity;import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

;
/**
 * 角色和菜单关联表(RoleMenu)表实体类
 *
 * @author Qzhou
 * @since 2023-04-28 20:18:53
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_role_menu")
public class RoleMenu  {
    //角色ID@TableId
    private Long roleId;
    //菜单ID@TableId
    private Long menuId;


}

