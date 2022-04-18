package com.cloud.tv.core.manager.admin.tools;

import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.utils.AesEncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LicenseTools {

    @Autowired
    private AesEncryptUtils aesEncryptUtils;

    /**
     * 验证License合法性
     * @return
     */
    public boolean verify(String systemSN, String license){
        try {
            String decrypt = this.aesEncryptUtils.decrypt(license);
            Map map = JSONObject.parseObject(decrypt, Map.class);
            String sn = map.get("systemSN").toString();
            if(sn.equals(systemSN)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
