package com.cloud.tv.core.manager.integrated.node;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.NodeDto;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Api("拓扑管理")
@RequestMapping("/nspm/topology")
@RestController
public class TopologyManagerController {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private IUserService userService;



    @ApiOperation("图层列表")
    @RequestMapping(value="/topology-layer/layerInfo/GET/listLayers")
    public Object listLayers(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            if(user.getUsername().equals("testadmin")){
                url = url + "topology-layer/layerInfo/GET/listLayers";
                dto.setBranchLevel("00");
                token = sysConfig.getTestToken();
                Object result = this.nodeUtil.getBody(dto, url, token);
                JSONObject results = JSONObject.parseObject(result.toString());
                JSONArray arrays = JSONArray.parseArray(results.get("rows").toString());
                List list = new ArrayList();
                for(Object array : arrays){
                    JSONObject obj = JSONObject.parseObject(array.toString());
                    String layerName = obj.get("layerName").toString();
                    if(layerName.indexOf("guest2") >= 0){
                        list.add(obj);
                    }
                }
                results.put("rows", list);
                return ResponseUtil.ok(results);
            }else{
                url = url + "topology-layer/layerInfo/GET/listLayers";
                dto.setBranchLevel(user.getGroupLevel());
                Object result = this.nodeUtil.getBody(dto, url, token);
                JSONObject results = JSONObject.parseObject(result.toString());
                // 检测用户
                List<String> users = this.userService.getObjByLevel(dto.getBranchLevel());
                if(users == null || users.size() <= 0){
                    return ResponseUtil.ok();
                }
                JSONArray arrays = JSONArray.parseArray(results.get("rows").toString());
                List list = new ArrayList();
                for(Object array : arrays){
                    JSONObject obj = JSONObject.parseObject(array.toString());
                    String userName = obj.get("layerDesc").toString();
                    if(users.contains(userName)){
                        obj.put("userName", userName);
                        list.add(obj);
                    }
                }
                results.put("rows", list);
                return ResponseUtil.ok(results);
            }
        }
        return ResponseUtil.error();
    }



    @ApiOperation("默认图层")
    @RequestMapping(value="/topology-layer/layerInfo/GET/defaultLayer")
    public Object defaultLayer(){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "topology-layer/layerInfo/GET/defaultLayer";
            Object result = this.nodeUtil.getBody(null, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("新建画布")
    @RequestMapping("/topology-layer/layerInfo/POST/saveLayer")
    public Object saveLayer(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userService.findByUserName(currentUser.getUsername());
            dto.setDesc(user.getUsername());
            url = url + "/topology-layer/layerInfo/POST/saveLayer";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("删除画布")
    @RequestMapping("/topology-layer/layerInfo/DELETE/layers")
    public Object DELETE(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/layerInfo/DELETE/layers";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("设置默认图层")
    @RequestMapping("/topology-layer/layerInfo/PUT/defaultLayer")
    public Object defaultLayer(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/layerInfo/PUT/defaultLayer";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("拓扑详情")
    @RequestMapping("/topology-layer/layerInfo/GET/getLayerByUuid")
    public Object getLayerByUuid(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/layerInfo/GET/getLayerByUuid";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("复制图层")
    @RequestMapping("/topology-layer/layerInfo/POST/copyLayer")
    public Object copyLayer(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/layerInfo/POST/copyLayer";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("移动")
    @RequestMapping("/topology-layer/layerInfo/POST/editLayerBranch")
    public Object editLayerBranch(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/layerInfo/POST/editLayerBranch";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("子网")
    @RequestMapping("/topology-layer/whale/GET/subnets")
    public Object subnets(NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/GET/subnets";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("关联设备")
    @GetMapping("/topology-layer/whale/GET/subnet/linkedDevice")
    public Object linkedDevice(NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/GET/subnet/linkedDevice";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("子网拆分（二层设备）记录")
    @RequestMapping("/topology-layer/whale/POST/topo/action/all-split-subnet-summary")
    public Object subnetSimmary(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/POST/topo/action/all-split-subnet-summary";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("撤销（设备接入或子网拆分）")
    @RequestMapping("/topology-layer/whale/PUT/topo/action/undo/splitSubnet")
    public Object splitSubnet(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/PUT/topo/action/undo/splitSubnet";
            Object result = this.nodeUtil.putFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("VPN")
    @RequestMapping("/topology-layer/whale/GET/topo/action/linkVpn")
    public Object linkVpn(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/GET/topo/action/linkVpn";
            Object result = this.nodeUtil.getBody(null, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("VPN-选择设备")
    @RequestMapping("/topology-layer/whale/GET/devices/summary")
    public Object devicesSummary(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/GET/devices/summary";
            Object result = this.nodeUtil.getBody(null, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("VPN-选择设备-接口")
    @RequestMapping("/topology-layer/whale/GET/vpn/subnet")
    public Object vpnSubnet(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/GET/vpn/subnet";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("VPN-保存")
    @RequestMapping("/topology-layer/whale/PUT/topo/action/linkVpn")
    public Object putLingVpn(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/PUT/topo/action/linkVpn";
            Object result = this.nodeUtil.putBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("撤销")
    @RequestMapping("/topology-layer/whale/DELETE/topo/action/linkVpn")
    public Object deleteLinkVpn(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/DELETE/topo/action/linkVpn";
            Object result = this.nodeUtil.deleteBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("更新")
    @RequestMapping("/topology-layer/layerInfo/POST/updateLayerStatus")
    public Object updateLayerStatus(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/layerInfo/POST/updateLayerStatus";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("路径")
    @RequestMapping("/topology/queryRoutesByLayerUuid.action")
    public Object queryRoutesByLayerUuid(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/queryRoutesByLayerUuid.action";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("路径备份")
    @RequestMapping("/topology/addRoute.action")
    public Object addRoute(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology/addRoute.action";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }
    @ApiOperation("路径细节")
    @RequestMapping("/topology-layer/whale/GET/detailedPath/run")
    public Object run(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/GET/detailedPath/run";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("关联子网")
    @PostMapping("/topology-layer/whale/GET/device/subnets")
    public Object deviceSubnets(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/GET/device/subnets";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("所属逻辑域")
    @PostMapping("/risk/api/alarm/zone/listLogicZoneAndSubnets")
    public Object listLogicZoneAndSubnets(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/risk/api/alarm/zone/listLogicZoneAndSubnets";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("防火墙安全域")
    @RequestMapping("/topology-layer/whale/GET/device/zones")
    public Object zones(NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/topology-layer/whale/GET/device/zones";
            Object result = this.nodeUtil.getBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("关联主机")
    @PostMapping("/risk/api/danger/hostComputerSoftware/hostComputerList")
    public Object hostComputerList(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/risk/api/danger/hostComputerSoftware/hostComputerList";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("业务区域树")
    @PostMapping("/risk/api/danger/businessZone/businessZoneTree")
    public Object businessZoneTree(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/risk/api/danger/businessZone/businessZoneTree";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("资产组列表")
    @PostMapping("/risk/api/danger/hostComputerSoftware/assetGroupList")
    public Object assetGroupList(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/risk/api/danger/hostComputerSoftware/assetGroupList";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("关联子网")
    @PostMapping("/risk/api/danger/assetHost/getSubnetByAssetGroup")
    public Object getSubnetByAssetGroup(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/risk/api/danger/assetHost/getSubnetByAssetGroup";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }

    @ApiOperation("主机列表")
    @PostMapping("/risk/api/danger/assetHost/pageList")
    public Object pageList(@RequestBody(required = false) NodeDto dto){
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if(url != null && token != null){
            url = url + "/risk/api/danger/assetHost/pageList";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.error();
    }
}
