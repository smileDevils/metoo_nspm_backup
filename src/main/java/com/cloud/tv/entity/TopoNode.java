package com.cloud.tv.entity;

import com.cloud.tv.core.domain.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class TopoNode extends IdEntity {

    private String branchLevel;
    private Long branchId;
    private String branchName;
    private String ip;
    private String deviceName;
    private String vendorName;
    private Integer type;
    private Integer origin;
    private String gatherTime;
    private String skipAnalysis;
    private String toSameInbound;
    private String layerTwoDevice;
    private String description;
    private Integer state;
    private String uuid;
    private String vendorId;
    private Integer version;
    private String errorMess;
    private String stateDesc;
    private String charset;
    private Integer credentialUuid;
    private String pushCredentialUuid;
    private String taskType;
    private String taskUuid;
    private Integer timeout;
}
