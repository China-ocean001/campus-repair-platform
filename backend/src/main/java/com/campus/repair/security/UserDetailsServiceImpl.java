package com.campus.repair.security;

import com.campus.repair.entity.User;
import com.campus.repair.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        String role = switch (user.getRole()) {
            case 1  -> "ROLE_WORKER";
            case 2  -> "ROLE_ADMIN";
            default -> "ROLE_STUDENT";
        };
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(role))
                .disabled(user.getStatus() == 0)
                .build();
    }
}
