package com.cloud.tv.core.manager.integrated.node;

import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.CycleDto;
import com.cloud.tv.entity.SysConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Api("采集周期")
@RequestMapping("/nspm/cycle")
@RestController
public class TopoCycleManagerController {
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private NodeUtil nodeUtil;

    @ApiOperation("列表")
    @RequestMapping("/topology/cycle/getCyclePage")
    public Object getCyclePage(@RequestBody(required = false) CycleDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/cycle/getCyclePage";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("新增")
    @RequestMapping("/topology/cycle/saveCycle")
    public Object saveCycle(CycleDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/cycle/saveCycle";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("编辑")
    @RequestMapping("/topology/cycle/updateCycle")
    public Object updateCycle(CycleDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/cycle/updateCycle";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("分配组")
    @RequestMapping("/topology/cycle/batch-cycle-update")
    public Object batchUpdate(@RequestBody(required = false) CycleDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/cycle/batch-cycle-update";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }
}
