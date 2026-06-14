package com.campus.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.common.BusinessException;
import com.campus.repair.common.PageResult;
import com.campus.repair.dto.*;
import com.campus.repair.entity.*;
import com.campus.repair.mapper.*;
import com.campus.repair.service.RepairOrderService;
import com.campus.repair.statemachine.OrderStatus;
import com.campus.repair.statemachine.RepairOrderStateMachine;
import com.campus.repair.vo.RepairOrderVO;
import com.campus.repair.websocket.NotifyWebSocketServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RepairOrderServiceImpl implements RepairOrderService {

    private final RepairOrderMapper orderMapper;
    private final OrderLogMapper logMapper;
    private final EvaluationMapper evaluationMapper;
    private final UserMapper userMapper;
    private final RepairOrderStateMachine stateMachine;

    private static final Map<Integer, String> CATEGORY_MAP = Map.of(
            1, "水电维修", 2, "网络故障", 3, "门窗维修", 4, "电器故障", 5, "其他");
    private static final Map<Integer, String> PRIORITY_MAP = Map.of(
            1, "普通", 2, "紧急", 3, "非常紧急");

    @Override
    @Transactional
    public RepairOrderVO createOrder(Long studentId, RepairOrderCreateDTO dto) {
        RepairOrder order = new RepairOrder();
        order.setOrderNo(generateOrderNo());
        order.setStudentId(studentId);
        order.setCategory(dto.getCategory());
        order.setLocation(dto.getLocation());
        order.setDescription(dto.getDescription());
        order.setImages(dto.getImages());
        order.setPriority(dto.getPriority() == null ? 1 : dto.getPriority());
        order.setStatus(OrderStatus.PENDING.code);
        orderMapper.insert(order);
        writeLog(order.getId(), null, OrderStatus.PENDING.code, studentId, "学生提交报修");
        return toVO(order);
    }

    @Override
    public PageResult<RepairOrderVO> listMyOrders(Long studentId, Integer status, int page, int size) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<RepairOrder>()
                .eq(RepairOrder::getStudentId, studentId)
                .eq(status != null, RepairOrder::getStatus, status)
                .orderByDesc(RepairOrder::getCreateTime);
        Page<RepairOrder> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.convert(this::toVO));
    }

    @Override
    public PageResult<RepairOrderVO> listAllOrders(Integer status, Integer category, int page, int size) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<RepairOrder>()
                .eq(status != null, RepairOrder::getStatus, status)
                .eq(category != null, RepairOrder::getCategory, category)
                .orderByDesc(RepairOrder::getPriority)
                .orderByDesc(RepairOrder::getCreateTime);
        Page<RepairOrder> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.convert(this::toVO));
    }

    @Override
    public PageResult<RepairOrderVO> listWorkerOrders(Long workerId, Integer status, int page, int size) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<RepairOrder>()
                .eq(RepairOrder::getWorkerId, workerId)
                .eq(status != null, RepairOrder::getStatus, status)
                .orderByDesc(RepairOrder::getCreateTime);
        Page<RepairOrder> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.convert(this::toVO));
    }

    @Override
    public RepairOrderVO getOrderDetail(Long orderId) {
        RepairOrder order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("工单不存在");
        return toVO(order);
    }

    @Override
    @Transactional
    public void assignOrder(AssignOrderDTO dto, Long adminId) {
        RepairOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) throw new BusinessException("工单不存在");
        stateMachine.assertTransit(order.getStatus(), OrderStatus.ASSIGNED.code);
        int from = order.getStatus();
        order.setWorkerId(dto.getWorkerId());
        order.setStatus(OrderStatus.ASSIGNED.code);
        order.setAssignTime(LocalDateTime.now());
        orderMapper.updateById(order);
        writeLog(order.getId(), from, OrderStatus.ASSIGNED.code, adminId, "管理员派单");
        pushNotify(dto.getWorkerId(), buildNotify("dispatch", order.getId(), "您有新的维修任务，请及时接单！"));
    }

    @Override
    @Transactional
    public void acceptOrder(Long orderId, Long workerId) {
        RepairOrder order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("工单不存在");
        if (!workerId.equals(order.getWorkerId())) throw new BusinessException("非本人工单");
        stateMachine.assertTransit(order.getStatus(), OrderStatus.PROCESSING.code);
        int from = order.getStatus();
        order.setStatus(OrderStatus.PROCESSING.code);
        order.setAcceptTime(LocalDateTime.now());
        orderMapper.updateById(order);
        writeLog(orderId, from, OrderStatus.PROCESSING.code, workerId, "维修工接单");
        pushNotify(order.getStudentId(), buildNotify("accept", orderId, "您的报修单已被接单，维修工正在处理中。"));
    }

    @Override
    @Transactional
    public void finishOrder(Long orderId, Long workerId, String remark) {
        RepairOrder order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("工单不存在");
        if (!workerId.equals(order.getWorkerId())) throw new BusinessException("非本人工单");
        stateMachine.assertTransit(order.getStatus(), OrderStatus.FINISHED.code);
        int from = order.getStatus();
        order.setStatus(OrderStatus.FINISHED.code);
        order.setFinishTime(LocalDateTime.now());
        order.setRemark(remark);
        orderMapper.updateById(order);
        writeLog(orderId, from, OrderStatus.FINISHED.code, workerId, "维修完成");
        pushNotify(order.getStudentId(), buildNotify("finish", orderId, "您的报修单已完成，请评价本次维修服务。"));
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long operatorId, boolean isAdmin) {
        RepairOrder order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("工单不存在");
        if (!isAdmin && !operatorId.equals(order.getStudentId())) {
            throw new BusinessException("无权取消他人工单");
        }
        stateMachine.assertTransit(order.getStatus(), OrderStatus.CANCELLED.code);
        int from = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED.code);
        orderMapper.updateById(order);
        writeLog(orderId, from, OrderStatus.CANCELLED.code, operatorId, "取消工单");
    }

    @Override
    @Transactional
    public void evaluate(Long studentId, EvaluationDTO dto) {
        RepairOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) throw new BusinessException("工单不存在");
        if (!studentId.equals(order.getStudentId())) throw new BusinessException("非本人工单");
        if (order.getStatus() != OrderStatus.FINISHED.code) throw new BusinessException("工单尚未完成");
        Long count = evaluationMapper.selectCount(
                new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getOrderId, dto.getOrderId()));
        if (count > 0) throw new BusinessException("已评价过该工单");
        Evaluation evaluation = new Evaluation();
        evaluation.setOrderId(dto.getOrderId());
        evaluation.setStudentId(studentId);
        evaluation.setWorkerId(order.getWorkerId());
        evaluation.setScore(dto.getScore());
        evaluation.setContent(dto.getContent());
        evaluationMapper.insert(evaluation);
        order.setStatus(OrderStatus.EVALUATED.code);
        orderMapper.updateById(order);
        writeLog(dto.getOrderId(), OrderStatus.FINISHED.code, OrderStatus.EVALUATED.code, studentId, "学生评价");
    }

    // ---- 私有辅助方法 ----

    private void writeLog(Long orderId, Integer from, int to, Long operatorId, String remark) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setFromStatus(from);
        log.setToStatus(to);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        logMapper.insert(log);
    }

    private String generateOrderNo() {
        return "RP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int)(Math.random() * 10000));
    }

    @SneakyThrows
    private String buildNotify(String type, Long orderId, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("orderId", orderId);
        map.put("message", message);
        return new ObjectMapper().writeValueAsString(map);
    }

    private void pushNotify(Long userId, String message) {
        NotifyWebSocketServer.sendToUser(userId, message);
    }

    private RepairOrderVO toVO(RepairOrder order) {
        RepairOrderVO vo = new RepairOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setStudentId(order.getStudentId());
        vo.setWorkerId(order.getWorkerId());
        vo.setCategory(order.getCategory());
        vo.setCategoryName(CATEGORY_MAP.getOrDefault(order.getCategory(), "未知"));
        vo.setLocation(order.getLocation());
        vo.setDescription(order.getDescription());
        vo.setImages(order.getImages());
        vo.setStatus(order.getStatus());
        vo.setStatusName(OrderStatus.of(order.getStatus()).desc);
        vo.setPriority(order.getPriority());
        vo.setPriorityName(PRIORITY_MAP.getOrDefault(order.getPriority(), "普通"));
        vo.setAssignTime(order.getAssignTime());
        vo.setAcceptTime(order.getAcceptTime());
        vo.setFinishTime(order.getFinishTime());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        // 关联用户名
        if (order.getStudentId() != null) {
            User student = userMapper.selectById(order.getStudentId());
            if (student != null) vo.setStudentName(student.getRealName());
        }
        if (order.getWorkerId() != null) {
            User worker = userMapper.selectById(order.getWorkerId());
            if (worker != null) vo.setWorkerName(worker.getRealName());
        }
        // 是否已评价
        long evalCount = evaluationMapper.selectCount(
                new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getOrderId, order.getId()));
        vo.setHasEvaluation(evalCount > 0);
        return vo;
    }
}
