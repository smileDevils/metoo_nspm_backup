package com.cloud.tv.core.mapper;

import com.cloud.tv.entity.Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper {

    List<Group> query();

    Group queryObjById(Long id);

    Group getObjByLevel(String level);

    List<Group> queryChild(Long id);

    boolean save(Group instance);

    boolean update(Group instance);

    boolean del(Long id);
}
