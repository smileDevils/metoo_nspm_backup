package com.cloud.tv.core.service.impl;

import com.cloud.tv.core.mapper.GroupMapper;
import com.cloud.tv.core.service.IGroupService;
import com.cloud.tv.dto.GroupDto;
import com.cloud.tv.entity.Group;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class GroupServiceImpl implements IGroupService {

    @Resource
    private GroupMapper groupMapper;

    @Override
    public List<Group> query() {
        return null;
    }

    @Override
    public Group queryObjById(Long id) {
        return this.groupMapper.queryObjById(id);
    }

    @Override
    public List<Group> queryChild(Long id) {
        return this.groupMapper.queryChild(id);
    }

    @Override
    public boolean save(GroupDto instance) {
        Group group = null;
        if(instance.getId() == null){
            instance.setAddTime(new Date());
            group = new Group();
        }else{
            instance.setUpdateTime(new Date());
            group = this.groupMapper.queryObjById(instance.getId());
        }
        BeanUtils.copyProperties(instance, group);

        if(instance.getId() == null){
            if(instance.getParentId() != null){
                Group parent = this.queryObjById(instance.getParentId());
                if(parent != null){
                    List<Group> parents = this.queryChild(parent.getId());
                    int level = parents.size() + 1;
                    group.setLevel(parent.getLevel() + String.format("%02d", level));
                    group.setParentLevel(parent.getLevel());
                    group.setParentId(parent.getId());
                }
            }else{
                group.setParentLevel("0");
                List<Group> parents = this.queryChild(null);
                int level = parents.size() + 1;
                group.setLevel(String.format("%02d", level));
            }
            try {
                this.groupMapper.save(group);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }else{
            try {
                this.groupMapper.update(group);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean del(Long id) {
        try {
            this.groupMapper.del(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
