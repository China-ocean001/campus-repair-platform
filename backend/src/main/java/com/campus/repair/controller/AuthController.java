package com.campus.repair.controller;

import com.campus.repair.common.Result;
import com.campus.repair.dto.LoginDTO;
import com.campus.repair.service.UserService;
import com.campus.repair.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(userService.login(dto.getUsername(), dto.getPassword()));
    }
}
