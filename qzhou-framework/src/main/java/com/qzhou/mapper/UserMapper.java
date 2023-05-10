package com.qzhou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户表(User)表数据库访问层
 *
 * @author Qzhou
 * @since 2023-04-18 20:41:18
 */

@Mapper
public interface UserMapper extends BaseMapper<entity.User> {
//    @Update("update #{}")
//     String updatePartUserInfo(entity.User user);
}

