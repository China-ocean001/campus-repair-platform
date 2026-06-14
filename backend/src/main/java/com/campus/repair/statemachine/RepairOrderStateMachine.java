package com.campus.repair.statemachine;

import org.springframework.stereotype.Component;

/**
 * 工单状态机 — 负责校验状态转换合法性
 */
@Component
public class RepairOrderStateMachine {

    /**
     * 校验并返回是否允许转换，不抛异常
     */
    public boolean canTransit(int fromCode, int toCode) {
        try {
            OrderStatus from = OrderStatus.of(fromCode);
            OrderStatus to   = OrderStatus.of(toCode);
            return from.canTransitTo(to);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 强校验，不合法时抛出业务异常
     */
    public void assertTransit(int fromCode, int toCode) {
        if (!canTransit(fromCode, toCode)) {
            OrderStatus from = OrderStatus.of(fromCode);
            OrderStatus to   = OrderStatus.of(toCode);
            throw new com.campus.repair.common.BusinessException(
                    String.format("状态不可转换: [%s] → [%s]", from.desc, to.desc));
        }
    }
}
