package com.mytest.reggie.dto;


import com.mytest.reggie.entity.Setmeal;
import com.mytest.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

/**
 * @author PJQ
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
