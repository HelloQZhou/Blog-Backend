package com.qzhou.domain.entity;import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

;
/**
 * 用户和角色关联表(UserRole)表实体类
 *
 * @author Qzhou
 * @since 2023-04-28 20:22:44
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user_role")
public class UserRole  {
    //用户ID@TableId
    private Long userId;
    //角色ID@TableId
    private Long roleId;


}

