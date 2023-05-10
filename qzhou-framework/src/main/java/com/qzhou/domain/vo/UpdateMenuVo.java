package com.qzhou.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMenuVo {
    private List<AddMenuVo> menus;
    private List<Long> checkedKeys;
}
