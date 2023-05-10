package com.qzhou.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.entity.Menu;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author Qzhou
 * @since 2023-04-28 20:21:49
 */
public interface MenuService extends IService<Menu> {

    /**
     * 获取菜单列表，可以根据状态和菜单名查询
     * @return
     * @param status
     * @param menuName
     */
    ResponseResult getMenuList(String status, String menuName);

    /**
     * 新增菜单
     * @param menu
     * @return
     */
    ResponseResult addMenu(Menu menu);

    /**
     * 根据id查询菜单数据
     * @param id
     * @return
     */
    ResponseResult getMenuById(Long id);

    /**
     * 更新菜单
     * @param menu
     * @return
     */
    ResponseResult updateMenu(Menu menu);

    /**
     * 根据id逻辑删除菜单表中数据
     * @param menuId
     * @return
     */
    ResponseResult deleteMenuById(Long menuId);

    /**
     * 以树状形式查询菜单
     * @return
     */
    ResponseResult getMenuToTree();

    /**
     * 根据角色id以树状形式查询菜单
     * @param id 角色id
     * @return
     */
    ResponseResult getMenuToTreeById(Long id);
}

