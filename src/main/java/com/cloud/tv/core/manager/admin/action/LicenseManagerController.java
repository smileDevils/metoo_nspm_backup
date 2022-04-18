package com.cloud.tv.core.manager.admin.action;

import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.manager.admin.tools.LicenseTools;
import com.cloud.tv.core.service.ILicenseService;
import com.cloud.tv.core.utils.AesEncryptUtils;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.core.utils.SystemInfoUtils;
import com.cloud.tv.entity.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/license")
@RestController
public class LicenseManagerController {

    @Autowired
    private ILicenseService licenseService;
    @Autowired
    private SystemInfoUtils systemInfoUtils;
    @Autowired
    private AesEncryptUtils aesEncryptUtils;
    @Autowired
    private LicenseTools licenseTools;

    @RequestMapping("/systemInfo")
    public Object systemInfo(){
        try {
            System.out.println(this.systemInfoUtils.getBiosUuid());
            return this.aesEncryptUtils.encrypt(this.systemInfoUtils.getBiosUuid());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/info")
    public Object get(){
        License license = this.licenseService.query().get(0);
        Map map = new HashMap();
        map.put("sn", license.getSystemSN());
        return ResponseUtil.ok(map);
    }

    /**
     * 授权
     * @param license
     * @return
     */
    @PutMapping("update")
    public Object license(@RequestParam("license") String license){
        String uuid = this.systemInfoUtils.getBiosUuid();
        // 验证license合法性
        boolean flag = this.licenseTools.verify(uuid, license);
        if(flag){
            License obj = this.licenseService.query().get(0);
            if(!obj.getLicense().equals(license)){
                obj.setLicense(license);
                if(obj.getFrom() == 1){
                    obj.setFrom(0);
                    obj.setSystemSN(uuid);
                    obj.setStatus(0);
                    if(!this.verify(license)){
                        obj.setStatus(2);
                    }
                }
                this.licenseService.update(obj);
                return ResponseUtil.ok("授权成功");
            }
            return ResponseUtil.badArgument("重复授权");
        }
        return ResponseUtil.error("非法授权码");
    }

    @RequestMapping("/license")
    public void license(){
        // 1，获取设备唯一申请码
        String biosUuid = this.systemInfoUtils.getBiosUuid();
        // 2，查询并比对当前设备是否允许授权
        List<License> licenseList = this.licenseService.query();
        License license = null;
        if(licenseList.size() > 0){
            license = licenseList.get(0);
        }
        if(license != null){
            String systemSN = license.getSystemSN();
            boolean from = true;// 是否检测授权码：不在同意设备时不允许使用
            // 初始化设备，并检查当前设备是否为初始化设备
            if(systemSN == null || systemSN.isEmpty()){// 申请码为空
                license.setSystemSN(biosUuid);
            }else{
                if(!systemSN.equals(biosUuid)){// 申请码不一致
                    license.setFrom(1);
                    license.setStatus(1);
                    from = false;
                }else{// 一致时恢复来源
                    license.setFrom(0);
                }
            }
            // 检测授权码是否已过期
            if(from && license.getLicense() != null && !license.getLicense().isEmpty()){
                String licenseInfo = license.getLicense();
                Map map = null;
                try {
                    map = JSONObject.parseObject(aesEncryptUtils.decrypt(licenseInfo), Map.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(map != null){
                    String endTimeStamp = map.get("expireTime").toString();// 有效期
                    if(endTimeStamp != null && !endTimeStamp.isEmpty()){
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        long currentTime = calendar.getTimeInMillis();
                        long timeStampSec = currentTime / 1000;// 13位时间戳（单位毫秒）转换为10位字符串（单位秒）
                        String timestamp = String.format("%010d", timeStampSec);// 当前时间
                        if(Long.valueOf(endTimeStamp).compareTo(Long.valueOf(timestamp)) <= 0){
                            license.setStatus(2);// 过期
                        }else{
                            license.setStatus(0);// 恢复为未过期
                        }
                    }
                }
                license.setStatus(1);// 未授权
            }
            // 更新License
            licenseService.update(license);
        }
    }

    public boolean verify(String license)  {
        // 检测授权码是否已过期
        if (license != null) {
            Map map = null;
            try {
                map = JSONObject.parseObject(this.aesEncryptUtils.decrypt(license), Map.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (map != null) {
                String endTimeStamp = map.get("expireTime").toString();// 有效期
                if (endTimeStamp != null && !endTimeStamp.isEmpty()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    long currentTime = calendar.getTimeInMillis();
                    long timeStampSec = currentTime / 1000;// 13位时间戳（单位毫秒）转换为10位字符串（单位秒）
                    String timestamp = String.format("%010d", timeStampSec);// 当前时间
                    if (Long.valueOf(endTimeStamp).compareTo(Long.valueOf(timestamp)) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
