package com.cloud.tv.core.manager.integrated.policy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.service.*;
import com.cloud.tv.core.utils.CommUtils;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.entity.Invisible;
import com.cloud.tv.entity.Policy;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.User;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RequestMapping("/nspm/policy")
@RestController
public class TopoPolicyIntegrateController {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private IUserService userService;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private IPolicyService policyService;
    @Autowired
    private IPolicyStatisticalService policyStatisticalService;
    @Autowired
    private InvisibleService invisibleService;
    @Autowired
    private IssuedService issuedService;

    private static Map<String, String> navigationMap = new HashMap();

    @Test
    public void test(){
        String a = "\\"+"u0003";

        String s = "\u0003";
        System.out.println(StringEscapeUtils.unescapeJava(a));
    }

    public static void main(String[] args) {
        String a = "\u0001";
        String b = "\u0002";
        String c = "\u0003";
        String d = "\u0004";
        String e = "\u0005";
        String f = "\u0006";
        String g = "\u0007";
        String h = "\u0008";
        String i = "\u0009";
        String j = "\u000B";
        String k = "\u000C";
        String l = "\u000E";
        String m = "\b";
        String n = "\f";
        String o = "\n";
        String p = "\r";
        String q = "\t";
        String r = "\u200a";
        String s = "\u200b";
        String t = "\u200c";
        String u = "\u200d";
        String v = "\u200e";
        String w = "\u200f";
        String x = "\020";
        String z = "\uFEFF";
        String aa = "\u202A";
        String bb = "\u202B";
        String cc = "\u202C";
        String dd = "\u202D";
        String ee = "\u202E";
        String ff = "\u202F";
        System.out.println("a：" + a);
        System.out.println("b：" + b);
        System.out.println("c：" + c);
        System.out.println("d：" + d);
        System.out.println("e：" + e);
        System.out.println("f：" + f);
        System.out.println("g：" + g);
        System.out.println("h：" + h);
        System.out.println("i：" + i);
        System.out.println("j：" + j);
        System.out.println("k：" + k);
        System.out.println("l：" + l);
        System.out.println("m：" + m);
        System.out.println("n：" + n);
        System.out.println("o：" + o);
        System.out.println("p：" + p);
        System.out.println("q：" + q);
        System.out.println("r：" + r);
        System.out.println("s：" + s);
        System.out.println("t：" + t);
        System.out.println("u：" + u);
        System.out.println("v：" + v);
        System.out.println("w：" + w);
        System.out.println("x：" + x);
        System.out.println("z：" + z);

        System.out.println("aa：" + aa);
        System.out.println("bb：" + bb);
        System.out.println("cc：" + cc);
        System.out.println("dd:" + dd);
        System.out.println("ee：" + ee);
    }

