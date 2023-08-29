package com.mytest.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mytest.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author PJQ
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
