package com.mytest.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mytest.reggie.common.R;
import com.mytest.reggie.entity.Category;
import com.mytest.reggie.entity.Employee;
import com.mytest.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author PJQ
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增分类
     * @param category
     * @return
     * */
    @PostMapping
    /*
    localhost:8080/category?name=***&type=***&sort=*** post请求
    name,type,sort 封装成category json格式
    前端返回只需要确认添加是否成功，返回R<String>
    */
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }
    /**
     * 套餐信息分页查询
     * @param page
     * @param pageSize
     * @return
     * */
    @GetMapping("/page")
    /*
    localhost:8080/category/page?page=***&pagesize=*** get请求
    page,pagesize
    由于分页采用的是mybatis-plus的分页方式，所以返回的类型为R<Page>
    */
    public R<Page> page(int page, int pageSize){
        log.info("page={}，pagesize={}，name={}",page,pageSize);
        //构建分页构造器
        Page pageInfo=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.orderByDesc(Category::getUpdateTime);
        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    /*
    localhost:8080/category?ids=*** delete请求
    ids
    前端返回只需要确认删除是否成功，返回R<String>
    */
    public R<String> delete(Long ids){
        log.info("删除分类，id为：{}",ids);
//        categoryService.removeById(ids);
        //由于有的分类可能已经被某套餐或菜品占用，所以需要对删除进行优化
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    /*
    localhost:8080/category?id=***&name=***&sort=*** put请求
    id,name,sort 封装成category json格式
    前端返回只需要确认修改是否成功，返回R<String>
    */
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{]",category.getId());
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }
}
