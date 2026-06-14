package com.campus.repair.controller;

import com.campus.repair.common.*;
import com.campus.repair.dto.*;
import com.campus.repair.service.RepairOrderService;
import com.campus.repair.service.UserService;
import com.campus.repair.vo.RepairOrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class RepairOrderController {

    private final RepairOrderService orderService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public Result<RepairOrderVO> create(@RequestBody @Valid RepairOrderCreateDTO dto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = userService.getCurrentUser(userDetails.getUsername()).getId();
        return Result.success(orderService.createOrder(studentId, dto));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<PageResult<RepairOrderVO>> myOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = userService.getCurrentUser(userDetails.getUsername()).getId();
        return Result.success(orderService.listMyOrders(studentId, status, page, size));
    }

    @GetMapping("/worker")
    @PreAuthorize("hasRole('WORKER')")
    public Result<PageResult<RepairOrderVO>> workerOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long workerId = userService.getCurrentUser(userDetails.getUsername()).getId();
        return Result.success(orderService.listWorkerOrders(workerId, status, page, size));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<RepairOrderVO>> allOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(orderService.listAllOrders(status, category, page, size));
    }

    @GetMapping("/{id}")
    public Result<RepairOrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> assign(@RequestBody @Valid AssignOrderDTO dto,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Long adminId = userService.getCurrentUser(userDetails.getUsername()).getId();
        orderService.assignOrder(dto, adminId);
        return Result.success();
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('WORKER')")
    public Result<Void> accept(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Long workerId = userService.getCurrentUser(userDetails.getUsername()).getId();
        orderService.acceptOrder(id, workerId);
        return Result.success();
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasRole('WORKER')")
    public Result<Void> finish(@PathVariable Long id,
                               @RequestParam(required = false) String remark,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Long workerId = userService.getCurrentUser(userDetails.getUsername()).getId();
        orderService.finishOrder(id, workerId, remark);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Long operatorId = userService.getCurrentUser(userDetails.getUsername()).getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        orderService.cancelOrder(id, operatorId, isAdmin);
        return Result.success();
    }

    @PostMapping("/evaluate")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Void> evaluate(@RequestBody @Valid EvaluationDTO dto,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = userService.getCurrentUser(userDetails.getUsername()).getId();
        orderService.evaluate(studentId, dto);
        return Result.success();
    }
}
