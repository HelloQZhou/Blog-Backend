package com.qzhou.domain.vo;

import com.qzhou.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListVo {
    private List<Long> roleIds;
    private List<Role> roles;
    private UserClassInfoVo user;
}
