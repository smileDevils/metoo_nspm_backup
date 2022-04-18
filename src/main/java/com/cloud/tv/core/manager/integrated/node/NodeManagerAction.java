package com.cloud.tv.core.manager.integrated.node;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.service.IGroupService;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.NodeDto;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.entity.Group;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.User;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/nspm/node")
@RestController
public class NodeManagerAction {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private IGroupService groupService;

    @ApiOperation("设备列表")
    @GetMapping(value = "/topology-layer/whale/GET/node/navigation")
    public Object nodeNavigation(PolicyDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
           if(dto.getBranchLevel() == null || dto.getBranchLevel().equals("")){
               User currentUser = ShiroUserHolder.currentUser();
               User user = this.userService.findByUserName(currentUser.getUsername());
               dto.setBranchLevel(user.getGroupLevel());
           }
            url = url + "topology-layer/whale/GET/node/navigation";
            Object result = this.nodeUtil.getBody(dto, url, token);
            Map map = JSONObject.parseObject(result.toString(), Map.class);
            Map resultMap = new HashMap();
            resultMap.put(0, map.get("0"));
            resultMap.put(1, map.get("1"));
            return ResponseUtil.ok(resultMap);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("节点列表")
    @RequestMapping("/nodeQuery")
    public Object nodeQuery(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/queryNode.action";
            if(dto.getBranchLevel() == null || dto.getBranchLevel().equals("")){
                User currentUser = ShiroUserHolder.currentUser();
                User user = this.userService.findByUserName(currentUser.getUsername());
                dto.setBranchLevel(user.getGroupLevel());
            }
            Object result = this.nodeUtil.getBody(dto, url, token);
            JSONObject object = JSONObject.parseObject(result.toString());
            if(object != null){
                List list = new ArrayList();
                if(object.get("data") != null){
                    JSONArray arrays = JSONArray.parseArray(object.get("data").toString());
                    for(Object array : arrays){
                        JSONObject json = JSONObject.parseObject(array.toString());
                        if(json.get("errorMess") != null && !json.get("errorMess").toString().equals("")){
                            String errorMess = json.get("errorMess").toString();
                            if(errorMess.indexOf("脚本异常") > -1){
                                errorMess = errorMess.substring(0, errorMess.indexOf("脚本异常"));
                                json.put("errorMess", errorMess);
                            }
                        }
                        if(json.get("branchLevel") != null){
                           Group group = this.groupService.getObjByLevel(json.get("branchLevel").toString());
                           if(group != null){
                               json.put("branchName", group.getBranchName());
                           }
                        }
                        list.add(json);
                    }
                    object.put("data", list);
                    return ResponseUtil.ok(object);
                }
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.error();
    }

    @ApiOperation("厂商")
    @RequestMapping("/vendor")
    public Object vendor(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            if(dto == null){
                dto = new NodeDto();
            }
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            dto.setBranchLevel(user.getGroupLevel());
            url = url + "/topology/node/getNavigation.action";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("编辑")
    @RequestMapping(value="/device/devices")
    public Object deviceDevices(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/device/devices/";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("保存")
    @RequestMapping(value="/simulation/addGateway")
    public Object simulationAddGateway(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        String uuid = dto.getUuid();
        if(url != null && token != null){
            url = url + "/topology/node/simulation/addGateway.action/";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("编辑保存（防火墙）")
    @RequestMapping("/updateNode")
    public Object updateNode(NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/updateNode.action";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("编辑保存（防火墙）")
    @RequestMapping("/addGatherNode")
    public Object addGatherNode(NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/addGatherNode.action";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("验证Ip是否存在")
    @RequestMapping("/booleanExistIPs")
    public Object booleanExistIPs(NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/booleanExistIPs.action";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("节点列表")
    @RequestMapping("/nodeDelete")
    public Object nodeDelete(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/nodeDelete.action";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("设备接口")
    @RequestMapping(value="/view/configuration")
    public Object viewConfiguration(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        String uuid = dto.getUuid();
        if(url != null && token != null){
            url = url + "/topology/businessSubnet/GET/deviceInfo/" + uuid;
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("业务子网自动学习")
   @PutMapping(value="/deviceBusinessSubnet")
    public Object deviceBusinessSubnet(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/businessSubnet/PUT/deviceBusinessSubnet/";
            Object result = this.nodeUtil.putBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("设备接口编辑")
    @PutMapping(value="/businessSubnet")
    public Object businessSubnet(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/businessSubnet/PUT/businessSubnet/";
            Object result = this.nodeUtil.putBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("设备配置")
    @PutMapping(value="device/rawConfig")
    public Object rawConfig(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/device/rawConfig/";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("采集历史")
    @PostMapping(value="/queryNodeHistory")
    public Object queryNodeHistory(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/queryNodeHistory.action/";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("文件对比")
    @GetMapping(value="/showConfig")
    public Object showConfig(NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/showConfig.action    ";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("路由表采集历史")
    @PostMapping(value="/queryRouteTableHistory")
    public Object queryRouteTableHistory(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/queryRouteTableHistory.action/";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("参与路径计算")
    @RequestMapping(value="/updateNodeSkipAnalysis")
    public Object updateNodeSkipAnalysis(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "topology/node/updateNodeSkipAnalysis/";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("同进同出")
    @RequestMapping(value="/updateNodeToSameInbound")
    public Object updateNodeToSameInbound(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/updateNodeToSameInbound/";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("二层设备")
    @RequestMapping(value="/updateNodeLayerTwoDevice")
    public Object updateNodeLayerTwoDevice(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/updateNodeLayerTwoDevice/";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("变更总览")
    @RequestMapping(value="/device/reversion")
    public Object deviceReversion(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/device/reversion/";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("变更总览")
    @RequestMapping(value="/device/change")
    public Object deviceChange(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/device/change/";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("对比")
    @GetMapping(value="/showRouteTableConfig")
    public Object showRouteTableConfig(NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/showRouteTableConfig.action/";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }


    @ApiOperation("设备类型")
    @GetMapping(value="/engineJson")
    public Object engineJson(){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/engineJson.action";
            Object result = this.nodeUtil.getBody(null, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("采集凭据/下发凭据")
    @RequestMapping(value="/push/credential/getall")
    public Object push(@RequestBody NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/push/credential/getall/";
            if(dto.getBranchLevel() == null || dto.getBranchLevel().equals("")){
                User currentUser = ShiroUserHolder.currentUser();
                User user = this.userService.findByUserName(currentUser.getUsername());
                dto.setBranchLevel(user.getGroupLevel());
            }
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("采集周期")
    @GetMapping(value="/cycle/getCyclePage")
    public Object cycleGetCyclePage(){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/cycle/getCyclePage/";
            Object result = this.nodeUtil.getBody(null, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("立即采集")
    @PostMapping(value="/doGather")
    public Object doGather(@RequestBody NodeDto dto) {

        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/topology/node/doGather.action/";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("下载模板")
    @GetMapping(value="/download-import-template")
    public Object download() {
        Object result = this.nodeUtil.getBody(null, "https://t14.baidu.com/it/u=2584240781,50873110&fm=224&app=112&f=JPEG?w=500&h=500&s=E9843472534072F055A8106F0200F063", null);
        return ResponseUtil.ok(result);
    }


    @ApiOperation("批量导入")
    @GetMapping(value="/batch-import-excel")
    public Object upload(@RequestParam(value = "multipartFile", required = false) MultipartFile file, String encrypt) throws IOException {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/batch-import-excel/";
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
                @Override
                public long contentLength() {
                    return file.getSize();
                }
            };
            MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
            multipartRequest.add("file", fileAsResource);
            multipartRequest.add("fileName",file.getName());
            multipartRequest.add("fileSize",file.getSize());
            multipartRequest.add("encrypt", encrypt);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + token);// 设置密钥
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(multipartRequest, headers);
            //发起调用
            Object obj =  restTemplate.postForObject(url, requestEntity, Object.class);
            return ResponseUtil.ok(obj);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("导入文件")
    @PostMapping(value = "/upload")
    public Object upload(@RequestParam(value = "file", required = false) MultipartFile file, NodeDto dto) throws IOException {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/upload.action/";
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
                @Override
                public long contentLength() {
                    return file.getSize();
                }
            };
            MultiValueMap<String, Object> multValueMap = new LinkedMultiValueMap<>();
            multValueMap.add("file", fileAsResource);
            Map<String, Object> map = new BeanMap(dto);
            for(String key : map.keySet()){
                multValueMap.set(key, map.get(key));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + token);// 设置密钥
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(multValueMap, headers);
            //发起调用
            Object obj =  restTemplate.postForObject(url, requestEntity, Object.class);
            return ResponseUtil.ok(obj);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("离线导入路由表")
    @PostMapping(value = "/uploadRouteTable")
    public Object uploadRouteTable(@RequestParam(value = "file", required = false) MultipartFile file, NodeDto dto) throws IOException {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/node/uploadRouteTable.action/";
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
                @Override
                public long contentLength() {
                    return file.getSize();
                }
            };
            MultiValueMap<String, Object> multValueMap = new LinkedMultiValueMap<>();
            multValueMap.add("file", fileAsResource);
            Map<String, Object> map = new BeanMap(dto);
            for(String key : map.keySet()){
                multValueMap.set(key, map.get(key));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + token);// 设置密钥
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(multValueMap, headers);
            //发起调用
            Object obj =  restTemplate.postForObject(url, requestEntity, Object.class);
            return ResponseUtil.ok(obj);
        }
        return ResponseUtil.error();
    }



}
