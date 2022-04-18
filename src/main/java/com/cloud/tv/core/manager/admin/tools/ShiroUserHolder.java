package com.cloud.tv.core.manager.admin.tools;

import com.cloud.tv.entity.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class ShiroUserHolder {


    public static User currentUser(){
        if(SecurityUtils.getSubject() != null){
            if(SecurityUtils.getSubject().getPrincipal() != null){
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                if(user != null){
                    return user;
                }
                return null;
            }
        }
        return null;
    }

}
