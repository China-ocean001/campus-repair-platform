package com.campus.repair.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EvaluationDTO {
    @NotNull
    private Long orderId;
    @NotNull @Min(1) @Max(5)
    private Integer score;
    private String content;
}
