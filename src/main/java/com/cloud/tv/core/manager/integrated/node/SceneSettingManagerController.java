package com.cloud.tv.core.manager.integrated.node;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.SceneDto;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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
    private SceneDto dto;
    @Autowired
    private IUserService userService;

    @ApiOperation("场景列表")
    @PostMapping(value = "/push/api/disposal/scenes/pageList")
    public Object list(@RequestBody(required = false) SceneDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/api/disposal/scenes/pageList";
            Object result = this.nodeUtil.postBody(dto, url, token);  List<String>
            users = this.userService.getObjByLevel(dto.getBranchLevel());
            if(users == null || users.size() <= 0){
                return ResponseUtil.ok();
            }
            JSONObject results = JSONObject.parseObject(result.toString());
            JSONObject data = JSONObject.parseObject(results.get("data").toString());
            JSONArray arrays = JSONArray.parseArray(data.get("list").toString());
            List list = new ArrayList();
            for(Object array : arrays){
                JSONObject obj = JSONObject.parseObject(array.toString());
                String theme = obj.get("name").toString();
                int index = theme.indexOf("`~");
                String userName = "";
                if(index >= 0){
                    userName = theme.substring(0,index);
                    obj.put("createUser", theme.substring(0,index));
                    obj.put("name", theme.substring(index + 2));
                }
                if(users.contains(userName)){
                    list.add(obj);
                }
            }
            data.put("list", list);
            results.put("data", data);
            return ResponseUtil.ok(results);
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
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            dto.setName(user.getUsername()+"`~"+dto.getName());
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
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
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
            String[] ids = dto.getIds().split(",");
            if(ids.length>0){
                url = url + "/push/api/disposal/scenes/delete";
                for(String id : ids){
                    SceneDto obj = new SceneDto();
                    obj.setId(Integer.parseInt(id));
                    this.nodeUtil.postFormDataBody(obj, url, token);
                }
                return ResponseUtil.ok();
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.error();
    }

}
