package com.mytest.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mytest.reggie.common.R;
import com.mytest.reggie.entity.Employee;
import com.mytest.reggie.service.impl.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author PJQ
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    /*
    localhost:8080/employee/login post请求
    username,password 封装成employee json格式 需要将得到的用户信息存入网页中 传入HttpServletRequest 调用session
    */
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        //将页面提交的密码password进行md5加密处理
        String password=employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        //根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //如果没有查询到则返回登录失败结果
        if (emp==null){
            return R.error("登录失败");
        }
        //密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        //登录成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(employee);
    }
    @PostMapping("/logout")
    /*
    localhost:8080/employee/logout post请求
    需要将得到的用户信息在网页中删除 传入HttpServletRequest 传入HttpServletRequest 调用session
    */
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
