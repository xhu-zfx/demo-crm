package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contacts.Contacts;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.untils.DateUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(){

        return "settings/qx/user/login";
    }
    @RequestMapping("/settings/qx/user/login.do")
    public @ResponseBody Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpServletResponse response,HttpSession session){
        Map<String,Object> map=new HashMap<>();
        map.put("loginAct" , loginAct ) ;
        map.put("loginPwd" , loginPwd ) ;
        User user = userService.queryUserByActAndPwd(map);
//        根据查询结果生成响应对象
        ReturnObject returnObject = new ReturnObject();
        if(user==null){
//           用户名或密码错误 登陆失败
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或密码错误");
        }else {
//            检查是否超时
            String format = DateUtils.formateDateTime(new Date());
            if(format.compareTo(user.getExpireTime())>0){
//                账号过期 登陆失败
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号过期");
            }else if("0".equals(user.getLockState())){
//                状态被锁定 登陆失败
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("状态被锁定");
            }else if(!user.getAllowIps().contains(request.getRemoteAddr())){
//                IP地址不属于 登陆失败
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("IP地址不属于");
            }else{
//                登陆成功
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
//                将用户保存到session
                session.setAttribute(Contacts.SESSION_USER,user);
//                如果需要记住密码 , 则将密码存入cookie
                if ("true".equals(isRemPwd)){
                    Cookie cookieReAct = new Cookie("loginAct", user.getLoginAct());
                    cookieReAct.setMaxAge(10*24*60*60);
                    response.addCookie(cookieReAct);
                    Cookie cookieRePwd = new Cookie("loginPwd", user.getLoginPwd());
                    cookieRePwd.setMaxAge(10*24*60*60);
                    response.addCookie(cookieRePwd);
                }else {
                    Cookie cookieReAct = new Cookie("loginAct", "1");
                    cookieReAct.setMaxAge(0);
                    response.addCookie(cookieReAct);
                    Cookie cookieRePwd = new Cookie("loginPwd", "1");
                    cookieRePwd.setMaxAge(0);
                    response.addCookie(cookieRePwd);
                }
            }
        }
        return returnObject;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response,HttpSession session){
        Cookie cookieDelAct = new Cookie("loginAct", "1");
        cookieDelAct.setMaxAge(0);
        response.addCookie(cookieDelAct);
        Cookie cookieDelPwd = new Cookie("loginPwd", "1");
        cookieDelPwd.setMaxAge(0);
        response.addCookie(cookieDelPwd);
        session.invalidate();
        return "redirect:/";
    }
}
