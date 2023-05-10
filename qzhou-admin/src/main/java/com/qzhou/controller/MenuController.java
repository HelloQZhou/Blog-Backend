package com.qzhou.controller;

import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.entity.Menu;
import com.qzhou.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult getMenuList(String status,String menuName){
        return menuService.getMenuList(status,menuName);
    }

    @PostMapping
    public ResponseResult addMenu(@RequestBody Menu menu){
        return menuService.addMenu(menu);
    }

    @GetMapping("/{id}")
    private ResponseResult getMenuById(@PathVariable("id") Long id) {
        return menuService.getMenuById(id);
    }

    @PutMapping
    public ResponseResult updateMenu(@RequestBody Menu menu){
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("{menuId}")
    public ResponseResult deleteMenuById(@PathVariable("menuId") Long menuId){
        return menuService.deleteMenuById(menuId);
    }

    @GetMapping("/treeselect")
    public ResponseResult getMenuToTree(){
        return menuService.getMenuToTree();
    }

    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult getMenuToTreeById(@PathVariable("id") Long id){
        return menuService.getMenuToTreeById(id);
    }
}
