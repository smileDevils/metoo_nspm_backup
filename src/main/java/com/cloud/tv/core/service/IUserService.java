package com.cloud.tv.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.manager.integrated.policy.PolicyIntegrateController;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.PolicyDto;
import com.cloud.tv.dto.UserDto;
import com.cloud.tv.entity.Group;
import com.cloud.tv.entity.Policy;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.User;
import com.cloud.tv.vo.UserVo;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface IUserService {

    /**
     * 根据Username 查询一个User 对象
     * @param username
     * @return
     */
    User findByUserName(String username);

    User findRolesByUserName(String username);

    UserVo findUserUpdate(Long id);

    User findObjById(Long id);

    Page<UserVo> getObjsByLevel(UserDto dto);

    List<String> getObjByLevel(String level);

    Page<UserVo> query(UserDto dto);

    boolean save(UserDto dto);

    boolean update(User user);

    boolean delete(User id);

    boolean allocation(List<User> list);

    List<User> findObjByIds(Long[] ids) ;

    
}
