package com.campus.repair.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private Integer role;
    private String roleName;
    private String department;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
}
