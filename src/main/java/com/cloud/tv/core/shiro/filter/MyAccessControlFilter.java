package com.cloud.tv.core.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.shiro.salt.MyByteSource;
import com.cloud.tv.core.shiro.tools.ApplicationContextUtils;
import com.cloud.tv.entity.User;
import com.cloud.tv.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MyAccessControlFilter extends AccessControlFilter {


    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //判断用户是通过记住我功能自动登录,此时session失效
        Subject subject = SecurityUtils.getSubject();
        if(subject.getPrincipal() != null){
            // 如果未认证并且未IsreMenmberMe(Session失效问题)
            if(subject.isAuthenticated()/* || subject.isRemembered()*/){
                return true;
            }else{
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().print(JSONObject.toJSONString(new Result(401,"Log in")));
                return false;
//                try {
//                    String principal = subject.getPrincipal().toString();
//                    IUserService userService = (IUserService) ApplicationContextUtils.getBean("userServiceImpl");
//                    User user = userService.findByUserName(principal);
//                    //对密码进行加密后验证
//                    UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
//                    //把当前用户放入session
//                    subject.login(token);
//                    Session session = subject.getSession();
//                    session.setTimeout(2678400);
//                    //设置会话的过期时间--ms,默认是30分钟，设置负数表示永不过期
////                session.setTimeout(-1000l);
//                }catch (Exception e){
//                    e.printStackTrace();
//                    //自动登录失败,跳转到登录页面
//                    response.setContentType("application/json;charset=utf-8");
//                    response.getWriter().print(JSONObject.toJSONString(new Result(401,"认证失败")));
//                    return false;
//                }
//                if(!subject.isAuthenticated()){
//                    response.setContentType("application/json;charset=utf-8");
//                    response.getWriter().print(JSONObject.toJSONString(new Result(401,"认证失败")));
//                    //自动登录失败,跳转到登录页面
////                response.sendRedirect(request.getContextPath()+"/login");
//
//                    return false;
//                }
            }
//            return true;
        }
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(JSONObject.toJSONString(new Result(401,"Log in")));
        return false;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        return false;
    }
}

