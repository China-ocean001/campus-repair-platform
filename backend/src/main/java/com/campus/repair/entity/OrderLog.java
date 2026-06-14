package com.campus.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_order_log")
public class OrderLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Integer fromStatus;
    private Integer toStatus;
    private Long operatorId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
