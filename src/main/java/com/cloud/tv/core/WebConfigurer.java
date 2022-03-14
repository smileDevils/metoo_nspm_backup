package com.cloud.tv.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Configuration
public class WebConfigurer /*extends WebMvcConfigurerAdapter*/ {

   /* @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry){
        viewControllerRegistry.addViewController("/").setViewName("user/login");
        //设置ViewController的优先级,将此处的优先级设为最高,当存在相同映射时依然优先执行
        viewControllerRegistry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.addViewControllers(viewControllerRegistry);
    }*/

}
