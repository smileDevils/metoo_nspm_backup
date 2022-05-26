package com.cloud.tv.core.service.impl;

import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.service.ITopologyTokenService;
import com.cloud.tv.core.topology.mapper.TopologyTokenMapper;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.TopologyToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TopologyTokenServiceImpl implements ITopologyTokenService {

    @Autowired
    private TopologyTokenMapper topologyTokenMapper;
    @Autowired
    private ISysConfigService sysConfigService;

    public void initToken(){
        Map map = new HashMap();
        map.put("type", "client_credentials");
        List<TopologyToken> TopologyTokens = this.topologyTokenMapper.query(map);
        if(TopologyTokens.size() > 0){
            TopologyToken topologyToken = TopologyTokens.get(0);
            // updateToken
            SysConfig sysConfigs = this.sysConfigService.findSysConfigList();
            SysConfig sysConfig = new SysConfig();
            sysConfig.setNspmToken(topologyToken.getToken_value());
            sysConfig.setId(sysConfigs.getId());
            this.sysConfigService.update(sysConfig);
        }
    }
}