    @RequestMapping("/viewData")
    public Object vendors(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            String nodeUrl = url + "/topology/node/queryNode.action";
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            dto.setBranchLevel(user.getGroupLevel());
            dto.setDisplay(0);
            dto.setOrigin(0);
            dto.setLimit(1000000);
            dto.setState("1");
            JSONArray arrays = new JSONArray();
            Integer total = 0;
            if(dto.getType() != null && !dto.getType().equals("")){
                Object nodes = this.nodeUtil.getBody(dto, nodeUrl, token);
                JSONObject object = JSONObject.parseObject(nodes.toString());
                JSONArray array = JSONArray.parseArray(object.get("data").toString());
                arrays.addAll(array);
                total = Integer.parseInt(object.get("total").toString());
            }else{
                List<String> types = new ArrayList();
                types.add("0");
                types.add("1");
                for(String type : types){
                    dto.setType(type);
                    Object nodes = this.nodeUtil.getBody(dto, nodeUrl, token);
                    JSONObject object = JSONObject.parseObject(nodes.toString());
                    if(object.get("data") != null) {
                        JSONArray array = JSONArray.parseArray(object.get("data").toString());
                        arrays.addAll(array);
                        total += Integer.parseInt(object.get("total").toString());
                    }
                }
            }
                Map map = new HashMap();
                map.put("total", total);
                String policyUrl = url + "/topology-policy/report/policyView/viewData";
                List policys = new ArrayList();
                for(Object obj : arrays){
                    if(obj != null){
                        JSONObject node = JSONObject.parseObject(obj.toString());
                        if(node.get("uuid") != null){
                            Map dataMap = new HashMap();
                            navigationMap.put(node.get("ip").toString(), node.get("uuid").toString());
                            PolicyDto policyDto = new PolicyDto();
                            policyDto.setCurrentPage(1);
                            policyDto.setPageSize(dto.getLimit());
                            policyDto.setVendor(node.get("vendorName").toString());
                            policyDto.setType(node.get("type").toString());
                            policyDto.setDeviceUuid(node.get("uuid").toString());
                            Object policy = this.nodeUtil.postFormDataBody(policyDto, policyUrl, token);
                            JSONObject json = JSONObject.parseObject(policy.toString());
                            if(json.get("data") != null){
                                JSONArray data = JSONArray.parseArray(json.get("data").toString());
                                Double score = this.policyService.getGrade(node.get("uuid").toString());
                                JSONObject dataJson = JSONObject.parseObject(data.get(0).toString());
                                dataJson.put("policyCheckTotal", score);
                                policys.add(dataJson);
                                dataMap.put("policyCheckTotal", score);
                            }else{
                                dataMap.put("policyCheckTotal", 0);
                            }
                        }
                    }
                }
                map.put("policys", policys);
                return ResponseUtil.ok(map);
        }
        return ResponseUtil.ok();
    }



//    @RequestMapping("/viewData")
//    public Object viewData(@RequestBody(required = false) PolicyDto dto){
//        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
//        String nspmUrl = sysConfig.getNspmUrl();
//        String token = sysConfig.getNspmToken();
//        if(nspmUrl != null && token != null){
//            if(dto.getVendor() == null || dto.getVendor().equals("")){
//                String url = nspmUrl + "/topology/node/getNavigation.action";
//                User currentUser = ShiroUserHolder.currentUser();
//                User user = this.userService.findByUserName(currentUser.getUsername());
//                dto.setBranchLevel(user.getGroupLevel());
//                String type = dto.getType();
//                dto.setType(null);
//                Object navigations = this.nodeUtil.getBody(dto, url, token);
//                List<String> vendorList = new ArrayList<String>();
//                if(navigations != null){
//                    String result = JSONObject.toJSONString(navigations);
//                    JSONObject devices = JSONObject.parseObject(result);
//                    if(!devices.isEmpty()){
//                        List<Object> list = new ArrayList<Object>();
//                        for(String device : devices.keySet()){
//                            if(type != null){
//                                if(device.equals(type)){
//                                    JSONObject vendors = JSONObject.parseObject(devices.get(device).toString());
//                                    for(String vendor : vendors.keySet()){
//                                        dto.setVendor(vendor);
//                                        dto.setType(type);
//                                        String url2 = nspmUrl + "/topology-policy/report/policyView/viewData";
//                                        Object policy = this.nodeUtil.postFormDataBody(dto, url2, token);
//                                        JSONObject json = JSONObject.parseObject(policy.toString());
//                                        List array = Arrays.asList(json.get("data"));
//                                        list.addAll(array);
//                                    }
//
//                                }
//                            }else{
//                                JSONObject vendors = JSONObject.parseObject(devices.get(device).toString());
//                                for(String vendor : vendors.keySet()){
//                                    dto.setVendor(vendor);
//                                    dto.setType(type);
//                                    String url2 = nspmUrl + "/topology-policy/report/policyView/viewData";
//                                    Object policy = this.nodeUtil.postFormDataBody(dto, url2, token);
//                                    JSONObject json = JSONObject.parseObject(policy.toString());
//                                    List array = Arrays.asList(json.get("data"));
//                                    list.addAll(array);
//                                }
//
//                            }
//                        }
//                        return ResponseUtil.ok(list);
//                    }
//                }
//            }else{
//                String url2 = nspmUrl + "/topology-policy/report/policyView/viewData";
//                Object result = this.nodeUtil.postFormDataBody(dto, url2, token);
//                return ResponseUtil.ok(result);
//            }
//        }
//        return ResponseUtil.error();
//    }


