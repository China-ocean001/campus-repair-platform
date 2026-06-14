package com.campus.repair.controller;

import com.campus.repair.common.Result;
import com.campus.repair.dto.NoticeDTO;
import com.campus.repair.service.NoticeService;
import com.campus.repair.service.UserService;
import com.campus.repair.vo.NoticeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final UserService userService;

    /** 公开接口：获取已发布公告（登录页使用） */
    @GetMapping
    public Result<List<NoticeVO>> list() {
        return Result.success(noticeService.listPublished());
    }

    /** 管理员：获取全部公告（含已下架） */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<NoticeVO>> listAll() {
        return Result.success(noticeService.listAll());
    }

    /** 管理员：发布新公告 */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<NoticeVO> create(@RequestBody @Valid NoticeDTO dto,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        Long adminId = userService.getCurrentUser(userDetails.getUsername()).getId();
        return Result.success(noticeService.create(adminId, dto));
    }

    /** 管理员：切换公告状态（发布/下架） */
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> toggle(@PathVariable Long id) {
        noticeService.toggleStatus(id);
        return Result.success();
    }

    /** 管理员：删除公告 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success();
    }
}
