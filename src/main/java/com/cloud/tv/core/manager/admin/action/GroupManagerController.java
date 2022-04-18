package com.cloud.tv.core.manager.admin.action;

import com.cloud.tv.core.manager.admin.tools.GroupTools;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.service.IGroupService;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.GroupDto;
import com.cloud.tv.entity.Group;
import com.cloud.tv.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/admin/group")
@RestController
public class GroupManagerController {

    @Autowired
    private IGroupService groupService;
    @Autowired
    private GroupTools groupTools;
    @Autowired
    private IUserService userService;

//    @RequestMapping("/list")
//    @ResponseBody
//    public Object list(@RequestBody(required = false) GroupDto dto){
//        List<Group> parent = this.groupService.queryChild(null);
//        if(parent.size() > 0){
//            List<Group> branchList = new ArrayList<Group>();
//            for(Group obj : parent){
//                if(this.groupTools.genericGroup(obj).size() > 0){
//                    this.groupTools.genericGroup(obj);
//                }
//                branchList.add(obj);
//            }
//            return ResponseUtil.ok(branchList);
//        }
//        return ResponseUtil.ok();
//    }



    @RequestMapping("/list")
    @ResponseBody
    public Object list(@RequestBody(required = false) GroupDto dto){
        User currentUser = ShiroUserHolder.currentUser();
        User user = this.userService.findByUserName(currentUser.getUsername());
        Group parent = this.groupService.queryObjById(user.getGroupId());
        if(parent != null){
            List<Group> branchList = new ArrayList<Group>();
            if(this.groupTools.genericGroup(parent).size() > 0){
                this.groupTools.genericGroup(parent);
            }
            branchList.add(parent);
            return ResponseUtil.ok(branchList);
        }
        return ResponseUtil.ok();
    }

    @PostMapping("/save")
    public Object save(@RequestBody GroupDto dto){
        if(dto.getBranchName() != null){
            return ResponseUtil.ok(this.groupService.save(dto));
        }
        return ResponseUtil.badArgument("请输入组名称");
    }


    @RequestMapping("/del")
    public Object del(@RequestBody GroupDto dto){
        List<Group> groupList = this.groupService.queryChild(dto.getId());
        if(groupList.size() <= 0){
            if(this.groupService.del(dto.getId())){
                return ResponseUtil.ok();
            }
            return ResponseUtil.error();
        }
        return ResponseUtil.badArgument("优先删除下级组织");
    }

    @RequestMapping("/parent")
    @ResponseBody
    public Object queryParent(@RequestBody(required = false) GroupDto dto){
        if(dto != null){
            List<Group> parent = this.groupService.queryChild(dto.getParentId());
            return ResponseUtil.ok(parent);
        }else{
            List<Group> parent = this.groupService.queryChild(null);
            return ResponseUtil.ok(parent);
        }
    }




}
