package com.campus.repair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepairOrderCreateDTO {
    @NotNull(message = "报修类别不能为空")
    private Integer category;
    @NotBlank(message = "报修地点不能为空")
    private String location;
    @NotBlank(message = "故障描述不能为空")
    private String description;
    private String images;
    private Integer priority = 1;
}
