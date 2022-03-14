package com.cloud.tv.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class PolicyDto implements Serializable {

    private String id;
    private Integer currentPage;
    private Integer pageSize;
    private String vendor;
    private String type;
    private String deviceUuid;
    private String layerUuid;
    private String isFilter;
    private String policyListUuid;
    private String filter;
    private String zoneUuid;
    private String objectType;
    private String ruleUuidList;
    private String skip;
    private String name;
    private String nodeIp;
    private String policyId;
    private String policyName;
    private String ruleListName;
    private String policyType;
    private String createUser;
    private String remarks;
    private String expireTime;
    private String ruleField;
    private String ruleUuid;
    private List<Object> tasks;
    private String vendorName;
    private Integer page;
    private Integer limit;
    private String ipRanges;
    private String typeStatus;
    private String devId;
    private String typeId;
    private String logCenterEnabled;
    private String logCenterIp;
    private String logCenterUniqueName;
    private String haEnabled;
    private String timeType;
    private String startTime;
    private String endTime;
    private String policyIds;
    private String policyUuid;
    private String flag;
    private String taskName;
    private String devName;
    private String inbound;
    private String srcIp;
    private String dstIp;
    private String filterService;
    private String filterSrcIp;
    private String filterDstIp;
    private String isGather;
    private String gatherObj;
    private String gatherMaskBit;
    private String isInternet;
    private String taskIds;
    private Integer taskId;
    private String slaveDeviceUuid;
    private String masterDeviceUuid;
    private String jsonQuery;
    private String content;
    private String searchType;
    private Integer dstPortStart;
    private Integer dstPortEnd;
    private Integer protocol;
    private String userName;

}
