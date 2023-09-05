package com.mytest.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mytest.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author PJQ
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
