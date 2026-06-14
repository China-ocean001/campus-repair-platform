package com.campus.repair.controller;

import com.campus.repair.common.*;
import com.campus.repair.dto.CreateUserDTO;
import com.campus.repair.service.UserService;
import com.campus.repair.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<UserVO> me(@AuthenticationPrincipal UserDetails userDetails) {
        return Result.success(userService.getCurrentUser(userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<UserVO>> list(
            @RequestParam(required = false) Integer role,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(userService.listUsers(role, page, size));
    }

    @GetMapping("/workers")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<UserVO>> workers() {
        return Result.success(userService.listWorkers());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserVO> create(@RequestBody @Valid CreateUserDTO dto) {
        return Result.success(userService.createUser(dto));
    }
}
