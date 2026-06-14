package com.campus.repair.service;

import com.campus.repair.common.PageResult;
import com.campus.repair.dto.*;
import com.campus.repair.vo.*;

public interface RepairOrderService {
    RepairOrderVO createOrder(Long studentId, RepairOrderCreateDTO dto);
    PageResult<RepairOrderVO> listMyOrders(Long studentId, Integer status, int page, int size);
    PageResult<RepairOrderVO> listAllOrders(Integer status, Integer category, int page, int size);
    PageResult<RepairOrderVO> listWorkerOrders(Long workerId, Integer status, int page, int size);
    RepairOrderVO getOrderDetail(Long orderId);
    void assignOrder(AssignOrderDTO dto, Long adminId);
    void acceptOrder(Long orderId, Long workerId);
    void finishOrder(Long orderId, Long workerId, String remark);
    void cancelOrder(Long orderId, Long operatorId, boolean isAdmin);
    void evaluate(Long studentId, EvaluationDTO dto);
}
