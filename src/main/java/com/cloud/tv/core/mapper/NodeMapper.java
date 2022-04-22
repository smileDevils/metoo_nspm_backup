package com.cloud.tv.core.mapper;

import com.cloud.tv.entity.TopoNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NodeMapper {

    TopoNode getObjByHostAddress(String hostAddress);

    int save(TopoNode instance);

    int update(TopoNode instance);
}
