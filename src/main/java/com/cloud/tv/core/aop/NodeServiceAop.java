package com.cloud.tv.core.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.service.ILicenseService;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.utils.AesEncryptUtils;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.TopoNodeDto;
import com.cloud.tv.dto.TopoPolicyDto;
import com.cloud.tv.entity.License;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.vo.LicenseVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NodeServiceAop {

    @Autowired
    private ILicenseService licenseServicer;
    @Autowired
    private AesEncryptUtils aesEncryptUtils;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private NodeUtil nodeUtil;

    private static final Integer pageSize = 10;

    @Around("execution(* com.cloud.tv.core.manager.integrated.node.TopoNodeManagerAction.addGatherNode(..))")
    public Object around(ProceedingJoinPoint pjp){
        // 获取License详细信息
        License obj = this.licenseServicer.query().get(0);
        // 设备授权信息
        String code = null;
        try {
            code = this.aesEncryptUtils.decrypt(obj.getLicense());
            LicenseVo license = JSONObject.parseObject(code, LicenseVo.class);
            if(license != null){
                SysConfig sysConfig = this.sysConfigService.findSysConfigList();
                String token = sysConfig.getNspmToken();
                String url = "/topology/ums/getLicenseInfo.action";
                TopoNodeDto dto = new TopoNodeDto();
                dto.setStart(0);
                dto.setLimit(20);
                Object object = this.nodeUtil.getBody(dto, url, token);
                if(object != null){
                    JSONObject result = JSONObject.parseObject(object.toString());
                    String data = result.get("data").toString();
                    JSONObject json = JSONObject.parseObject(data);
                    license.setUseFirewall(Integer.parseInt(json.get("currentFwNum").toString()));
                    license.setUseRouter(Integer.parseInt(json.get("currentRouterNum").toString()));
                    license.setUseHost(Integer.parseInt(json.get("currentHostNum").toString()));
                    license.setUseUe(Integer.parseInt(json.get("currentGatewayNum").toString()));
                }
            }
            Signature signature = pjp.getSignature();
            String methodName = signature.getName();
            Object[] arguments = pjp.getArgs();
            System.out.println();
            String node = JSONObject.toJSONString((Object)arguments[0]);
            JSONObject json = JSONObject.parseObject(node);
            int device = 0;
            switch(methodName){
                case "addGatherNode":
                    // 判断设备类型
                    if(json.get("deviceType").equals("0") && license.getLicenseFireWall() <= license.getUseFirewall()){
                        return ResponseUtil.error("防火墙已达到最大授权数，禁止上传");
                    }
                    if(json.get("deviceType").equals("1") && license.getLicenseRouter() <= license.getUseRouter()){
                        return ResponseUtil.error("路由交换已达到最大授权数，禁止上传");
                    }
                    if(json.get("deviceType").equals("3") && license.getLicenseUe() <= license.getUseUe()){
                        return ResponseUtil.error("未适配设备已达到最大授权数，禁止上传");
                    }
                default:
                    return pjp.proceed();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Around("execution(* com.cloud.tv.core.manager.integrated.policy.TopoPolicyIntegrateController.ruleListSearch(..))" + " || execution(* com.cloud.tv.core.manager.integrated.policy.TopoPolicyIntegrateController.objectList(..))")
    public Object  befor(ProceedingJoinPoint pjp) throws Throwable {
        License obj = this.licenseServicer.query().get(0);
        // 设备授权信息
        String code = null;
        code = this.aesEncryptUtils.decrypt(obj.getLicense());
        LicenseVo license = JSONObject.parseObject(code, LicenseVo.class);
        if(license != null){
            if(license.getType().equals("试用版")){
                Signature signature = pjp.getSignature();
                String methodName = signature.getName();
                Object[] arguments = pjp.getArgs();
                String node = JSONObject.toJSONString((Object)arguments[0]);
                JSONObject json = JSONObject.parseObject(node);
                json.put("pageSize", pageSize);
                TopoPolicyDto dto = JSON.parseObject(JSON.toJSONString(json), TopoPolicyDto.class);
                arguments[0] = dto;
                return pjp.proceed(arguments);
            }
        }
        return pjp.proceed();
    }

}
