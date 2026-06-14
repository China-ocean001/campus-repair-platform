package com.campus.repair.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignOrderDTO {
    @NotNull
    private Long orderId;
    @NotNull
    private Long workerId;
}
