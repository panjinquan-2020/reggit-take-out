package com.mytest.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mytest.reggie.dto.DishDto;
import com.mytest.reggie.entity.Dish;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author PJQ
 */
@Service
public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据
    public void saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
    public void removeWithFlavor(List<Long> ids);
}
