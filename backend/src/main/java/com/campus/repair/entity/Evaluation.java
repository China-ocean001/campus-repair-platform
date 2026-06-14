package com.campus.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_evaluation")
public class Evaluation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long studentId;
    private Long workerId;
    private Integer score;
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
