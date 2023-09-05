package com.mytest.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mytest.reggie.dto.SetmealDto;
import com.mytest.reggie.entity.DishFlavor;
import com.mytest.reggie.entity.Setmeal;
import com.mytest.reggie.entity.SetmealDish;
import com.mytest.reggie.mapper.SetmealMapper;
import com.mytest.reggie.service.SetmealDishService;
import com.mytest.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PJQ
 */
@Slf4j
@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal表执行新增操作
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        /*setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());*/
        for (SetmealDish setmealDish:setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        //保存套餐和菜品的关联信息，操作setmeal_dish执行新增操作
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        //如果可以删除，先删除菜品表中的数据
        this.removeByIds(ids);
        //删除关系表中的数据 dish_flavor
        LambdaQueryWrapper<SetmealDish> flavorQueryWrapper = new LambdaQueryWrapper();
        flavorQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(flavorQueryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        for (SetmealDish dish:dishes){
            dish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(dishes);

    }
}
