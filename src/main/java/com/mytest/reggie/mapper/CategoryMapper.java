package com.mytest.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mytest.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author PJQ
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
