package com.cloud.tv.core.service;

import com.cloud.tv.dto.RoomProgramDto;
import com.cloud.tv.entity.RoomProgram;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface IRoomProgramService {

    RoomProgram findObjById(Long id);

    List<RoomProgram> getRoomProgram(Map<String, Object> params);

    int getAccountByTotal();

    Page<RoomProgram> query(RoomProgramDto dto);

    Object save(RoomProgram instance);

    boolean  save(RoomProgramDto instance);

    boolean update(RoomProgram instance);

    int delete(Long id);

    Object programLiveRoom(RoomProgram instance);

    List<RoomProgram> findObjByCondition(Map<String, Object> params);

    List<RoomProgram> findRoomProgramByLiveRoomId(Map<String, Object> params);

    List<RoomProgram> liveStatus(Map<String, Object> params);

 }
