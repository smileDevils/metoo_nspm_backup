package com.cloud.tv.core.config;

import com.cloud.tv.core.jwt.interceptor.JwtInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>
 *     Title: InterceptorConfig.java
 * </p>
 *
 * <p>
 *      Description: JWT 拦截器注册管理类。使用JavaBean的方式，代替传统xml配置文件形式对框架个性化定制，
 *     可以自定义一些Handler，Interceptor，ViewResolver，MessageConverter
 *     implements WebMvcConfigurer(官方推荐)
 *      extends WebMvcConfigurerAdapter（已废弃）
 * </p>
 */

//@Configuration
public class InterceptorConfig implements WebMvcConfigurer  {

    /**
     * 注册JWT登录拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 第一种方式：拦截所有请求，使用注解方式，判断哪些接口需要登录 required
         *
         * 第二种方式：使用addPathPatterns excludePathPatterns 过滤
         */
        registry.addInterceptor(new JwtInterceptor())// 注册拦截器
                .addPathPatterns("/**")
                .excludePathPatterns("/jwt/**");// 设置拦截器的过滤路径规则

    }
}
