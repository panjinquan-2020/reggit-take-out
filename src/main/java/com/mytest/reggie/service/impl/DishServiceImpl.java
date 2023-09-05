package com.mytest.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mytest.reggie.common.CustomException;
import com.mytest.reggie.dto.DishDto;
import com.mytest.reggie.entity.Dish;
import com.mytest.reggie.entity.DishFlavor;
import com.mytest.reggie.entity.Setmeal;
import com.mytest.reggie.mapper.DishMapper;
import com.mytest.reggie.service.DishFlavorService;
import com.mytest.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PJQ
 */
@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到对应的菜品表内
        this.save(dishDto);
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
/*        flavors.stream().map((item->{
            item.setDishId(dishId);
            return item;
        })).collect(Collectors.toList());*/
        for (DishFlavor flavor:flavors) {
            flavor.setDishId(dishId);
        }
        //保存菜品口味数据到菜品口味表内
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查找到当前dishDto对象
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 修改菜品，同时修改对应的口味数据
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        /*        flavors.stream().map((item->{
            item.setDishId(dishId);
            return item;
        })).collect(Collectors.toList());*/
        for (DishFlavor flavor:flavors){
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void removeWithFlavor(List<Long> ids) {
        //查询菜品状态，确定是否可用删除
        /*LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        int count = this.count(queryWrapper);
        if (count>0){
            throw new CustomException("菜品正在售卖中，不能删除");
        }*/
        //如果可以删除，先删除菜品表中的数据
        this.removeByIds(ids);
        //删除关系表中的数据 dish_flavor
        LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper();
        flavorQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(flavorQueryWrapper);
    }
}
