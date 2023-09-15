package com.mytest.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mytest.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}