package com.campus.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_repair_order")
public class RepairOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long studentId;
    private Long workerId;
    /** 1水电 2网络 3门窗 4电器 5其他 */
    private Integer category;
    private String location;
    private String description;
    private String images;
    /** 0待派单 1已派单 2处理中 3已完成 4已评价 5已取消 */
    private Integer status;
    /** 1普通 2紧急 3非常紧急 */
    private Integer priority;
    private LocalDateTime assignTime;
    private LocalDateTime acceptTime;
    private LocalDateTime finishTime;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
