package com.campus.repair.controller;

import com.campus.repair.common.Result;
import com.campus.repair.service.StatisticsService;
import com.campus.repair.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<StatisticsVO> overview() {
        return Result.success(statisticsService.getOverview());
    }
}
