package com.cloud.tv.core.manager.integrated.policy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.http.HttpTools;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.mapper.IssuedMapper;
import com.cloud.tv.core.service.*;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.entity.Order;
import com.cloud.tv.entity.Policy;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jws.Oneway;
import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;

@Api("策略下发")
@RequestMapping("/nspm/policy")
@RestController
public class IssuedManagerController {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private HttpTools httpTools;
    @Autowired
    private IUserService userService;
    @Autowired
    private IPolicyService policyService;
    @Autowired
    private IssuedService issuedService;
    @Autowired
    private IOrderService orderService;

    public static void main(String[] args) {
        String s = "\u000C";
        System.out.println(s);
    }


//    @ApiOperation("列表")
//    @RequestMapping("/push/task/pushtasklist")
//    public Object pushtasklist(@RequestBody(required = false) PolicyDto dto){
//        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
//        String url = sysConfig.getNspmUrl();
//        String token = sysConfig.getNspmToken();
//        if(url != null && token != null){
//            String taskUrl = url + "/push/task/pushtasklist";
//            Object result = this.nodeUtil.postFormDataBody(dto, taskUrl, token);
//            if(result != null){
//                List<String> users = this.userService.getObjByLevel(dto.getBranchLevel());
//                if(users == null || users.size() <= 0){
//                    return ResponseUtil.ok();
//                }
//                JSONObject task = JSONObject.parseObject(result.toString());
//                List list = new ArrayList();
//                if(!task.get("data").equals("")){
//                    JSONObject taskData = JSONObject.parseObject(task.get("data").toString());
//                    JSONArray arrays = JSONArray.parseArray(taskData.get("list").toString());
//                    for (Object array : arrays){
//                        JSONObject obj = JSONObject.parseObject(array.toString());
//                        String theme = obj.get("orderNo").toString();
//                        int index = theme.indexOf("`~");
//                        String userName = "";
//                        if(index >= 0){
//                            userName = theme.substring(0,index);
//                            obj.put("userName", theme.substring(0,index));
//                            obj.put("orderNo", theme.substring(index + 2));
//                        }
//                        if(obj.get("orderType").toString().equals("2") || obj.get("orderType").toString().equals("17")){
//                            String taskId =  obj.get("taskId").toString();
//                            String commandUrl = url + "/push/recommend/task/getcommand";
//                            PolicyDto policyDto = new PolicyDto();
//                            policyDto.setTaskId(Integer.parseInt(taskId));
//                            Object commandInfo = this.nodeUtil.postFormDataBody(policyDto, commandUrl, token);
//                            JSONObject results = JSONObject.parseObject(commandInfo.toString());
//                            JSONArray commandArrays = JSONArray.parseArray(results.get("data").toString());
//                            for(Object info : commandArrays){
//                                JSONObject infos = JSONObject.parseObject(info.toString());
//                                obj.put("deviceUuid", infos.get("deviceUuid").toString());
//                                String command = infos.get("command").toString();
//                                int indexOf = command.indexOf("`~\n");
//                                String name = "";
//                                if(indexOf > -1){
//                                    userName = command.substring(0,indexOf);
//                                    int inx = userName.indexOf("##");
//                                    userName = userName.substring(inx + 2);
//                                    if(userName.indexOf("|") > -1){
//                                       // 拆分 策略/对象
//                                        String type = userName.substring(0, userName.indexOf("|"));
//                                        int policy = type.indexOf("policy");
//                                        if(policy != -1){
//                                            obj.put("policyId", type.substring(type.indexOf("policy") + 6));
//                                        }
//                                        int object = type.indexOf("object");
//                                        if(object != -1){
//                                            obj.put("objectName", type.substring(type.indexOf("object") + 6));
//                                        }
//                                        obj.put("userName", userName.substring(userName.indexOf("|") + 1));
//                                        userName = userName.substring(userName.indexOf("|") + 1);
//                                    }
//                                }
//                            }
//                        }
//                        if(users.contains(userName)) {
//                            obj.put("userName", userName);
//                            list.add(obj);
//                        }
//                    }
//                    taskData.put("list", list);
//                    task.put("data", taskData);
//                }
//                return ResponseUtil.ok(task);
//            }
//            return ResponseUtil.ok();
//        }
//        return ResponseUtil.error();
//    }

