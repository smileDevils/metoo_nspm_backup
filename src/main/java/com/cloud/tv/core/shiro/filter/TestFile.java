package com.cloud.tv.core.shiro.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Order(2)
@WebFilter(urlPatterns = "/qwe/*", filterName = "testFilter")
public class TestFile implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("testFilter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("testFilter doFilter");
        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {
        System.out.println("init");
    }
}
