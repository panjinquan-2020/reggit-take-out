package com.mytest.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mytest.reggie.common.R;
import com.mytest.reggie.dto.DishDto;
import com.mytest.reggie.dto.SetmealDto;
import com.mytest.reggie.entity.Category;
import com.mytest.reggie.entity.Dish;
import com.mytest.reggie.entity.Setmeal;
import com.mytest.reggie.service.CategoryService;
import com.mytest.reggie.service.SetmealDishService;
import com.mytest.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PJQ
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    /*
    localhost:8080/setmeal post请求
    值太多 不仅有setmeal_dish表中的数据还有category的数据，将二者整合成dishDto类
    前端返回只需要确认添加是否成功 返回R<String>
    */
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("获取套餐信息：{}"+setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增成功");
    }
    @GetMapping("/page")
    /*
    localhost:8080/setmeal/page?page=***&pagesize=***&name=*** get请求
    page,pagesize name可能会输入（有搜索框）
    由于分页采用的是mybatis-plus的分页方式，所以返回的类型为R<Page>
    */
    public R<Page> page(int page, int pageSize, String name){
        //构建分页构造器
        Page<Setmeal> pageinfo=new Page(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行分页查询
        setmealService.page(pageinfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageinfo,setmealDtoPage,"records");
        List<Setmeal> records=pageinfo.getRecords();
        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName=category.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 根据ids进行删除以及批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    /*
    localhost:8080/setmeal?ids=***,***,... delete请求
    ids 可以封装成一个List<Long>
    前端返回只需要确认删除是否成功，返回R<String>
    */
    public R<String> delete(String ids){
        String[] idList = ids.split(",");
        List<Long> setmealList=new ArrayList<>();
        for(String id:idList){
            setmealList.add(Long.valueOf(id));
        }
        setmealService.removeWithDish(setmealList);
        return R.success("删除套餐成功");
    }

    /**
     * 根据status对套餐状态进行修改
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    /*
    localhost:8080/setmeal/status?ids=***,***,... post请求
    status是绑定的占位符 ids 可以封装成一个List<Long>
    前端返回只需要确认修改状态是否成功，返回R<String>
    */
    public R<String> updateStatus(@PathVariable int status,String ids){
        String[] idList=ids.split(",");
        List<Setmeal> setmealList=new ArrayList<>();
        for (String id:idList){
            Setmeal setmeal=new Setmeal();
            setmeal.setId(Long.valueOf(id));
            setmeal.setStatus(status);
            setmealList.add(setmeal);
        }
        setmealService.updateBatchById(setmealList);
        /*if (status==0){
            return R.success("批量停售成功");
        }else if (status==1){
            return R.success("批量启售成功");
        }
        return null;*/
        return status==0?R.success("批量停售成功"):R.success("批量起售成功");
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    /*
    localhost:8080/setmeal/id get请求
    id是绑定的占位符
    前端返回需要获取相关信息，返回R<SetmealDto>
    */
    public R<SetmealDto> save(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 根据填入的数据对套餐进行修改
     * @param setmealDto
     * @return
     */
    @PutMapping
    /*
    localhost:8080/setmeal put请求
    值太多 整合成SetmealDto类
    前端返回只需要确认修改是否成功 返回R<String>
    */
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("套餐修改成功");
    }
}
