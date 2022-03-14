package com.cloud.tv.core.manager.integrated.expose;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.http.HttpTools;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.ExposureDto;
import com.cloud.tv.dto.OperationDto;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.User;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/nspm/operation")
@RestController
public class OperationManageController {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private HttpTools httpTools;

    @ApiOperation("仿真开通-命令行")
    @PostMapping(value = "/task/command")
    public Object taskCommand(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            String url1 = url + "/push/task/addpushtasks";
            Object result = this.nodeUtil.postFormDataBody(dto, url1, token);
            JSONObject jsonObject = JSONObject.parseObject(result.toString());
            String status = jsonObject.get("status").toString();
            if("0".equals(status)){
                // 根据用户名查询工单记录
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                String userName = user.getUsername();
                OperationDto operation = new OperationDto();
                operation.setPage(1);
                operation.setPsize(1);
                String url2 = url + "push/task/pushtasklist";
                Object operations = this.nodeUtil.postFormDataBody(operation, url2, token);
                JSONObject body = JSONObject.parseObject(operations.toString());
                JSONObject data = JSONObject.parseObject(body.get("data").toString());
                Integer total = Integer.parseInt(data.get("total").toString());
                String taskId = "";
                if(total > 0){
                    JSONArray devices = JSONArray.parseArray(data.get("list").toString());
                    JSONObject list = JSONObject.parseObject(devices.get(0).toString());
                    taskId = list.get("taskId").toString();
                }
                if(!taskId.equals("")){
                    OperationDto operation1 = new OperationDto();
                    operation1.setTaskId(Integer.parseInt(taskId));
                    String url3 = url + "push/recommend/task/getcommand";
                    Object recomment = this.nodeUtil.postFormDataBody(operation1, url3, token);
                    if(recomment != null){
                        // 删除对应工单信息
                        OperationDto operation2 = new OperationDto();
                        operation2.setIds(taskId);
                        String url4 = url + "push/recommend/task/deletetask";
                        this.nodeUtil.postFormDataBody(operation2, url4, token);
                        return ResponseUtil.ok(recomment);
                    }
                }
            }
            return ResponseUtil.ok("未匹配到设备！未能生成命令行");
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略生成列表")
    @RequestMapping("push/recommend/task/searchsecuritypolicytasklist")
    public Object searchsecuritypolicytasklist(@RequestBody(required = false) OperationDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/searchsecuritypolicytasklist";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略生成（保存）")
    @RequestMapping("push/recommend/task/new-policy-push")
    public Object push(@RequestBody(required = false) OperationDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/new-policy-push";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略生成（删除）")
    @RequestMapping("push/recommend/task/deletesecuritypolicytasklist")
    public Object delete(@RequestBody(required = false) OperationDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/deletesecuritypolicytasklist";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略生成（命令行）")
    @RequestMapping("push/recommend/task/getcommand")
    public Object getcommand(@RequestBody(required = false) OperationDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/getcommand";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略生成（下载）")
    @GetMapping("push/recommend/task/download")
    public Object download(@RequestParam(name = "ids") String ids){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "push/recommend/download";
            Map map = new HashMap();
            map.put("ids", ids);
          return this.nodeUtil.download(map, url, token);

        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略生成（策略生成导出）")
    @RequestMapping("push/recommend/task/exportTask")
    public Object exportTask(OperationDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/exportTask";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略生成（策略下载）")
    @RequestMapping("push/recommend/task/exportTaskDown")
    public Object exportTaskDownLoad(String isReload) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/recommend/task/exportTask";
            Map map = new HashMap();
            map.put("isReload", isReload);
            return this.nodeUtil.download(map, url, token);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略生成（批量生成-模板下载）")
    @RequestMapping("push/recommend/task/downloadsecuritytemplate")
    public Object downloadsecuritytemplate() {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/recommend/task/downloadsecuritytemplate";
            return this.nodeUtil.downloadPost(null, url, token);
        }
        return ResponseUtil.error();
    }
}
