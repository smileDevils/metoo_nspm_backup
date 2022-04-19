package com.cloud.tv.core.manager.integrated.policy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.http.HttpTools;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("策略开通")
@RequestMapping("/nspm/policy")
@RestController
public class OpenManageController {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private HttpTools httpTools;
    @Autowired
    private IUserService userService;

    @ApiOperation("列表")
    @RequestMapping("/push/recommend/task/searchtasklist.action")
    public Object searchsecuritypolicytasklist(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            String pushUrl = url + "/push/recommend/task/searchtasklist";
            Object result = this.nodeUtil.postFormDataBody(dto, pushUrl, token);
            // 检测用户
            List<String> users = this.userService.getObjByLevel(dto.getBranchLevel());
            if(users == null || users.size() <= 0){
                return ResponseUtil.ok();
            }
            JSONObject results = JSONObject.parseObject(result.toString());
            JSONObject data = JSONObject.parseObject(results.get("data").toString());
            JSONArray arrays = JSONArray.parseArray(data.get("list").toString());
            List list = new ArrayList();
            for(Object array : arrays){
                JSONObject obj = JSONObject.parseObject(array.toString());
                if(obj.get("id") != null){
                    String theme = obj.get("theme").toString();
                    int index = theme.indexOf("`~");
                    String userName = "";
                    if(index >= 0){
                        userName = theme.substring(0,index);
                        obj.put("userName", theme.substring(0,index));
                        obj.put("theme", theme.substring(index + 2));
                    }
                    if(users.contains(userName)){
                        PolicyDto policy = new PolicyDto();
                        policy.setTaskId(Integer.parseInt(obj.get("id").toString()));
                        policy.setPage(1);
                        policy.setPsize(1);
                        String taskUrl = url + "/push/task/pushtasklist";
                        Object taskList = this.nodeUtil.postFormDataBody(policy, taskUrl, token);
                        if(taskList != null){
                            JSONObject task = JSONObject.parseObject(taskList.toString());
                            if(!task.get("data").equals("")){
                                JSONObject taskData = JSONObject.parseObject(task.get("data").toString());
                                JSONArray taskArray = JSONArray.parseArray(taskData.get("list").toString());
                                if(taskArray.size() > 0){
                                    obj.put("task", taskArray.get(0));
                                }else{
                                    obj.put("task", "");
                                }
                            }
                        }
                        list.add(obj);
                    }
                }
            }
            data.put("list", list);
            results.put("data", data);
            return ResponseUtil.ok(results);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("业务开通")
    @RequestMapping("/push/recommend/task/addGlobalRecommendTask")
    public Object addGlobalRecommendTask(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/addGlobalRecommendTask";
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            dto.setTheme(user.getUsername()+"`~"+dto.getTheme());
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("互联网开通")
    @RequestMapping("/push/recommend/task/addGlobalinternat")
    public Object addGlobalinternat(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/addGlobalinternat";
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            dto.setTheme(user.getUsername()+"`~"+dto.getTheme());
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("起点标签")
    @RequestMapping("/push/recommend/label/list")
    public Object labelList(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/label/list";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("编辑")
    @RequestMapping("/push/recommend/task/gettaskbyid")
    public Object gettaskbyid(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/gettaskbyid";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            JSONObject results = JSONObject.parseObject(result.toString());
            JSONObject obj = JSONObject.parseObject(results.get("data").toString());
            String theme = obj.get("theme").toString();
            int index = theme.indexOf("`~");
            if(index >= 0){
                obj.put("userName", theme.substring(0,index));
                obj.put("theme", theme.substring(index + 2));
            }
            results.put("data",obj);
            return ResponseUtil.ok(results);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("编辑保存")
    @RequestMapping("/push/recommend/task/edit")
    public Object edit(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/edit";
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            dto.setTheme(user.getUsername()+"`~"+dto.getTheme());
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("删除")
    @RequestMapping("/push/recommend/task/deletetask")
    public Object deletetask(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/deletetask";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("仿真")
    @RequestMapping("/push/recommend/recommend/startGlobalRecommendTaskList")
    public Object startGlobalRecommendTaskList(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/recommend/startGlobalRecommendTaskList";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("重复仿真")
    @RequestMapping("/push/recommend/recommend/restart")
    public Object restart(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/recommend/restart";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命令行下载")
    @GetMapping("/push/recommend/download")
    public Object download(@RequestParam(required = false) String ids){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/download";
            Map map = new HashMap();
            map.put("ids", ids);
            return this.nodeUtil.download(map, url, token);
        }
        return null;
    }

    @ApiOperation("导出")
    @GetMapping("/push/recommend/task/export")
    public Object export(PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/export";
            if(dto.getIsReload().equals("true")){
                Object result = this.nodeUtil.getBody(dto, url, token);
                return ResponseUtil.ok(result);
            }else{
                Map map = new HashMap();
                map.put("isReload", false);
                return this.nodeUtil.download(map, url, token);
            }
        }
        return ResponseUtil.error();
   }

    @ApiOperation("任务执行情况")
    @RequestMapping("/push/recommend/task/gettaskstatus")
    public Object gettaskstatus(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/gettaskstatus";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("模拟仿真")
    @RequestMapping("/push/recommend/task/getpathstatic")
    public Object etpathstatic(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/getpathstatic";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("路径列表")
    @RequestMapping("/push/recommend/task/analyzepathinfolist")
    public Object analyzepathinfolist(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/analyzepathinfolist";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("路径状态")
    @RequestMapping("/push/recommend/task/enablepath")
    public Object enablepath(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/enablepath";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("路径详情")
    @RequestMapping("/push/recommend/task/pathdetail")
    public Object pathdetail(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/pathdetail";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("相关策略")
    @RequestMapping("/push/recommend/task/getrisk")
    public Object getrisk(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/getrisk";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("相关策略")
    @RequestMapping("/push/recommend/task/devicedetail")
    public Object devicedetail(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/devicedetail";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }


    @ApiOperation("策略建议详情")
    @RequestMapping("/push/recommend/task/getmergedpolicy")
    public Object getmergedpolicy(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/getmergedpolicy";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("下发结果")
    @RequestMapping("/push/recommend/task/getcheckresult")
    public Object getcheckresult(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/getcheckresult";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略开通建议")
    @RequestMapping("/push/recommend/task/getpolicy")
    public Object getpolicy(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/getpolicy";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("批量任务管理")
    @RequestMapping("/push/recommend/task/searchbatchlist")
    public Object searchbatchlist(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/searchbatchlist";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("批量下发")
    @RequestMapping("/push/recommend/getbatchdevicenum")
    public Object getbatchdevicenum(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/getbatchdevicenum";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("验证列表")
    @RequestMapping("/push/recommend/task/verifypathinfolist")
    public Object verifypathinfolist(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/verifypathinfolist";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

}