    @ApiOperation("列表")
    @RequestMapping("/push/task/pushtasklist")
    public Object pushtasklist(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            String taskUrl = url + "/push/task/pushtasklist";
            Object result = this.nodeUtil.postFormDataBody(dto, taskUrl, token);
            if(result != null){
                List<String> users = this.userService.getObjByLevel(dto.getBranchLevel());
                if(users == null || users.size() <= 0){
                    return ResponseUtil.ok();
                }
                JSONObject task = JSONObject.parseObject(result.toString());
                List list = new ArrayList();
                if(!task.get("data").equals("")){
                    JSONObject taskData = JSONObject.parseObject(task.get("data").toString());
                    JSONArray arrays = JSONArray.parseArray(taskData.get("list").toString());
                    for (Object array : arrays){
                        JSONObject obj = JSONObject.parseObject(array.toString());
                        String theme = obj.get("orderNo").toString();
                        int index = theme.indexOf("`~");
                        String userName = "";
                        if(index >= 0){
                            userName = theme.substring(0,index);
                            obj.put("userName", theme.substring(0,index));
                            obj.put("orderNo", theme.substring(index + 2));
                        }
                        if(obj.get("orderType").toString().equals("2") || obj.get("orderType").toString().equals("17")){
                            String orderNo =  obj.get("orderNo").toString();
//                            根据工单id查询用户信息
                            Order order = this.orderService.getObjByOrderNo(orderNo);
                            userName = order == null ? "" : order.getUserName();
                        }
                        if(users.contains(userName)) {
                            obj.put("userName", userName);
                            list.add(obj);
                        }
                    }
                    taskData.put("list", list);
                    task.put("data", taskData);
                }
                return ResponseUtil.ok(task);
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.error();
    }

//    @ApiOperation("列表")
//    @RequestMapping("/push/task/pushtasklist")
//    public Object pushtasklist(@RequestBody(required = false) PolicyDto dto){
//        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
//        String url = sysConfig.getNspmUrl();
//        String token = sysConfig.getNspmToken();
//        if(url != null && token != null){
//            String taskUrl = url + "/push/task/pushtasklist";
//            Object result = this.nodeUtil.postFormDataBody(dto, taskUrl, token);
//            if(result != null){
//                List<String> users = this.userService.getObjByLevel(dto.getBranchLevel());
//                if(users == null || users.size() <= 0){
//                    return ResponseUtil.ok();
//                }
//                JSONObject task = JSONObject.parseObject(result.toString());
//                List list = new ArrayList();
//                if(!task.get("data").equals("")){
//                    JSONObject taskData = JSONObject.parseObject(task.get("data").toString());
//                    JSONArray arrays = JSONArray.parseArray(taskData.get("list").toString());
//                    for (Object array : arrays){
//                        JSONObject obj = JSONObject.parseObject(array.toString());
//                        if(obj.get("orderType").toString().equals("2") || obj.get("orderType").toString().equals("17")){
//                            String orderNo =  obj.get("orderNo").toString();
////                            根据工单id查询用户信息
//                            Order order = this.orderService.getObjByOrderNo(orderNo);
//                            String userName = order.getUserName();
//                            if(users.contains(userName)) {
//                                obj.put("userName", userName);
//                                list.add(obj);
//                            }
//                        }
//                    }
//                    taskData.put("list", list);
//                    task.put("data", taskData);
//                }
//                return ResponseUtil.ok(task);
//            }
//            return ResponseUtil.ok();
//        }
//        return ResponseUtil.error();
//    }



    @ApiOperation("策略信息")
    @RequestMapping("/getPolicy")
    public Object policys(@RequestBody Policy policy){
        List<Policy> policys = this.policyService.getObjOrderNo(policy.getOrderNo());
        return ResponseUtil.ok(policys);
    }


    @ApiOperation("下发详情")
    @RequestMapping("/push/recommend/task/getcommand")
    public Object getcommand(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/getcommand";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            JSONObject results = JSONObject.parseObject(result.toString());
            JSONArray arrays = JSONArray.parseArray(results.get("data").toString());
            List list = new ArrayList();
            for(Object array : arrays){
                JSONObject obj = JSONObject.parseObject(array.toString());

                String command = obj.get("command").toString();
                int index = command.indexOf("\n");
                String str = command.substring(0, index);
                obj.put("command", command.substring(str.length() + 1));
                list.add(obj);
            }
            results.put("data", list);
            return ResponseUtil.ok(results);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("列表")
    @RequestMapping("/push/task/pushtaskstatuslist")
    public Object pushtaskstatuslist(){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/task/pushtaskstatuslist";
            Object result = this.nodeUtil.postFormDataBody(null, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("获取下发信息")
    @RequestMapping("/push/task/getdevicenum")
    public Object getdevicenum(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/task/getdevicenum";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("下发")
    @RequestMapping("/push/task/startpushtasks")
    public Object startpushtasks(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/task/startpushtasks";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            this.issuedService.pushtaskstatuslist();
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("停止下发")
    @RequestMapping("/push/task/stoppushtasks")
    public Object stoppushtasks(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/task/stoppushtasks";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            this.issuedService.pushtaskstatuslist();
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("下发")
    @RequestMapping("/push/task/checkNatOrder")
    public Object checkNatOrder(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/task/checkNatOrder";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命令下发")
    @RequestMapping("/push/task/startdevicepushtasks")
    public Object startdevicepushtasks(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/task/startdevicepushtasks";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            this.issuedService.pushtaskstatuslist();
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }



    @ApiOperation("命令保存")
    @RequestMapping("/push/recommend/task/editcommand.action")
    public Object editcommand(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/recommend/task/editcommand.action";
//            User currentUser = ShiroUserHolder.currentUser();
//            User user = this.userService.findByUserName(currentUser.getUsername());
//            JSONObject dtoJson = (JSONObject) JSONObject.toJSON(dto);
//            if(dtoJson != null){
//                String command = dtoJson.get("command").toString();
//                dto.setCommand( "##" + user.getUsername() + "`~\n" + command);
//                Object result = this.nodeUtil.postBody(dto, url, token);
//                return ResponseUtil.ok(result);
//            }

            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("下发计划")
    @RequestMapping("/push/task/setschedule")
    public Object setschedule(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/task/setschedule";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }


}
