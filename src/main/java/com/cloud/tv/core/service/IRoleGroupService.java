package com.cloud.tv.core.service;

import com.github.pagehelper.Page;
import com.cloud.tv.dto.RoleGroupDto;
import com.cloud.tv.entity.RoleGroup;

import java.util.List;
import java.util.Map;

public interface IRoleGroupService {

    RoleGroup getObjById(Long id);

    boolean checkExist(String name);

    Page<RoleGroup> query(Map<String, Integer> params);

    RoleGroup change(Long id);

    boolean save(RoleGroupDto instance);

    Object update(RoleGroup instance);

    int delete(Long id);

    List<RoleGroup> roleUnitGroup(Map<String, Integer> params);

    List<RoleGroup> selectByPrimaryType(String type);

}
