package com.cloud.tv.core.jwt.action;

import com.cloud.tv.core.jwt.util.JwtUtil;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *     Title: JwtLoginAction
 * </p>
 *
 * <p>
 *     Description: Spring Boot 集成 JWT
 *     SpringWeb项目：在拦截器中拦截登录请求；
 *     SpeingCloud项目：在网关中拦截登录请求；
 * </p>
 *
 * <author>
 *     HKK
 * </author>
 */
@RestController
@RequestMapping("/jwt/buyer")
public class JwtLoginAction {

    @Autowired
    private IUserService userService;

    @RequestMapping("login")
    @ResponseBody
    public Object login(HttpServletResponse response, User user) throws IOException {
        //this.userService.
        Map payload = new HashMap();
        payload.put("username", user.getUsername());
        String token = JwtUtil.getToken(payload);
        response.setHeader("token", token);
//        response.getWriter().print("TestResponse");

        return token;
    }

    @RequestMapping("/test/filter")
    @ResponseBody
    public Object test_filter(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("h","kk");
        return ResponseUtil.ok();
    }


}