//    @RequestMapping("/viewData")
//    public Object viewData(@RequestBody(required = false) PolicyDto dto){
//        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
//        String nspmUrl = sysConfig.getNspmUrl();
//        String token = sysConfig.getNspmToken();
//        String url2 = nspmUrl + "/topology-policy/report/policyView/viewData";
//        Object result = this.nodeUtil.postFormDataBody(dto, url2, token);
//        return ResponseUtil.ok(result);
//    }

//    @RequestMapping("/viewData")
//    public Object viewData(@RequestBody(required = false) PolicyDto dto){
//        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
//        String nspmUrl = sysConfig.getNspmUrl();
//        String token = sysConfig.getNspmToken();
//        if(nspmUrl != null && token != null){
//            String url = nspmUrl + "/topology-layer/whale/GET/node/navigation";
//            User currentUser = ShiroUserHolder.currentUser();
//            User user = this.userService.findByUserName(currentUser.getUsername());
//            dto.setBranchLevel(user.getGroupLevel());
//            Object navigations = this.nodeUtil.getBody(dto, url, token);
//            List<String> vendorList = new ArrayList<String>();
//            if(navigations != null){
//                String result = JSONObject.toJSONString(navigations);
//                JSONObject jsonObject = JSONObject.parseObject(result);
//                int navigationSize = jsonObject.size();// 设备数量
//                for(int i = 0; i < 6; i++){
//                    if(jsonObject.get(i) != null){
//                        if(i > 1){
//                            break;
//                        }
//                        JSONObject vendors = JSONObject.parseObject(jsonObject.get(i).toString());
//                        boolean flag = Integer.parseInt(dto.getType()) == i ? true : false;
//                        Map vendorMap = JSONObject.toJavaObject(vendors, Map.class);
//                        for(Object key : vendorMap.keySet()){
//                            Object value = vendorMap.get(key);
//                            if(flag){
//                                vendorList.add(key.toString());
//                            }
//                            JSONObject vendor = JSONObject.parseObject(JSONObject.toJSONString(value));
//                            Integer total = Integer.parseInt(vendor.get("total").toString());
//                            for(int n = 0; n < total; n++){
//                                JSONArray devices = JSONArray.parseArray(vendor.get("data").toString());
//                                JSONObject device = JSONObject.parseObject(devices.get(n).toString());
//                                navigationMap.put(device.get("ip").toString(), device.get("uuid").toString());
//                                System.out.println(device.get("pluginId").toString() + "/" +  device.get("vendorName").toString() + "/" + device.get("ip").toString());
//                            }
//                        }
//                    }
//                }
//            }
//            if(vendorList.size() > 0){
//                List<Object> list = new ArrayList<Object>();
//                for(String vendors : vendorList){
//                    String vendor = vendors;
//                    dto.setVendor(vendor);
//                    String url2 = nspmUrl + "/topology-policy/report/policyView/viewData";
//                    Object result = this.nodeUtil.postFormDataBody(dto, url2, token);
//                    JSONObject json = JSONObject.parseObject(result.toString());
//                    List array = Arrays.asList(json);
//                    list.addAll(array);
//                }
//                return ResponseUtil.ok(list);
//            }
//            return ResponseUtil.ok();
//        }
//        return ResponseUtil.error();
//    }

    @GetMapping(value = "/node/navigation")
    public Object nodeNavigation(PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/GET/node/navigation";
            Object result = this.nodeUtil.getBody(dto, url, token);
            Map map = JSONObject.parseObject(result.toString(), Map.class);
            Map resultMap = new HashMap();
            resultMap.put(0, map.get("0"));
            resultMap.put(1, map.get("1"));
            if(map.get("3") != null){
                Map vendor = JSONObject.parseObject(map.get("3").toString(), Map.class);
                for (Object key : vendor.keySet()){
                    if(key.toString().equals("安博通")){
                        vendor.put("觅通", vendor.get(key.toString()));
                        vendor.remove("安博通");
                    }
                }
                resultMap.put("3", vendor);
            }
            return ResponseUtil.ok(resultMap);
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
            Object object = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(object);
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
            Object object = this.nodeUtil.postFormDataBody(dto, url, token);
            JSONObject result = JSONObject.parseObject(object.toString());
            if(result.get("success") != null){
                if(result.get("success").toString().equals("false")){
                    return ResponseUtil.error(result.get("message").toString());
                }
            }
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
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
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
            url = url + "/topology-policy/policy/filter-list/rout-table";
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
    public Object listDeviceZoneInterface(@RequestBody  PolicyDto dto){
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
    public Object objectListPie(@RequestBody(required = false) PolicyDto dto){
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

//    @ApiOperation("策略优化-是否加入下发队列")
//    @PostMapping(value = "/push/task/addpushtasks")
//    public Object addPushShtasks(@RequestBody PolicyDto dto){
//        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
//        String url = sysConfig.getNspmUrl();
//        String token = sysConfig.getNspmToken();
//        if(url != null && token != null){
//            String addUrl = url + "/push/task/addpushtasks";
//            User currentUser = ShiroUserHolder.currentUser();
//            User user = this.userService.findByUserName(currentUser.getUsername());
//            JSONObject dtoJson = (JSONObject) JSONObject.toJSON(dto);
//            if(dtoJson != null){
//                JSONArray arrays = JSONArray.parseArray(dtoJson.get("tasks").toString());
//                if(arrays.size() > 0){
//                    JSONObject json = JSONObject.parseObject(arrays.get(0).toString());
//                    if(dto.getFrom().equals("2")){
//                        // "" ""
//                        json.put("command", "##" + "policy " + dto.getPolicyId() +"|"+ user.getUsername() + "`~\n" + json.get("command"));
//                    }else if(dto.getFrom().equals("17")){
//                        json.put("command", "##" + "object" + dto.getName() +"|"+ user.getUsername() + "`~\n" + json.get("command"));
//                    }
//                    arrays.clear();
//                    arrays.add(json);
//                    dto.setTasks(arrays);
//                    Object result = this.nodeUtil.postFormDataBody(dto, addUrl, token);
//                    JSONObject resultJ = JSONObject.parseObject(result.toString());
//                    if(resultJ.get("status").toString().equals("0")){
//                        // 执行完成 保存策略信息
//                        // 策略优化 查询策略信息
//                        if(dto.getFrom().equals("2")){
//                            PolicyDto policyDto = new PolicyDto();
//                            policyDto.setCurrentPage(dto.getCurrentPage());
//                            policyDto.setPageSize(dto.getPageSize());
//                            policyDto.setDeviceUuid(dto.getDeviceUuid());
//                            policyDto.setType(dto.getType());
//                            String policyUrl = url + "/topology-policy/policy/check";
//                            Object policyResult = this.nodeUtil.postFormDataBody(policyDto, policyUrl, token);
//                            JSONObject resultJson = JSONObject.parseObject(policyResult.toString());
//                            if(resultJson.get("data") != null){
//                                JSONArray poliscyArrays = JSONArray.parseArray(resultJson.get("data").toString());
//                                if(poliscyArrays.size() > 0){
//                                    for(Object array : poliscyArrays){
//                                        JSONObject data = JSONObject.parseObject(array.toString());
//                                        if(data.get("relatedPolicyId").toString().equals(dto.getPolicyId())){
//                                            // 查询当前策略是否存在，不存在则更新
//                                            Policy policy = new Policy();
//                                            policy.setParentId(dto.getPolicyId());
//                                            policy.setDeviceUuid(dto.getDeviceUuid());
//                                            List<Policy> policys = this.policyService.getObjByMap(policy);
//                                            if(policys.size() > 0){
//                                                int i = this.policyService.delete(policy);
//                                            }
//                                            policys = this.policyService.getObjByMap(policy);
//                                            if(policys.size() == 0){                                            // 插入数据库
//                                                JSONArray details = JSONArray.parseArray(data.get("detailList").toString());
//                                                for(Object obj : details){
//                                                    Map map = JSON.parseObject(obj.toString(), Map.class);
//                                                    map.put("parentId", dto.getPolicyId());
//                                                    map.put("policyType", "RuleCheck_1");
//                                                    map.put("deviceUuid", data.get("deviceUuid"));
//                                                    this.policyService.save(map);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }else if(dto.getFrom().equals("17")){ // 对象优化
//                            PolicyDto policyDto = new PolicyDto();
//                            policyDto.setCurrentPage(dto.getCurrentPage());
//                            policyDto.setPageSize(dto.getPageSize());
//                            policyDto.setDeviceUuid(dto.getDeviceUuid());
//                            policyDto.setType(dto.getType());
//                            policyDto.setObjectType(dto.getObjectType());
//                            String objectUrl = url + "/topology-policy/object/check";
//                            Object objectResult = this.nodeUtil.postFormDataBody(policyDto, objectUrl, token);
//                            JSONObject objects = JSONObject.parseObject(objectResult.toString());
//                            if(objects.get("data") != null){
//                                JSONArray objectArray = JSONArray.parseArray(objects.get("data").toString());
//                                if(objectArray.size() > 0){
//                                    for(Object array : objectArray){
//                                        JSONObject data = JSONObject.parseObject(array.toString());
//                                        if(data.get("name").toString().equals(dto.getName())){
//                                            // 执行批量删除
//                                            Policy policy = new Policy();
//                                            policy.setName(dto.getName());
//                                            policy.setDeviceUuid(dto.getDeviceUuid());
//                                            List<Policy> policys = this.policyService.getObjByMap(policy);
//                                            if(policys.size() > 0){
//                                                int i = this.policyService.delete(policy);
//                                            }
//                                            // 查询当前策略是否存在，不存在则更新
//                                            policys = this.policyService.getObjByMap(policy);
//                                            if(policys.size() == 0){
//                                                // 插入数据库
//                                                Map map = JSON.parseObject(data.toString(), Map.class);
//                                                map.put("policyType", "RC_EMPTY_OBJECT");
//                                                this.policyService.save(map);
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                        }
//                    }
//                    return ResponseUtil.ok(result);
//                }
//            }
//            return ResponseUtil.badArgument();
//        }
//        return ResponseUtil.error();
//    }

    @ApiOperation("策略优化-是否加入下发队列")
    @PostMapping(value = "/push/task/addpushtasks")
    public Object addPushShtasks(@RequestBody PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            String addUrl = url + "/push/task/addpushtasks";
            JSONObject dtoJson = (JSONObject) JSONObject.toJSON(dto);
            if(dtoJson != null){
                JSONArray arrays = JSONArray.parseArray(dtoJson.get("tasks").toString());
                if(arrays.size() > 0){
                    JSONObject json = JSONObject.parseObject(arrays.get(0).toString());
                    // 获取不可见字符
                    String invisibleName = this.invisibleService.getName();
                    String command =  json.get("command").toString();
                    json.put("command", StringEscapeUtils.unescapeJava(invisibleName) + "\n" +json.get("command"));
//                    json.put("command", "\\" + invisibleName + " \n" +json.get("command"));
                    arrays.clear();
                    arrays.add(json);
                    dto.setTasks(arrays);
                    Object result = this.nodeUtil.postFormDataBody(dto, addUrl, token);
                    JSONObject resultJ = JSONObject.parseObject(result.toString());
                    if(resultJ.get("status").toString().equals("0")) {
                        // 更新字符状态
                        Invisible invisible = new Invisible();
                        invisible.setName(invisibleName);
                        invisible.setStatus(1);
                        this.invisibleService.update(invisible);
                        // 执行完成 保存策略信息
                        // 策略优化 查询策略信息
                        if (dto.getFrom().equals("2")) {
                            PolicyDto policyDto = new PolicyDto();
                            policyDto.setCurrentPage(dto.getCurrentPage());
                            policyDto.setPageSize(dto.getPageSize());
                            policyDto.setDeviceUuid(dto.getDeviceUuid());
                            policyDto.setType(dto.getType());
                            String policyUrl = url + "/topology-policy/policy/check";
                            Object policyResult = this.nodeUtil.postFormDataBody(policyDto, policyUrl, token);
                            JSONObject resultJson = JSONObject.parseObject(policyResult.toString());
                            if (resultJson.get("data") != null) {
                                JSONArray poliscyArrays = JSONArray.parseArray(resultJson.get("data").toString());
                                if (poliscyArrays.size() > 0) {
                                    for (Object array : poliscyArrays) {
                                        JSONObject data = JSONObject.parseObject(array.toString());
                                        if(data.get("index").toString().equals(dto.getIndex().toString())){
                                            // 查询当前策略是否存在，不存在插入数据 存在 清空数据后插入
                                            Policy policy = new Policy();
                                            String random = CommUtils.randomString(6);
                                            policy.setParentName(random);
                                            policy.setDeviceUuid(dto.getDeviceUuid());
                                            JSONArray details = JSONArray.parseArray(data.get("detailList").toString());
                                            for (Object obj : details) {
                                                Map map = JSON.parseObject(obj.toString(), Map.class);
                                                map.put("parentName", random);
                                                map.put("policyType", "RuleCheck_1");
                                                map.put("deviceUuid", data.get("deviceUuid"));
                                                map.put("invisible", invisibleName);
                                                this.policyService.save(map);
                                            }
                                            // 更新策略信息，添加工单号
                                            List<Policy> policysNew = this.policyService.getObjByMap(policy);
                                            System.out.println(StringEscapeUtils.unescapeJava(invisibleName));
                                            this.issuedService.queryTask(invisibleName, dto.getFrom(), policysNew, command);

                                            break;
                                        }
                                    }
                                }
                            }

                        }else if(dto.getFrom().equals("17")){ // 对象优化
                            PolicyDto policyDto = new PolicyDto();
                            policyDto.setCurrentPage(dto.getCurrentPage());
                            policyDto.setPageSize(dto.getPageSize());
                            policyDto.setDeviceUuid(dto.getDeviceUuid());
                            policyDto.setType(dto.getType());
                            policyDto.setObjectType(dto.getObjectType());
                            String objectUrl = url + "/topology-policy/object/check";
                            Object objectResult = this.nodeUtil.postFormDataBody(policyDto, objectUrl, token);
                            JSONObject objects = JSONObject.parseObject(objectResult.toString());
                            if(objects.get("data") != null){
                                JSONArray objectArray = JSONArray.parseArray(objects.get("data").toString());
                                if(objectArray.size() > 0){
                                    for(Object array : objectArray){
                                        JSONObject data = JSONObject.parseObject(array.toString());
                                        if(data.get("index").toString().equals(dto.getIndex().toString())){
                                            // 执行批量删除
                                            String random = CommUtils.randomString(6);
                                            Policy policy = new Policy();
                                            policy.setParentName(random);
                                            policy.setDeviceUuid(dto.getDeviceUuid());
//                                             插入数据库
                                                Map map = JSON.parseObject(data.toString(), Map.class);
                                                map.put("policyType", "RC_EMPTY_OBJECT");
                                                map.put("invisible", invisibleName);
                                                this.policyService.save(map);
                                            // 更新策略信息，添加工单号
                                            List<Policy> policysNew = this.policyService.getObjByMap(policy);
                                            System.out.println(StringEscapeUtils.unescapeJava(invisibleName));
                                            this.issuedService.queryTask(invisibleName, dto.getFrom(), policysNew, command);
                                            break;
                                        }

                                    }
                                }
                            }

                        }

                    }
                    return ResponseUtil.ok(result);
                }
            }
            return ResponseUtil.badArgument();
        }
        return ResponseUtil.error();
    }

    @RequestMapping("/delete")
    public Object delete(@RequestBody Policy policy){
        int i = this.policyService.delete(policy);
        return ResponseUtil.ok();
    }

    //
    @RequestMapping("/grade")
    public Object grade(@RequestBody PolicyDto dto){
        // 计算总数
        // 策略统计
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        int total = 0;
        double score = 0;
        if(url != null && token != null){
            String policyUrl = url + "/topology-policy/report/policy/policy-list-pie";
            Object result = this.nodeUtil.postFormDataBody(dto, policyUrl, token);
            JSONObject policyResult = JSONObject.parseObject(result.toString());
            if(policyResult.get("data") != null){
                JSONObject data = JSONObject.parseObject(policyResult.get("data").toString());
                total += Integer.parseInt(data.get("total").toString());
            }
            // 对象统计
            String objectUrl = url + "/topology-policy/report/policy/object-list-pie";
            Object objectResult = this.nodeUtil.postFormDataBody(dto, objectUrl, token);
            JSONObject ObjectResultJ = JSONObject.parseObject(objectResult.toString());
            if(ObjectResultJ.get("data") != null){
                JSONObject data = JSONObject.parseObject(ObjectResultJ.get("data").toString());
                total += Integer.parseInt(data.get("total").toString());
            }


            // 策略优化统计
            String policyOptUrl = url + "/topology-policy/report/policy/policy-check-pie";
            Object policyOptResult = this.nodeUtil.postFormDataBody(dto, policyOptUrl, token);
            JSONObject policyOptResultJ = JSONObject.parseObject(policyOptResult.toString());
            if(policyOptResultJ.get("data") != null){
                JSONObject data = JSONObject.parseObject(policyOptResultJ.get("data").toString());
                JSONArray arrays = JSONArray.parseArray(data.get("statistics").toString());
                for(Object array : arrays){
                    JSONObject json = JSONObject.parseObject(array.toString());
                    // 根据测录Code查询对应策略
                    int value = Integer.parseInt(json.get("value").toString());
                    Double weight = this.policyStatisticalService.getObjByCode(json.get("code").toString());
                    BigDecimal b1 = new BigDecimal(value);

                    BigDecimal b2 = new BigDecimal(weight);
                    score += b1.multiply(b2).doubleValue();
                }
            }
            // 对象优化统计
            String objectOptUrl = url + "/topology-policy/report/policy/object-check-pie";
            Object objectOptResult = this.nodeUtil.postFormDataBody(dto, objectOptUrl, token);
            JSONObject objectOptResultJ = JSONObject.parseObject(objectOptResult.toString());
            if(objectOptResultJ.get("data") != null){
                JSONObject data = JSONObject.parseObject(objectOptResultJ.get("data").toString());
                JSONArray arrays = JSONArray.parseArray(data.get("statistics").toString());
                for(Object array : arrays){
                    JSONObject json = JSONObject.parseObject(array.toString());
                    // 根据测录Code查询对应策略
                    int value = Integer.parseInt(json.get("value").toString());
                    double weight = this.policyStatisticalService.getObjByCode(json.get("name").toString());
                    BigDecimal b1 = new BigDecimal(value);
                    BigDecimal b2 = new BigDecimal(Double.toString(weight));
                    score += b1.multiply(b2).doubleValue();
                }
            }
        }
        BigDecimal b1 = new BigDecimal(Double.toString(total));

        BigDecimal b2 = new BigDecimal(score);

        Double b3 = b2.divide(b1, 2, RoundingMode.HALF_UP).doubleValue();

        BigDecimal b4 = new BigDecimal(1);

        return b4.subtract(new BigDecimal(b3)).doubleValue();

    }

}
