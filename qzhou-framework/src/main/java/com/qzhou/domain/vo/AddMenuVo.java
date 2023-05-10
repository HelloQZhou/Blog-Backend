package com.qzhou.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddMenuVo {
    //菜单ID@TableId
    private Long id;

    //菜单名称

    private String label;

    //父菜单ID
    private Long parentId;

    private List<AddMenuVo> children;

}
