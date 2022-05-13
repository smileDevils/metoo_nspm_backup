package com.cloud.tv.core.manager.admin.tools;

import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.utils.AesEncryptUtils;
import com.cloud.tv.entity.License;
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
    public boolean verify(String systemSN, String code){
        try {
            String decrypt = this.aesEncryptUtils.decrypt(code);
            License license = JSONObject.parseObject(decrypt, License.class);
            String sn = license.getSystemSN();
            if(sn.equals(systemSN)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
