package com.cloud.tv.core.manager.view.action;

import com.cloud.tv.core.jwt.util.JwtUtil;
import com.cloud.tv.core.utils.CaptchaUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.entity.User;
import com.cloud.tv.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Api(description = "登录控制器")
@RestController
@RequestMapping(value = "/buyer")
public class LoginController{

    Logger log = LoggerFactory.getLogger(LoginController.class);

    @ApiOperation("登录方法")
    @RequestMapping("/login")
    public Object login(HttpServletRequest request, HttpServletResponse response,
                        String username, String password,@ApiParam("验证码") String captcha, String isRememberMe){
        String msg = "";
        // 通过安全工具类获取 Subject
        Subject subject = SecurityUtils.getSubject();
        // 获取当前已登录用户
        Session session = SecurityUtils.getSubject().getSession();
        String sessionCaptcha = (String) session.getAttribute("captcha");
        session.getStartTimestamp();
        if(captcha != null && !StringUtils.isEmpty(captcha) && !StringUtils.isEmpty(sessionCaptcha)){
            if(sessionCaptcha.toUpperCase().equals(captcha.toUpperCase())){
                boolean flag = true;// 当前用户是否已登录
                if(subject.getPrincipal() != null) {
                    User user = (User) SecurityUtils.getSubject().getPrincipal();
                    if (user != null && !user.getUsername().equals("")) {
                        if (user.getUsername().equals(username)) {
                            flag = false;
                        }
                    }
                }
                if(flag){
                    UsernamePasswordToken token = new UsernamePasswordToken(username,password);
                    try {
                        if(isRememberMe != null && isRememberMe.equals("1")){
                            token.setRememberMe(true);
                            // 或 UsernamePasswordToken token = new UsernamePasswordToken(username,password,true);
                        }else{
                            token.setRememberMe(false);
                        }
                        subject.login(token);// 进入ShiroRealm查询数据库获取用户信息
                        session.removeAttribute("captcha");
                        // 登陆成功，签发Jwt Token
                        String jwtToken = JwtUtil.sign(username, JwtUtil.SECRET);
                        response.setHeader(JwtUtil.AUTH_HEADER, jwtToken);
                        Cookie cookie = new Cookie("access_token", "3c221ede-fb1d-4492-b291-bc7f4983cdf0");
                        cookie.setMaxAge(-1);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                        return ResponseUtil.ok();
                        //  return "redirect:/index.jsp";
                    } catch (UnknownAccountException e) {
                        e.printStackTrace();
                        msg = "用户名错误";
                        System.out.println("用户名错误");
                        return new Result(410, msg);
                    } catch (IncorrectCredentialsException e){
                        e.printStackTrace();
                        msg = "密码错误";
                        System.out.println("密码错误");
                        return new Result(420, msg);
                    }
                }else{
                    return new Result(200, "用户已登录");
                }
            }else{
                return new Result(430, "验证码错误");
            }
        }else{
            return new Result(400,  "Captcha has expired");
        }
    }

    /**
     * 验证码图片
     */
    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置响应头信息，通知浏览器不要缓存
        response.setHeader("Expires", "-1");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "-1");
        response.setContentType("image/jpeg");

        // 获取验证码
        String code = CaptchaUtil.getRandomCode();
        // 将验证码输入到session中，用来验证
        HttpSession session = request.getSession();

        session.setAttribute("captcha", code);
        this.removeAttrbute(session, "captcha");
        System.out.println(session.getId());
        // 输出到web页面
        ImageIO.write(CaptchaUtil.genCaptcha(code), "jpg", response.getOutputStream());
    }

    public void removeAttrbute(final HttpSession session, final String attrName) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                // 删除session中存的验证码
                session.removeAttribute(attrName);
                timer.cancel();
            }
        }, 5 * 60 * 1000); //5 * 60 * 1000
    }
 /*   摘取自下方
————————————————
    版权声明：本文为CSDN博主「xxWillxx」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
    原文链接：https://blog.csdn.net/qq_40858048/article/details/81075805
*/
    @RequestMapping("/logout")
    public Object logout(HttpServletRequest request){
        Subject subject = SecurityUtils.getSubject();
        User obj = (User) subject.getPrincipal();
        if(obj != null){
            subject.logout(); // 退出登录
            return ResponseUtil.ok();
        }else{
            return new Result(401,"log in");
        }
    }



/*    @RequestMapping(value = "getYzm", method = RequestMethod.GET)
    public void getYzm(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");
            // 生成随机字串
            String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
            // 存入会话session
            HttpSession session = request.getSession(true);
            String attrName = "_code";
            session.setAttribute(attrName, verifyCode.toLowerCase());
            // 生成验证码流
            int w = 99, h = 38;
            VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
            //定时5分钟删除验证码
            this.removeAttrbute(session, attrName);
        } catch (Exception e) {
            log.error("获取验证码异常：{}",e.getCause().toString());
        }
    }

    *//**
     * 设置5分钟后删除session中的验证码
     * @param session
     * @param attrName
     *//*
    private void removeAttrbute(final HttpSession session, final String attrName) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 删除session中存的验证码
                session.removeAttribute(attrName);
                timer.cancel();
            }
        }, 5 * 60 * 1000);
    }
}
————————————————
        版权声明：本文为CSDN博主「请持续率性」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
        原文链接：https://blog.csdn.net/k21325/article/details/78365872*/
}

