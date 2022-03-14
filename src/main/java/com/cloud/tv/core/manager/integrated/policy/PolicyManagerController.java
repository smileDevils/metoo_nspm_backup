package com.cloud.tv.core.manager.integrated.policy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.entity.SysConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/nspm/policy")
@RestController
public class PolicyManagerController {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private NodeUtil nodeUtil;

    private static Map<String, String> navigationMap = new HashMap();

    @RequestMapping("/viewData")
    public Object viewData(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String nspmUrl = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(nspmUrl != null && token != null){
            String url = nspmUrl + "/topology-layer/whale/GET/node/navigation";
            Object navigations = this.nodeUtil.getBody(dto, url, token);
            if(navigations != null){
                String result = JSONObject.toJSONString(navigations);
                JSONObject jsonObject = JSONObject.parseObject(result);
                int navigationSize = jsonObject.size();// 设备数量
                for(int i = 0; i < navigationSize; i++){
                    JSONObject vendors = JSONObject.parseObject(jsonObject.get("0").toString());
                    Map vendorMap = JSONObject.toJavaObject(vendors, Map.class);
                    for(Object key : vendorMap.keySet()){
                        Object value = vendorMap.get(key);
                        JSONObject vendor = JSONObject.parseObject(JSONObject.toJSONString(value));
                        Integer total = Integer.parseInt(vendor.get("total").toString());
                        for(int n = 0; n < total; n++){
                            JSONArray devices = JSONArray.parseArray(vendor.get("data").toString());
                            JSONObject device = JSONObject.parseObject(devices.get(n).toString());
                            navigationMap.put(device.get("ip").toString(), device.get("uuid").toString());
                        }
                    }
                }
            }
            String url2 = nspmUrl + "/topology-policy/report/policyView/viewData";
            Object result = this.nodeUtil.postFormDataBody(dto, url2, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

        @GetMapping(value = "/node/navigation")
    public Object nodeNavigation(PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "topology-layer/whale/GET/node/navigation";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @GetMapping("/getUuid")
    public Object getUuid(String ip){
        if(ip != null && !navigationMap.isEmpty()){
            return ResponseUtil.ok(navigationMap.get(ip));
        }
        return ResponseUtil.badArgument();
    }

    @ApiOperation("策略列表")
    @PostMapping(value = "/rule-list-search")
    public Object ruleListSearch(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/rule-list-search";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("合并策略（详情）")
    @PostMapping(value = "/rule-list")
    public Object ruleList(@RequestBody(required = false)PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/filter-list/rout/rule-list";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("源Ip、目的Ip、服务(对象信息)")
    @RequestMapping("/query-object-detail")
    public Object queryObjectDetail(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/device/query-object-detail";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @RequestMapping("/batch-skip-check")
    public Object batchSkipCheck(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/batch-skip-check";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略列表统计")
    @PostMapping(value = "/policy-list-pie")
    public Object policyListPie(@RequestBody(required = false)PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/report/policy/policy-list-pie";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略集")
    @PostMapping(value = "/filter-list")
    public Object filterList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/filter-list";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略集(静态路由)")
    @PostMapping(value = "/filter-list/static-rout")
    public Object filterListStaticRout(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/filter-list/static-rout";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略集(静态路由)")
    @PostMapping(value = "/filter-list/rout-table")
    public Object filterListRoutTable(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "topology-policy/policy/filter-list/rout-table";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("安全域")
    @PostMapping(value = "/listDeviceZone")
    public Object listDeviceZone(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/risk/api/alarm/zone/listDeviceZone";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }


    @ApiOperation("设备接口")
    @PostMapping(value = "/listDeviceInterface")
    public Object listDeviceInterface(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/risk/api/alarm/zone/listDeviceInterface";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("详情")
    @PostMapping(value = "/raw-config")
    public Object rawConfig(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/device/raw-config";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("备注")
    @PostMapping(value = "/remark/get")
    public Object remarkGet(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/remark/get";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("备注")
    @PostMapping(value = "/remark/save")
    public Object remarkSave(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/remark/save";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("安全域接口")
    @PostMapping(value = "/listDeviceZoneInterface")
    public Object listDeviceZoneInterface(PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/risk/api/alarm/zone/listDeviceZoneInterface";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("对象分布")
    @PostMapping(value = "/object-list-pie")
    public Object objectListPie(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/report/policy/object-list-pie";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("对象列表")
    @PostMapping(value = "/object-list")
    public Object objectList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/device/object-list";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略列表-对象列表-查询")
    @PostMapping(value = "/topology-policy/device/search-address")
    public Object searchAddress(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/device/search-address";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略列表-对象列表-查询")
    @PostMapping(value = "/topology-policy/device/search-service")
    public Object searchService(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/device/search-service";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }




    @ApiOperation("策略优化列表")
    @PostMapping(value = "/check")
    public Object check(@RequestBody(required = false)PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/check";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略优化统计")
    @PostMapping(value = "/policy-check-pie")
    public Object policyCheckPie(@RequestBody(required = false)PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/report/policy/policy-check-pie";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("ACL未调用（未调用策略集）")
    @PostMapping(value = "/unrefAclList")
    public Object unrefAclList(@RequestBody(required = false)PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/unrefAclList";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("合并策略（详情）")
    @PostMapping(value = "/check/primary")
    public Object checkPrimary(@RequestBody(required = false)PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/check/primary";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略优化-对象统计")
    @PostMapping(value = "/object-check-pie")
    public Object objectCheckPie(@RequestBody(required = false)PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/report/policy/object-check-pie";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略优化-对象优化")
    @PostMapping(value = "/object/check")
    public Object objectCheck(@RequestBody(required = false)PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/object/check/";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略优化-是否加入下发队列")
    @PostMapping(value = "/push/task/addpushtasks")
    public Object addPushShtasks(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/task/addpushtasks";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命中收敛-厂商")
    @PostMapping(value = "/listVendorName")
    public Object listVendorName(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/hit/count/listVendorName";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命中收敛-内网ip地址管理")
    @PostMapping(value = "/intranetIpList")
    public Object intranetIpList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/hit/logConfig/intranetIpList";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命中收敛-任务列表")
    @PostMapping(value = "/listDevNodeLogConfig")
    public Object listDevNodeLogConfig(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/hit/count/listDevNodeLogConfig";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命中收敛-内网ip地址管理-保存")
    @PostMapping(value = "/saveIntranetIp")
    public Object saveIntranetIp(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/hit/logConfig/saveIntranetIp";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命中收敛-内网ip地址管理-保存")
    @PostMapping(value = "/logConfigList")
    public Object logConfigList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/hit/logConfig/list";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命中收敛-内网ip地址管理-保存")
    @PostMapping(value = "/logConfigEdit")
    public Object logConfigEdit(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/hit/logConfig/edit";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命中收敛-内网ip地址管理-保存")
    @PostMapping(value = "/hitCountList")
    public Object hitCountList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/hit/count/hitCountList";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("命中收敛-标记忽略检查")
    @PostMapping(value = "/skipCheck")
    public Object skipCheck(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/skip-check";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-任务列表")
    @PostMapping(value = "/combing/api/suggest/task/list")
    public Object taskList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/task/list";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-新建-设备列表")
    @PostMapping(value = "/combing/api/suggest/task/getDevNode")
    public Object getDevNode(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/task/getDevNode";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-新建-设备列表-选择")
    @PostMapping(value = "/combing/api/suggest/task/checkDevLog")
    public Object checkDevLog(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/task/checkDevLog";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-新建-保存")
    @PostMapping(value = "/combing/api/suggest/task/add")
    public Object taskAdd(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/task/add";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-新建-删除")
    @PostMapping(value = "/combing/api/suggest/task/delete")
    public Object delete(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/task/delete";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-任务-详情")
    @PostMapping(value = "/combing/api/suggest/result/list")
    public Object resultList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/result/list";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-任务-梳理")
    @PostMapping(value = "/combing/api/suggest/task/again")
    public Object again(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/task/again";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-任务-停止梳理")
    @PostMapping(value = "/combing/api/suggest/threadPool/stopTask")
    public Object stopTask(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/threadPool/stopTask";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-任务-日志")
    @PostMapping(value = "/combing/api/suggest/rawlog/findList")
    public Object findList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/rawlog/findList";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略梳理-任务-日志-查找设备")
    @PostMapping(value = "/combing/api/suggest/rawlog/findDeviceIsVsys")
    public Object findDeviceIsVsys(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/combing/api/suggest/rawlog/findDeviceIsVsys";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略对比-列表")
    @PostMapping(value = "/topology-policy/policy/getPolicyCompareTaskList")
    public Object getPolicyCompareTaskList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/getPolicyCompareTaskList";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略对比-新增")
    @PostMapping(value = "/topology-policy/policy/createPolicyCompareTask")
    public Object createPolicyCompareTask(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/createPolicyCompareTask";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略对比-删除")
    @PostMapping(value = "/topology-policy/policy/deletePolicyCompareTask")
    public Object deletePolicyCompareTask(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/deletePolicyCompareTask";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略对比-详情")
    @PostMapping(value = "/topology-policy/policy/policyCompareTaskDetail")
    public Object policyCompareTaskDetail(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/policyCompareTaskDetail";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略对比-安全策略/静态路由")
    @PostMapping(value = "/topology-policy/policy/policyCompare/list")
    public Object policyCompareList(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/policyCompare/list";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略对比-统计信息")
    @PostMapping(value = "/topology-policy/policy/policyCompare/statisticalInformation")
    public Object statisticalInformation(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-policy/policy/policyCompare/statisticalInformation";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }



}
