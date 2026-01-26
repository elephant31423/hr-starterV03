package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.PermissionTreeDTO;
import com.example.hrstarter.dto.UserDetailDTO;
import com.example.hrstarter.entity.User;
import com.example.hrstarter.mapper.RoleMapper;
import com.example.hrstarter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final RoleMapper roleMapper;
    private final UserMapper userMapper;

    public UserDetailServiceImpl(  RoleMapper roleMapper, UserMapper userMapper) {
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("加載用戶詳情: {}", username);
        try {
            // 1. 查詢用戶
            Optional<User> userOpt = Optional.ofNullable(userMapper.findByUsername(username));

            // 2. 檢查用戶是否存在
            if (userOpt.isEmpty()) {
                log.warn("用戶不存在: {}", username);
                throw new UsernameNotFoundException("用戶不存在: " + username);
            }

            User user = userOpt.get();

            // 3. 檢查用戶是否啟用
            if (!user.getEnabled()) {
                log.warn("用戶已被禁用: {}", username);
                throw new UsernameNotFoundException("用戶已被禁用: " + username);
            }

            // 4. 獲取用戶角色 
            String roleKey = roleMapper.findRolesByUserId(user.getId()).getRoleKey();
            log.info("用戶角色: {} - {}", username, roleKey);
            String finalRole = roleKey.startsWith("ROLE_") ? roleKey : "ROLE_" + roleKey;
            // 5. 轉換為 Spring Security 的 GrantedAuthority
            var authorities =new SimpleGrantedAuthority(finalRole);


            // 6. 返回 UserDetails
            return new PermissionTreeDTO.UserPrincipal(
                    user.getId(),            // ★ 加入 userId
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmployeeId(),
                    Collections.singleton(authorities)
            );
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("加載用戶詳情失敗: {}", username, e);
            throw new UsernameNotFoundException("加載用戶詳情失敗: " + username, e);
        }
    }
}
