package com.qzhou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzhou.constants.SystemConstants;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.entity.Menu;
import com.qzhou.domain.vo.AddMenuVo;
import com.qzhou.domain.vo.MenuListVo;
import com.qzhou.domain.vo.UpdateMenuVo;
import com.qzhou.enums.AppHttpCodeEnum;
import com.qzhou.mapper.MenuMapper;
import com.qzhou.mapper.RoleMenuMapper;
import com.qzhou.service.MenuService;
import com.qzhou.utils.BeanCopyUtils;
import com.qzhou.utils.DateUtils;
import com.qzhou.utils.SecurityUtils;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author Qzhou
 * @since 2023-04-28 20:21:49
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public ResponseResult getMenuList(String status, String menuName) {
        //Long userId = SecurityUtils.getUserId();

        LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.like(Strings.hasText(menuName),Menu::getMenuName,menuName);
        queryWrapper.eq(Strings.hasText(status),Menu::getStatus,status);

        List<Menu> menus = menuMapper.selectList(queryWrapper);

        List<MenuListVo> menuListVos = BeanCopyUtils.copyBeanList(menus, MenuListVo.class);

        return ResponseResult.okResult(menuListVos);
    }

    @Override
    public ResponseResult addMenu(Menu menu) {
        Long userId = SecurityUtils.getUserId();
        Date currentTime = DateUtils.getCurrentTime();
        menu.setCreateBy(userId);
        menu.setCreateTime(currentTime);
        menu.setUpdateBy(userId);
        menu.setUpdateTime(currentTime);
        menuMapper.insert(menu);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getMenuById(Long id) {
        Menu menu = menuMapper.selectById(id);
        MenuListVo menuListVo = BeanCopyUtils.copyBean(menu, MenuListVo.class);
        return ResponseResult.okResult(menuListVo);
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
        //如果把父菜单设置为当前菜单
        if(menu.getId().equals(menu.getParentId())){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,
                    "修改菜单 '"+menu.getMenuName()+"' 失败，上级菜单不能选择自己");
        }
        menu.setUpdateTime(DateUtils.getCurrentTime());
        menu.setUpdateBy(SecurityUtils.getUserId());
        menuMapper.updateById(menu);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenuById(Long menuId) {
        //若删除的菜单有子菜单，则删除失败并提示
        List<Menu> menuList=menuMapper.selectChildrenMenuById(menuId);
        Menu menu = menuMapper.selectById(menuId);
        if(menuList.size()!=0){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"菜单 '"+menu.getMenuName()+"' 存在子菜单不允许被删除");
        }

        LambdaUpdateWrapper<Menu> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Menu::getId,menuId).set(Menu::getDelFlag,SystemConstants.STATUS_ABNORMAL);
        update(updateWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getMenuToTree() {

        List<AddMenuVo> allMenuToTree = getAllMenuToTree();

        return ResponseResult.okResult(allMenuToTree);
    }

    @Override
    public ResponseResult getMenuToTreeById(Long id) {
        List<AddMenuVo> allMenuToTree = getAllMenuToTree();
        List<Long> checkedKeys=roleMenuMapper.selectMenuIdByRoleId(id);

        UpdateMenuVo updateMenuVo = new UpdateMenuVo(allMenuToTree, checkedKeys);
        return ResponseResult.okResult(updateMenuVo);
    }

    List<AddMenuVo> getAllMenuToTree(){
        //1、按返回格式找出 menu 表所有数据
        List<Menu> lists = list();
        List<AddMenuVo> addMenuVoList = lists.stream()
                .map(menu -> {
                    AddMenuVo addMenuVo = new AddMenuVo();
                    addMenuVo.setId(menu.getId());
                    addMenuVo.setLabel(menu.getMenuName());
                    addMenuVo.setParentId(menu.getParentId());
                    return addMenuVo;
                })
                .collect(Collectors.toList());

        //2、构建tree
        //找出父菜单下的子菜单
        //先找出第一层的菜单  然后去找他们的子菜单设置到children属性中
        List<AddMenuVo> parent =new ArrayList<>();
        List<AddMenuVo> children =new ArrayList<>();
        for(AddMenuVo addMenuVo:addMenuVoList){
            if(addMenuVo.getParentId()==SystemConstants.STATUS_PARENT_ID){
                parent.add(addMenuVo);
            }else
                children.add(addMenuVo);
        }

        for (AddMenuVo addMenuVoParent:parent){
            List<AddMenuVo> addChildrenList=new ArrayList<>();
            for (AddMenuVo addMenuVoChildren:children){
                if(addMenuVoParent.getId().equals(addMenuVoChildren.getParentId()))
                    addChildrenList.add(addMenuVoChildren);
            }
            addMenuVoParent.setChildren(addChildrenList);
        }
        return parent;
    }

}

