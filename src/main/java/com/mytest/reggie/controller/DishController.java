package com.mytest.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mytest.reggie.common.R;
import com.mytest.reggie.dto.DishDto;
import com.mytest.reggie.entity.Category;
import com.mytest.reggie.entity.Dish;
import com.mytest.reggie.service.CategoryService;
import com.mytest.reggie.service.DishFlavorService;
import com.mytest.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PJQ
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    /*
    localhost:8080/dish post请求
    值太多 不仅有dish表中的数据还有dish_flavor的数据，将二者整合成dishDto类
    前端返回只需要确认添加是否成功 返回R<String>
    */
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    /*
    localhost:8080/dish/page?page=***&pagesize=***&name=*** get请求
    page,pagesize name可能会输入（有搜索框）
    由于分页采用的是mybatis-plus的分页方式，所以返回的类型为R<Page>
    */
    public R<Page> page(int page,int pageSize,String name){
        //构建分页构造器
        Page<Dish> pageinfo=new Page(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageinfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageinfo,dishDtoPage,"records");
        List<Dish> records=pageinfo.getRecords();
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName=category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    /*
    localhost:8080/dish/id get请求
    此处的id是绑定的占位符 需要通过@PathVariable调用
    前端返回需要得到当前id返回的dishDto对象填入表格，返回R<Employee>
    */
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 根据填入信息修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    /*
    localhost:8080/dish put请求
    值太多 不仅有dish表中的数据还有dish_flavor的数据，将二者整合成dishDto类
    前端返回只需要确认修改是否成功 返回R<String>
    */
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据id和status进行批量停售/起售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    /*
    localhost:8080/dish/status?ids=***,***... post请求
    此处的status是绑定的占位符 需要通过@PathVariable调用 ids
    前端返回只需要确认批量起售/停售是否成功 返回R<String>
    */
    public R<String> updateStatus(@PathVariable int status,String ids){
        String[] idList=ids.split(",");
        List<Dish> dishList=new ArrayList<>();
        for (String id:idList){
            Dish dish=new Dish();
            dish.setId(Long.valueOf(id));
            dish.setStatus(status);
            dishList.add(dish);
        }
        dishService.updateBatchById(dishList);
        /*if (status==0){
            return R.success("批量停售成功");
        }else if (status==1){
            return R.success("批量启售成功");
        }
        return null;*/
        return status==0?R.success("批量停售成功"):R.success("批量起售成功");
    }

    /**
     * 根据id对菜品进行批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
     /*
    localhost:8080/dish?ids=***,***... delete请求
    ids
    前端返回只需要确认批量删除是否成功 返回R<String>
    */
    public R<String> removeAny(String ids){
        String[] idList=ids.split(",");
        List<Long> dishList=new ArrayList<>();
        for (String id:idList){
            dishList.add(Long.valueOf(id));
        }
        dishService.removeWithFlavor(dishList);
        return R.success("菜品删除成功");
    }

    /**
     * 根据dish查询菜品信息
     * @param dish
     * @return
     */
    @GetMapping("/list")
    /*
    localhost:8080/dish/list get请求
    categoryId 封装成dish
    前端返回需要获取到对应的菜品数据 返回R<List<Dish>>
    */
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加排序操作
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }
}
