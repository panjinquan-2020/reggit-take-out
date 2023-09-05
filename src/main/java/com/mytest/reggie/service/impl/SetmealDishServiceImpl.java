package com.mytest.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mytest.reggie.entity.SetmealDish;
import com.mytest.reggie.mapper.SetmealDishMapper;
import com.mytest.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
