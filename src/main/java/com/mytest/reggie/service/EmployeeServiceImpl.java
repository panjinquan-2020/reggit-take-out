package com.mytest.reggie.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mytest.reggie.entity.Employee;
import com.mytest.reggie.mapper.EmployeeMapper;
import com.mytest.reggie.service.impl.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author PJQ
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
}
