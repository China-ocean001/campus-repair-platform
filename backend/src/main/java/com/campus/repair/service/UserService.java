package com.campus.repair.service;

import com.campus.repair.common.PageResult;
import com.campus.repair.dto.CreateUserDTO;
import com.campus.repair.vo.LoginVO;
import com.campus.repair.vo.UserVO;

import java.util.List;

public interface UserService {
    LoginVO login(String username, String password);
    UserVO getCurrentUser(String username);
    PageResult<UserVO> listUsers(Integer role, int page, int size);
    List<UserVO> listWorkers();
    void updateUserStatus(Long userId, Integer status);
    UserVO createUser(CreateUserDTO dto);
}
