package com.cloud.tv.core.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.mapper.PolicyMapper;
import com.cloud.tv.core.service.IPolicyService;
import com.cloud.tv.core.service.IPolicyStatisticalService;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.entity.Policy;
import com.cloud.tv.entity.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class PolicyServiceImpl implements IPolicyService {

    @Autowired
    private PolicyMapper policyMapper;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private IPolicyStatisticalService policyStatisticalService;

    @Override
    public Policy getObjById(String id) {
        return this.policyMapper.getObjById(id);
    }

    @Override
    public List<Policy> getObjByParentId(String parentId) {
        return this.policyMapper.getObjByParentId(parentId);
    }

    @Override
    public List<Policy> getObjOrderNo(String orderNo) {
        return this.policyMapper.getObjOrderNo(orderNo);
    }

    @Override
    public List<Policy> getObjByMap(Policy instance) {
        return this.policyMapper.getObjByMap(instance);
    }


    @Override
    public int save(Map policy) {
        return this.policyMapper.save(policy);
    }

    @Override
    public int delete(Policy policy) {
        return this.policyMapper.delete(policy);
    }

    @Override
    public Double getGrade(String deviceUuid) {
        if(deviceUuid != null && !deviceUuid.equals("")){
            PolicyDto dto = new PolicyDto();
            dto.setDeviceUuid(deviceUuid);
            // 计算总数
            // 策略统计
            SysConfig sysConfig = this.sysConfigService.findSysConfigList();
            String token = sysConfig.getNspmToken();
            int total = 0;
            double score = 0;
            if(token != null){
                String policyUrl = "/topology-policy/report/policy/policy-list-pie";
                Object result = this.nodeUtil.postFormDataBody(dto, policyUrl, token);
                JSONObject policyResult = JSONObject.parseObject(result.toString());
                if(policyResult.get("data") != null){
                    JSONObject data = JSONObject.parseObject(policyResult.get("data").toString());
                    total += Integer.parseInt(data.get("total").toString());
                }
                // 对象统计
                String objectUrl = "/topology-policy/report/policy/object-list-pie";
                Object objectResult = this.nodeUtil.postFormDataBody(dto, objectUrl, token);
                JSONObject ObjectResultJ = JSONObject.parseObject(objectResult.toString());
                if(ObjectResultJ.get("data") != null){
                    JSONObject data = JSONObject.parseObject(ObjectResultJ.get("data").toString());
                    total += Integer.parseInt(data.get("total").toString());
                }

                if(total != 0){
                    // 问题策略优化统计
                    String policyOptUrl =  "/topology-policy/report/policy/policy-check-pie";
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
                    // 问题对象优化统计
                    String objectOptUrl = "/topology-policy/report/policy/object-check-pie";
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

                score = score == 0 ? 1 : score;
                BigDecimal b2 = new BigDecimal(score);
                Double b3 = b2.divide(b1, 2, RoundingMode.HALF_UP).doubleValue();

                BigDecimal b4 = new BigDecimal(1);
                return b4.subtract(new BigDecimal(Double.toString(b3))).doubleValue() * 100;
            }
               return new Double(99);
        }
        return null;
    }

    @Override
    public int update(List<Policy> policies) {
        return this.policyMapper.update(policies);
    }
}
