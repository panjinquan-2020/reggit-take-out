package com.mytest.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mytest.reggie.entity.User;
import com.mytest.reggie.mapper.UserMapper;
import com.mytest.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author PJQ
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
}
