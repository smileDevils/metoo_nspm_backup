package com.cloud.tv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class OperationDto {

    private Integer page;
    private Integer psize;
    private String userName;
    private Integer taskId;
    private String ids;
    private Integer pushStatus;
    private String action;
    private String beforeConflict;
    private String description;
    private String deviceUuid;
    private String dstIp;
    private String dstIpSystem;
    private String dstZone;
    private Integer inDevIf;
    private String inDevItfAlias;
    private String ipType;
    private String mergeCheck;
    private String outDevIf;
    private String outDevItfAlias;
    private String rangeFilter;
    private List serviceList;
    private String srcIp;
    private String srcIpSystem;
    private String srcZone;
    private String startTime;
    private String endTime;
    private String theme;
    private Integer type;
    private String isTrusted;
    private String isReload;
}
