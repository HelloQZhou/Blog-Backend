package com.qzhou.service;

import com.qzhou.domain.ResponseResult;

public interface AdminLoginService {
    ResponseResult login(entity.User user);

    /**
     * 获取相应用户后台界面
     * @return
     */
    ResponseResult getInfo();

    /**
     * 返回用户所能访问的菜单数据
     * @return
     */
    ResponseResult getRouters();

    ResponseResult logout();
}
