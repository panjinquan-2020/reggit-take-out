package com.mytest.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mytest.reggie.common.R;
import com.mytest.reggie.entity.Employee;
import com.mytest.reggie.service.impl.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
/**
 * @author PJQ
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /**
     * 登录验证
     * @param employee
     * @param request
     * @return
     * */
    @PostMapping("/login")
    /*
    localhost:8080/employee/login post请求
    username,password 封装成employee json格式 需要将得到的用户信息存入网页中 传入HttpServletRequest 调用session
    前端接收数据需要employee 返回R<Employee>
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
    /**
     * 退出登录
     * @param request
     * @return
     * */
    @PostMapping("/logout")
    /*
    localhost:8080/employee/logout post请求
    需要将得到的用户信息在网页中删除 传入HttpServletRequest 传入HttpServletRequest 调用session
    前端返回界面只需要确认退出即可，返回R<String>
    */
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    /**
     * 新增员工
     * @param employee
     * @return
     * */
    @PostMapping
    /*
    localhost:8080/employee post请求
    idNumber,name,phone,sex,username 封装成employee json格式 需要获取当前的新增人，修改人信息 传入HttpSession 调用session
    前端返回只需要确认添加是否成功，返回R<String>
    */
    public R<String> save(HttpSession session,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        //设置初始密码123456，需要进行mds加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());;
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser((Long) session.getAttribute("employee"));
        employee.setUpdateUser((Long) session.getAttribute("employee"));
        employeeService.save(employee);
        return R.success("新增员工成功");
        //账号已存在，数据库表中有唯一约束，抛出异常：java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '***' for key 'employee.idx_username'
        //通过try catch解决
        //使用异常处理器进行全局捕获异常
    }
    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     * */
    @GetMapping("/page")
    /*
    localhost:8080/employee/page get请求
    page,pagesize name可能会输入（有搜索框）
    由于分页采用的是mybatis-plus的分页方式，所以返回的类型为R<Page>
    */
    public R<Page> page(int page,int pageSize, String name){
        log.info("page={}，pagesize={}，name={}",page,pageSize,name);
        //构建分页构造器
        Page pageInfo=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        if (name!=null){
            queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        }
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     * */
    @PutMapping
    /*
    localhost:8080/employee put请求
    id,status 封装成employee json格式 需要获取当前的新增人，修改人信息 传入HttpSession 调用session
    前端返回只需要确认添加是否成功，返回R<String>
    */
    public R<String> update(HttpSession session,@RequestBody Employee employee){
        log.info(employee.toString());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) session.getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
    /**
     * 根据id查询员工信息
     * @param id
     * @return
     * */
    @GetMapping("/{id}")
    /*
    localhost:8080/employee/id get请求
    此处的id是绑定的占位符 需要通过@PathVariable调用
    前端返回需要得到当前id返回的employee对象填入表格，返回R<Employee>
    */
    public R<Employee> getById(@PathVariable long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
        //由于update()已经是将add页面的内容进行新增了，并且add和update共用一个页面，所以不需要再次编写修改提交的操作
    }
}
