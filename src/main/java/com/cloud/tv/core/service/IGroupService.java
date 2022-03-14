package com.cloud.tv.core.service;

import com.cloud.tv.dto.GroupDto;
import com.cloud.tv.entity.Group;

import java.util.List;

public interface IGroupService {



    // 查询所有组
    List<Group> query();

    Group queryObjById(Long id);

    // 获取子集
    List<Group> queryChild(Long id);


    boolean save(GroupDto instance);

    boolean del(Long id);

}
