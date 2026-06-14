package com.campus.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.repair.common.BusinessException;
import com.campus.repair.dto.NoticeDTO;
import com.campus.repair.entity.Notice;
import com.campus.repair.mapper.NoticeMapper;
import com.campus.repair.service.NoticeService;
import com.campus.repair.vo.NoticeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public List<NoticeVO> listPublished() {
        return noticeMapper.selectList(
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getStatus, 1)
                        .orderByDesc(Notice::getCreateTime))
                .stream().map(this::toVO).toList();
    }

    @Override
    public List<NoticeVO> listAll() {
        return noticeMapper.selectList(
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime))
                .stream().map(this::toVO).toList();
    }

    @Override
    public NoticeVO create(Long adminId, NoticeDTO dto) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setAdminId(adminId);
        notice.setStatus(1);
        noticeMapper.insert(notice);
        return toVO(notice);
    }

    @Override
    public void toggleStatus(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        notice.setStatus(notice.getStatus() == 1 ? 0 : 1);
        noticeMapper.updateById(notice);
    }

    @Override
    public void delete(Long id) {
        if (noticeMapper.selectById(id) == null) throw new BusinessException("公告不存在");
        noticeMapper.deleteById(id);
    }

    private NoticeVO toVO(Notice notice) {
        NoticeVO vo = new NoticeVO();
        vo.setId(notice.getId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setStatus(notice.getStatus());
        vo.setCreateTime(notice.getCreateTime());
        return vo;
    }
}
