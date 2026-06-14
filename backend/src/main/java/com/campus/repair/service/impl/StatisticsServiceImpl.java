package com.campus.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.repair.entity.Evaluation;
import com.campus.repair.entity.RepairOrder;
import com.campus.repair.mapper.EvaluationMapper;
import com.campus.repair.mapper.RepairOrderMapper;
import com.campus.repair.service.StatisticsService;
import com.campus.repair.statemachine.OrderStatus;
import com.campus.repair.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final RepairOrderMapper orderMapper;
    private final EvaluationMapper evaluationMapper;

    @Override
    public StatisticsVO getOverview() {
        StatisticsVO vo = new StatisticsVO();
        vo.setTotalOrders(orderMapper.selectCount(null));
        vo.setPendingOrders(orderMapper.selectCount(
                new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, OrderStatus.PENDING.code)));
        vo.setProcessingOrders(orderMapper.selectCount(
                new LambdaQueryWrapper<RepairOrder>()
                        .in(RepairOrder::getStatus, OrderStatus.ASSIGNED.code, OrderStatus.PROCESSING.code)));
        vo.setFinishedOrders(orderMapper.selectCount(
                new LambdaQueryWrapper<RepairOrder>()
                        .in(RepairOrder::getStatus, OrderStatus.FINISHED.code, OrderStatus.EVALUATED.code)));
        vo.setCancelledOrders(orderMapper.selectCount(
                new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, OrderStatus.CANCELLED.code)));
        // 今日工单
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        vo.setTodayOrders(orderMapper.selectCount(
                new LambdaQueryWrapper<RepairOrder>().ge(RepairOrder::getCreateTime, todayStart)));
        // 平均评分
        List<Evaluation> evaluations = evaluationMapper.selectList(null);
        double avg = evaluations.stream().mapToInt(Evaluation::getScore).average().orElse(0.0);
        vo.setAvgScore(Math.round(avg * 10.0) / 10.0);
        return vo;
    }
}
