package com.campus.repair.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeVO {
    private Long id;
    private String title;
    private String content;
    private Integer status;
    private LocalDateTime createTime;
}
