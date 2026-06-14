package com.campus.repair.statemachine;

/**
 * 工单状态枚举
 * 状态流转：待派单(0) → 已派单(1) → 处理中(2) → 已完成(3) → 已评价(4)
 *          任意状态 → 已取消(5)（管理员操作）
 */
public enum OrderStatus {
    PENDING(0, "待派单"),
    ASSIGNED(1, "已派单"),
    PROCESSING(2, "处理中"),
    FINISHED(3, "已完成"),
    EVALUATED(4, "已评价"),
    CANCELLED(5, "已取消");

    public final int code;
    public final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatus of(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("未知状态码: " + code);
    }

    public boolean canTransitTo(OrderStatus target) {
        return switch (this) {
            case PENDING    -> target == ASSIGNED  || target == CANCELLED;
            case ASSIGNED   -> target == PROCESSING || target == CANCELLED;
            case PROCESSING -> target == FINISHED  || target == CANCELLED;
            case FINISHED   -> target == EVALUATED;
            default         -> false;
        };
    }
}
