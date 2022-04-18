package com.cloud.tv.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("采集周期DTO")
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class CycleDto {

    private Integer id;
    private Integer page;
    private Integer psize;

    private String cycleName;
    private String type;
    private Integer cycleType;
    private String day;
    private String hour;
    private List updateCycleDTOS;
}
