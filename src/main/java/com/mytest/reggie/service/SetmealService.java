package com.mytest.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mytest.reggie.dto.SetmealDto;
import com.mytest.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author PJQ
 */
@Service
public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> setmealList);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
