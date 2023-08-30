package com.mytest.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mytest.reggie.entity.Dish;
import com.mytest.reggie.mapper.DishMapper;
import com.mytest.reggie.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @author PJQ
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
