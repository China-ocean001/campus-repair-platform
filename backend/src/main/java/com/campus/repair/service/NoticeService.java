package com.campus.repair.service;

import com.campus.repair.dto.NoticeDTO;
import com.campus.repair.vo.NoticeVO;

import java.util.List;

public interface NoticeService {
    List<NoticeVO> listPublished();
    List<NoticeVO> listAll();
    NoticeVO create(Long adminId, NoticeDTO dto);
    void toggleStatus(Long id);
    void delete(Long id);
}
