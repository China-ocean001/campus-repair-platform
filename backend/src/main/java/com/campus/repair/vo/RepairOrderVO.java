package com.campus.repair.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RepairOrderVO {
    private Long id;
    private String orderNo;
    private Long studentId;
    private String studentName;
    private Long workerId;
    private String workerName;
    private Integer category;
    private String categoryName;
    private String location;
    private String description;
    private String images;
    private Integer status;
    private String statusName;
    private Integer priority;
    private String priorityName;
    private LocalDateTime assignTime;
    private LocalDateTime acceptTime;
    private LocalDateTime finishTime;
    private String remark;
    private LocalDateTime createTime;
    private Boolean hasEvaluation;
}
