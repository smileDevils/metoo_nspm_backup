package com.cloud.tv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * <p>
 *     Title: SpringbootShiroApplication.java
 * </p>
 *
 * <p>
 *     Description: Springboot启动类，springBoot整合Mybatis、Shiro
 */

@SpringBootApplication // 申明让spring boot自动给程序进行必要的配置 == @Configuration ，@EnableAutoConfiguration 和 @ComponentScan
@ServletComponentScan(basePackages ={ "com.hkk.cloud.tv.filter"})//只用注解配置时，需要扫描包
// @ComponentScan("")// 等价：<context:component-scan base-package="com.metoo" /> 组件扫描
// @EnableAspectJAutoProxy(proxyTargetClass = true)
//@EnableTransactionManagement// 事务
public class SpringbootShiroApplication extends SpringBootServletInitializer {

    public static void main(String[] args) { 
        Long time=System.currentTimeMillis();
        SpringApplication.run(SpringbootShiroApplication.class);
        System.out.println("===应用启动耗时："+(System.currentTimeMillis()-time)+"===");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

}

