package com.mytest.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mytest.reggie.entity.Setmeal;
import com.mytest.reggie.mapper.SetmealMapper;
import com.mytest.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

/**
 * @author PJQ
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
