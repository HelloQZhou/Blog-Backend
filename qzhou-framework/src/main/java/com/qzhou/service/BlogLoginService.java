package com.qzhou.service;

import com.qzhou.domain.ResponseResult;

public interface BlogLoginService {
    ResponseResult login(entity.User user);

    ResponseResult logout();
}
