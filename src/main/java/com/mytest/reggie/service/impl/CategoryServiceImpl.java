package com.mytest.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mytest.reggie.common.CustomException;
import com.mytest.reggie.entity.Category;
import com.mytest.reggie.entity.Dish;
import com.mytest.reggie.entity.Setmeal;
import com.mytest.reggie.mapper.CategoryMapper;
import com.mytest.reggie.service.CategoryService;
import com.mytest.reggie.service.DishService;
import com.mytest.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author PJQ
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (count>0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        count = setmealService.count(setmealLambdaQueryWrapper);
        if (count>0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }
}
