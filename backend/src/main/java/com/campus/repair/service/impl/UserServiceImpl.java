package com.campus.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.common.BusinessException;
import com.campus.repair.common.PageResult;
import com.campus.repair.dto.CreateUserDTO;
import com.campus.repair.entity.User;
import com.campus.repair.mapper.UserMapper;
import com.campus.repair.security.JwtTokenProvider;
import com.campus.repair.service.UserService;
import com.campus.repair.vo.LoginVO;
import com.campus.repair.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    private static final String[] ROLE_NAMES = {"学生", "维修工", "管理员"};

    @Override
    public LoginVO login(String username, String password) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        String token = tokenProvider.generateToken(username);
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUser(toVO(user));
        return vo;
    }

    @Override
    public UserVO getCurrentUser(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) throw new BusinessException("用户不存在");
        return toVO(user);
    }

    @Override
    public PageResult<UserVO> listUsers(Integer role, int page, int size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(role != null, User::getRole, role)
                .orderByAsc(User::getId);
        Page<User> pageResult = userMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.convert(this::toVO));
    }

    @Override
    public List<UserVO> listWorkers() {
        return userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .eq(User::getRole, 1)
                        .eq(User::getStatus, 1))
                .stream().map(this::toVO).toList();
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public UserVO createUser(CreateUserDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) throw new BusinessException("用户名已存在");
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setDepartment(dto.getDepartment());
        user.setStatus(1);
        userMapper.insert(user);
        return toVO(user);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setRole(user.getRole());
        Integer role = user.getRole();
        vo.setRoleName(role != null && role >= 0 && role < ROLE_NAMES.length
                ? ROLE_NAMES[role] : "未知");
        vo.setDepartment(user.getDepartment());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
