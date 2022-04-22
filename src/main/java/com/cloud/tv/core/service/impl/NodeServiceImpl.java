package com.cloud.tv.core.service.impl;

import com.cloud.tv.core.mapper.NodeMapper;
import com.cloud.tv.core.service.INodeService;
import com.cloud.tv.entity.TopoNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class NodeServiceImpl implements INodeService {

    @Autowired
    private NodeMapper nodeMapper;

    @Override
    public TopoNode getObjByHostAddress(String hostAddress) {
        return this.nodeMapper.getObjByHostAddress(hostAddress);
    }

    @Override
    public int save(TopoNode instance) {
        return this.nodeMapper.save(instance);
    }

    @Override
    public int update(TopoNode instance) {
        return this.nodeMapper.update(instance);
    }
}
