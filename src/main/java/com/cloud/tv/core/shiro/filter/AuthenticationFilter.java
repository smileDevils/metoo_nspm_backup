package com.cloud.tv.core.shiro.filter;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**              c
 *
 * <p>
 *     Title: AuthenticationFilter.java
 * </p>
 *
 * <p>
 *     Description: 认证、授权过滤器，未认证授权情况下访问后端页面，前后端分离自动重定向到指定URL，前端提示302（重定向）；
 * </p>
 */
/*@Order(0)
@WebFilter("/*")*/
public class AuthenticationFilter extends FormAuthenticationFilter {


   /* @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResp = WebUtils.toHttp(response);
        HttpServletRequest httpReq = WebUtils.toHttp(request);
        System.out.println("onAccessDenied");
        *//**系统重定向会默认把请求头清空，这里通过拦截器重新设置请求头，解决跨域问题*//*
        httpResp.addHeader("Access-Control-Allow-Origin", httpReq.getHeader("Origin"));
        httpResp.addHeader("Access-Control-Allow-Headers", "*");
        httpResp.addHeader("Access-Control-Allow-Methods", "*");
        httpResp.addHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("===AuthenticationFilter===");
        WebUtils.toHttp(response).sendRedirect("/admin/auth/401");
        return false;
    }*/
}
