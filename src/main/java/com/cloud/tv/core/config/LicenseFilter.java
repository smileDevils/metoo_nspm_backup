package com.cloud.tv.core.config;

import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.service.ILicenseService;
import com.cloud.tv.entity.License;
import com.cloud.tv.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LicenseFilter implements Filter {

    @Autowired
    private ILicenseService licenseService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // 检测授权码
        License license = this.licenseService.detection();
        if(license != null && license.getStatus() == 0 && license.getFrom() == 0){
            chain.doFilter(req, res);
        }else{
            HttpServletResponse response = (HttpServletResponse) res;
            String message = "未授权";
            switch (license.getStatus()){
                case 1:
                    message = "未授权";
                    break;
                case 2:
                    message = "授权已过期";
                    break;
            }
            Result result = new Result(413, message);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().print(JSONObject.toJSONString(result));
        }
      }

    @Override
    public void destroy() {

    }
}
