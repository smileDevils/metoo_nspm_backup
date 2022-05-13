package com.cloud.tv.core.manager.admin.action;

import com.cloud.tv.core.service.TestInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestInitAction {

    @Autowired
    private TestInitService testInitService;

    @RequestMapping("/testInit")
    public String test(){
//        return this.testInitService.getA();
        return null;
    }
}
