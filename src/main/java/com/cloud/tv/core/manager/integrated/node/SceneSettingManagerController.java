package com.cloud.tv.core.manager.integrated.node;

import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.SceneDto;
import com.cloud.tv.entity.SysConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Api("场景设置")
@RequestMapping("/nspm/scene")
@RestController
public class SceneSettingManagerController {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private NodeUtil nodeUtil;

    @ApiOperation("场景列表")
    @PostMapping(value = "/push/api/disposal/scenes/pageList")
    public Object list(@RequestBody(required = false) SceneDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/api/disposal/scenes/pageList";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("场景新建")
    @PostMapping(value = "/push/api/disposal/scenes/edit")
    public Object edit(@RequestBody(required = false) SceneDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/api/disposal/scenes/edit";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("场景编辑")
    @PostMapping(value = "/push/api/disposal/scenes/getByUUId")
    public Object getByUUId(@RequestBody(required = false) SceneDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/api/disposal/scenes/getByUUId";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("场景删除")
    @PostMapping(value = "/push/api/disposal/scenes/delete")
    public Object delete(@RequestBody(required = false) SceneDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/api/disposal/scenes/delete";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }
}
