package com.cloud.tv.core.manager.integrated.node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.service.*;
import com.cloud.tv.core.utils.ListSortUtil;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.NodeDto;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.Task;
import com.cloud.tv.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Api("系统首页")
@RequestMapping("/nspm/index")
@RestController
public class TopoNspmIndexManagerAction {

    @Autowired
    private IssuedService issuedService;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private static ListSortUtil listSortUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private IPolicyService policyService;
    @Autowired
    private IPolicyStatisticalService policyStatisticalService;

    public static void main(String[] args) {
        List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
        Map map1 = new HashMap();
        Map map2 = new HashMap();
        Map map3 = new HashMap();
        map1.put("policyCheckTotal",91);
        map2.put("policyCheckTotal", 50);
        map3.put("policyCheckTotal",100);
        list.add(map1);
        list.add(map2);
        list.add(map3);
//        listSortUtil.sort(list);
        System.out.println(list);


    }

    @RequestMapping("/task")
    public Object task(){
        List<Task> tasks = this.issuedService.query();
        return ResponseUtil.ok(tasks);
    }

    @RequestMapping("/policy")
    public Object policy(@RequestBody(required = false) PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        
        String token = sysConfig.getNspmToken();
        if(token != null){
            String nodeUrl = "/topology/node/queryNode.action";
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            dto.setBranchLevel(user.getGroupLevel());
            dto.setDisplay(0);
            dto.setOrigin(0);
            dto.setLimit(1000000);
            dto.setState("1");
            JSONArray arrays = new JSONArray();
            if(dto.getType() != null && !dto.getType().equals("")){
                Object nodes = this.nodeUtil.getBody(dto, nodeUrl, token);
                JSONObject nodeJson = JSONObject.parseObject(nodes.toString());
                JSONArray array = JSONArray.parseArray(nodeJson.get("data").toString());
                arrays.addAll(array);
            }else{
                List<String> types = new ArrayList();
                types.add("0");
                types.add("1");
                for(String type : types){
                    dto.setType(type);
                    Object nodes = this.nodeUtil.getBody(dto, nodeUrl, token);
                    JSONObject nodeJson = JSONObject.parseObject(nodes.toString());
                    if(nodeJson.get("data") != null) {
                        Map map = new HashMap();
                        map.put("total", nodeJson.get("total"));
                        JSONArray array = JSONArray.parseArray(nodeJson.get("data").toString());
                        arrays.addAll(array);
                    }
                }
            }
                String policyUrl = "/topology-policy/report/policyView/viewData";
                List policys = new ArrayList();
                List<Map<String, Double>> list = new ArrayList<Map<String, Double>>();
                for(Object obj : arrays){
                    if(obj != null){
                        JSONObject node = JSONObject.parseObject(obj.toString());
                        if(node.get("uuid") != null){
                            Map dataMap = new HashMap();
                            PolicyDto policyDto = new PolicyDto();
                            policyDto.setCurrentPage(1);
                            policyDto.setPageSize(dto.getLimit());
                            policyDto.setVendor(node.get("vendorName").toString());
                            policyDto.setType(node.get("type").toString());
                            policyDto.setDeviceUuid(node.get("uuid").toString());
                            Object policy = this.nodeUtil.postFormDataBody(policyDto, policyUrl, token);
                            JSONObject json = JSONObject.parseObject(policy.toString());
                            dataMap.put("grade", new Double(100));
                            dataMap.put("objectTotal", 0);
                            dataMap.put("policyTotal", 0);
                            dataMap.put("policyCheckTotal", 0);
                            dataMap.put("objectCheckTotal", 0);
                            if(json.get("data") != null){
                                JSONArray data = JSONArray.parseArray(json.get("data").toString());

                                JSONObject dataJson = JSONObject.parseObject(data.get(0).toString());
                                dataMap.put("deviceName", dataJson.get("deviceName") != null  ? dataJson.get("deviceName") : "");
//                                // 策略总数
//                                Integer policyTotal = dataJson.get("policyTotal") != null ? Integer.parseInt(dataJson.get("policyTotal").toString()) : 0;
                                // 策略总数
                               Integer policyTotal = 0;
//                            Integer policyTotal = dataJson.get("policyTotal") != null ? Integer.parseInt(dataJson.get("policyTotal").toString()) : 0;
//                            dataMap.put("policyTotal", policyTotal);
                                if(dataJson.get("policyTotalDetail") != null){
                                    JSONArray policyTotalDetails = JSONArray.parseArray(dataJson.get("policyTotalDetail").toString());
                                    JSONObject policyTotalDetail = JSONObject.parseObject(policyTotalDetails.get(0).toString());
                                    Integer aclTotal = 0;
                                    Integer natTotal = 0;
                                    Integer policyRoutTotal = 0;
                                    Integer safeTotal = 0;
                                    aclTotal = Integer.parseInt(policyTotalDetail.get("aclTotal").toString());
                                    natTotal = Integer.parseInt(policyTotalDetail.get("natTotal").toString());
                                    policyRoutTotal = Integer.parseInt(policyTotalDetail.get("policyRoutTotal").toString());
                                    safeTotal = Integer.parseInt(policyTotalDetail.get("safeTotal").toString());
                                    policyTotal = aclTotal + natTotal + policyRoutTotal + safeTotal;
                                }

                                dataMap.put("policyTotal", policyTotal);
                                // 对象总数
                                Integer objectTotal = dataJson.get("objectTotal") != null ? Integer.parseInt(dataJson.get("objectTotal").toString()) : 0;
                                dataMap.put("objectTotal", objectTotal);
                                Integer total  = policyTotal + objectTotal;
                                if(total > 0){
                                    // 问题策略数
                                    dataMap.put("policyCheckTotal", dataJson.get("policyCheckTotal") != null ? dataJson.get("policyCheckTotal") : 0);
                                    // 问题对象数
                                    dataMap.put("objectCheckTotal", dataJson.get("objectCheckTotal") != null ? dataJson.get("objectCheckTotal") : 0);
                                    // 健康度
                                    // 计算问题策略权重
                                    double policyGrade = 0;
                                    if(dataJson.get("policyCheckTotalDetail") != null){
                                        JSONArray detail = JSONArray.parseArray(dataJson.get("policyCheckTotalDetail").toString());
                                        if(detail.size() > 0){
                                            Map<String, Integer> map = JSONObject.parseObject(detail.get(0).toString(), Map.class);
                                            Set<String> keys = map.keySet();
                                            Iterator<String> it = keys.iterator();
                                            while (it.hasNext()){
                                                String key =  it.next();
                                                // 获取策略权重
                                                double weight = this.policyStatisticalService.getObjByCode(key);
                                                Integer value = map.get(key);
                                                BigDecimal b1 = new BigDecimal(value);
                                                BigDecimal b2 = new BigDecimal(weight);
                                                policyGrade += b1.multiply(b2).doubleValue();
                                            }
                                        }
                                    }
                                    // 问题对象策略权重
                                    double objectGrade = 0;
                                    if(dataJson.get("objectCheckTotalDetail") != null){
                                        JSONArray detail = JSONArray.parseArray(dataJson.get("objectCheckTotalDetail").toString());
                                        if(detail.size() > 0){
                                            Map<String, Integer> map = JSONObject.parseObject(detail.get(0).toString(), Map.class);
                                            Set<String> keys = map.keySet();
                                            Iterator<String> it = keys.iterator();
                                            while (it.hasNext()){
                                                String key =  it.next();
                                                // 获取对象权重
                                                double weight = this.policyStatisticalService.getObjByCode(key);
                                                Integer value = map.get(key);
                                                BigDecimal b1 = new BigDecimal(value);
                                                BigDecimal b2 = new BigDecimal(weight);
                                                objectGrade += b1.multiply(b2).doubleValue();
                                            }
                                        }
                                    }
                                    double checkTotal = policyGrade + objectGrade;
                                    BigDecimal b1 = new BigDecimal(Double.toString(total));
                                    BigDecimal b2 = new BigDecimal(Double.toString(checkTotal));
                                    BigDecimal b3 = b2.divide(b1, 2, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal b4 = new BigDecimal(1);
                                    dataMap.put("grade", b4.subtract(b3).doubleValue() * 100);
                                }
                            }
                            list.add(dataMap);
                        }
                    }
                }
                // 排序
                listSortUtil.sort(list);
                List list1 = list.subList(0,list.size() < 10 ? list.size() : 10);
                return ResponseUtil.ok(list1);
        }
        return ResponseUtil.ok();
    }

    @RequestMapping(value="/devices")
    public Object devices(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology/node/getChangeDevices.action";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("策略优化统计")
    @PostMapping(value = "/policy-check-pie")
    public Object policyCheckPie(@RequestBody(required = false)PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        
        String token = sysConfig.getNspmToken();
        if(token != null){
            String url = "/topology-policy/report/policy/policy-check-pie";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            JSONObject resultJ = JSONObject.parseObject(result.toString());
            if(resultJ.get("data") != null){
                JSONObject data = JSONObject.parseObject(resultJ.get("data").toString());
                JSONArray arrays = JSONArray.parseArray(data.get("statistics").toString());
                if(arrays.size() > 0){
                    for(Object object : arrays){
                        JSONObject policy = JSONObject.parseObject(object.toString());
                        if(policy.get("name").toString().equals("域外策略")){
                            arrays.remove(policy);
                        }
                    }
                    data.put("statistics",arrays);
                    resultJ.put("data",data);
                }
            }
            return ResponseUtil.ok(resultJ);
        }
        return ResponseUtil.error();
    }


}
