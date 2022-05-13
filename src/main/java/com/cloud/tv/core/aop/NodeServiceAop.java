package com.cloud.tv.core.aop;

import com.cloud.tv.core.service.ILicenseService;
import com.cloud.tv.entity.License;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Aspect
@Component
public class NodeServiceAop {

    @Autowired
    private ILicenseService licenseServicer;

    @Around("execution(* com.cloud.tv.core.manager.integrated.node.TopoNodeManagerAction.addGatherNode(..))")
    public Object around(){
        // 获取License详细信息
        License license = this.licenseServicer.query().get(0);
        // 设备授权信息
//        int licenseFwNum = license.getLicenseFwNum();
//        int currentFwNum = license.getCurrentFwNum();
//        int licenseRouterNum = license.getLicenseRouterNum();
//        int currentRouterNum = license.getCurrentRouterNum();
//        int licenseHostNum = license.getLicenseHostNum();
//        int currentHostNum = license.getCurrentHostNum();
//        int currentGatewayNum = license.getCurrentGatewayNum();
//        int licenseGatewayNum = license.getLicenseGatewayNum();

        return null;
    }

}
