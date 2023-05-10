package com.qzhou.service.impl;

import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.entity.LoginUser;
import com.qzhou.domain.entity.Menu;
import com.qzhou.domain.vo.*;
import com.qzhou.mapper.MenuMapper;
import com.qzhou.mapper.RoleMapper;
import com.qzhou.service.AdminLoginService;
import com.qzhou.utils.BeanCopyUtils;
import com.qzhou.utils.JwtUtil;
import com.qzhou.utils.RedisCache;
import com.qzhou.utils.SecurityUtils;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AdminLoginServiceImpl implements AdminLoginService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid 生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject("adminlogin:"+userId,loginUser);

        //把token封装 返回
        //此时可以是封装一个类返回，也可以是封装一个map集合返回
        AdminUserLoginVo vo = new AdminUserLoginVo(jwt);
        return ResponseResult.okResult(vo);
    }
//sql username -> user_id -> role_id -> menu_id -> perms
//data:
// permissions List<perms>
// roles List<role_key>
// user ...
    @Override
    public ResponseResult getInfo() {
        //1、获取当前登录的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //2、根据用户id查询权限信息 user_id -> role_id
        Long userId = loginUser.getUser().getId();
        //2.1、若用户id为1，则是管理员，得到所有权限(menu_type=C 、 F)
        List<String> permissions=new ArrayList<>();
        if(SecurityUtils.isAdmin()){
//            LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper<>();
//            //queryWrapper.in(Menu::getMenuType,"C","F");
//            queryWrapper.in(Menu::getMenuType, SystemConstants.MENU,SystemConstants.BUTTON);
//            queryWrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
//            List<Menu> menus = menuMapper.selectList(queryWrapper);
//            permissions=menus.stream()
//                    .map(Menu::getPerms)
//                    .collect(Collectors.toList());
           permissions=menuMapper.selectPermissionsIfIsAdmin();
        }
        //2.2、不是管理员，则得到所具有的权限 （在menu表中）
        else{
            permissions=menuMapper.selectPermissionsByUserId(userId);
        }
        //3、根据user_id 查询 role_key （在role表中）
        List<String> roles =roleMapper.selectRoleIdByUserId(userId);

        //4、封装数据返回
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        return ResponseResult.okResult(new AdminUserInfoVo(permissions,roles,userInfoVo));
    }

    @Override
    public ResponseResult getRouters() {
        //1、获取当前登录的用户
        Long userId = SecurityUtils.getUserId();

        List<Menu> firstMenu=new ArrayList<>();
        List<Menu> otherMenu =new ArrayList<>();

        //2.1、若用户id为1，则是管理员
        //if(userId==1L){  此时有多处判断用户是否为管理员，可以封装为工具类判断
        if(SecurityUtils.isAdmin()){
            //firstMenu=menuMapper.selectAllRouterMenu();
            //menuMapper.selectMenuRootTree();

            firstMenu=menuMapper.selectFirstFloorAllRouterMenu();
            otherMenu=menuMapper.selectOtherFloorAllMenu();
        }
        //2.2、不是管理员，根据id查询所具有的 父/第一层 列表
        else{
            //firstMenu =menuMapper.selectRouterMenuBuUserId(userId);
            //menuMapper.selectMenuRootTree();

            firstMenu=menuMapper.selectFirstFloorAllRouterMenuByUserId(userId);
            otherMenu=menuMapper.selectOtherFloorAllMenuByUserId(userId);
        }


        List<MenuVo> menuFirstFloorVos = BeanCopyUtils.copyBeanList(firstMenu, MenuVo.class);
        List<MenuVo> menuOtherFloorVos = BeanCopyUtils.copyBeanList(otherMenu, MenuVo.class);

        for(MenuVo menuFirstFloorVo:menuFirstFloorVos){
            ArrayList<MenuVo> menuVos = new ArrayList<>();
            // 找出目录设置进菜单
            for(MenuVo menuOtherFloorVo:menuOtherFloorVos) {
                if (menuFirstFloorVo.getId().equals(menuOtherFloorVo.getParentId()))
                {
                    menuVos.add(menuOtherFloorVo);
                }
            }
            // 找出按钮设置进目录
            for (MenuVo menuVo:menuVos){
                ArrayList<MenuVo> menuVos1 = new ArrayList<>();
                for (MenuVo menuOtherFloorVo:menuOtherFloorVos){
                    if(menuVo.getId().equals(menuOtherFloorVo.getParentId()))
                        menuVos1.add(menuOtherFloorVo);
                }
                menuVo.setChildren(menuVos1);
            }
            //
            menuFirstFloorVo.setChildren(menuVos);
        }

       return ResponseResult.okResult( new RouterMenuVo(menuFirstFloorVos));

    }

    @Override
    public ResponseResult logout() {
        //获取用户 id
        Long userId = SecurityUtils.getUserId();
        //在redis在删除对应记录
        redisCache.deleteObject("adminlogin:"+userId);
        return ResponseResult.okResult();
    }

}

