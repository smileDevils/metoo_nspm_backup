package com.cloud.tv.core.service;

import com.cloud.tv.dto.CredentialDto;
import com.cloud.tv.dto.UserDto;
import com.cloud.tv.entity.Credential;
import com.cloud.tv.vo.UserVo;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface ICredentialService {

    Credential getObjById(Long id);

    Credential getObjByName(String name);

    List<Credential> query();

    int save(Credential instance);

    int update(Credential instance);

    int delete(Long id);

    Map<String, String> getUuid(CredentialDto dto);

    Page<Credential> getObjsByLevel(Credential instance);


}
