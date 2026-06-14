package com.campus.repair.vo;

import lombok.Data;

@Data
public class StatisticsVO {
    private long totalOrders;
    private long pendingOrders;
    private long processingOrders;
    private long finishedOrders;
    private long cancelledOrders;
    private double avgScore;
    private long todayOrders;
}
