package com.cloud.tv.core.service;

import com.cloud.tv.entity.TopoNode;

public interface INodeService {

    TopoNode getObjByHostAddress(String hostAddress);
    int save(TopoNode instance);

    int update(TopoNode instance);
}
