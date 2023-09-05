package com.mytest.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mytest.reggie.common.R;
import com.mytest.reggie.common.SMSUtils;
import com.mytest.reggie.common.ValidateCodeUtils;
import com.mytest.reggie.entity.User;
import com.mytest.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author PJQ
 * 客户管理
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 客户登录获取验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    /*
    localhost:8080/user/sendMsg post请求
    phone 封装成user 需要将得到的用户信息在网页中存储 传入HttpServletRequest 传入HttpServletRequest 调用session
    前端返回界面只需要确认发送验证码即可，返回R<String>
    */
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机的四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //调用阿里云提供的短信服务
            //SMSUtils.sendMessage("瑞吉外卖登录验证码","瑞吉外卖模板",phone,code);
            //将生成的验证码保存到Session
            session.setAttribute("phone",code);
            return R.success("手机验证码发送成功");
        }
        return R.error("发送失败");
    }

    /**
     * 客户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    /*
    localhost:8080/user/login post请求
    phone,code 使用map 需要将得到的用户信息在网页中存储 传入HttpServletRequest 传入HttpServletRequest 调用session
    前端返回界面需要确认登录并存入数据，返回R<User>
    */
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //获取session中保存的验证码
        String attribute = (String) session.getAttribute("phone");
        //进行验证码比对
        if (code.equals(attribute)){
            //如果能够比对成功说名登录成功
            //判断当前手机号对应的用户是否为新用户
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
