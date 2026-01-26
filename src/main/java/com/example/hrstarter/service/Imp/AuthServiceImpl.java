package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.Permission;
import com.example.hrstarter.entity.Role;
import com.example.hrstarter.entity.User;
import com.example.hrstarter.mapper.PermissionMapper;
import com.example.hrstarter.mapper.RoleMapper;
import com.example.hrstarter.mapper.UserMapper;
import com.example.hrstarter.util.JwtUtil;
import com.example.hrstarter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    //TODO
    private final RoleMapper roleMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private final PermissionMapper permissionMapper;

    public AuthServiceImpl(UserMapper userMapper, RoleMapper roleMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;

        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.permissionMapper = permissionMapper;
    }


    /**
     * 驗證用戶憑證
     *
     * @param username 用戶名
     * @param password 密碼
     * @return 用戶對象（如果驗證成功）
     */
    public Optional<User> validateCredentials(String username, String password) {
        log.info("驗證用戶開始: {}", username);

        User user = userMapper.findByUsername(username);

        if (user==null) {
            log.warn("用戶不存在: {}", username);
            return Optional.empty();
        }


        if (!user.getEnabled()) {
            log.warn("用戶已被禁用: {}", username);
            return Optional.empty();
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("密碼不正確: {}", username);
            return Optional.empty();
        }

        log.info("用戶驗證成功: {}", username);
        return Optional.of(user);
    }

    /**
     * 登入並生成 Token
     *
     * @param user 用戶
     * @return JWT Token
     */
    public String loginAndGenerateToken(User user) {
        log.info("開始生成用戶 {} 的 Token", user.getUsername());
        List<String> permissions = getUserPermissions(user.getId());
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(),user.getEmployeeId(),permissions);
        log.info("用戶 Token 生成成功: {}", user.getUsername());
        return token;
    }

    /**
     * 根據用戶 ID 查詢用戶的所有權限碼
     *
     * @param userId 用戶 ID
     * @return 權限碼列表
     */
    public List<String> getUserPermissions(Long userId) {
        log.info("查詢用戶權限: userId = {}", userId);
        List<String> permissions = permissionMapper.selectPermissionCodesByUserId(userId);
        log.info("登入成功 用戶權限: {} - {}", userId, permissions);
        return permissions;
    }

    /**
     * 根據用戶 ID 查詢用戶的所有權限對象
     *
     * @param userId 用戶 ID
     * @return 權限對象列表
     */
    public List<Permission> getUserPermissionDetails(Long userId) {
        log.info("查詢用戶權限詳情: userId = {}", userId);
        return permissionMapper.selectUserPermissionsByUserId(userId);
    }

    /**
     * 根據用戶 ID 查詢用戶的所有角色碼
     *
     * @param userId 用戶 ID
     * @return 角色碼列表
     */
    public Role getUserRole(Long userId) {
        log.info("查詢用戶角色: userId = {}", userId);
        return roleMapper.findRolesByUserId(userId);
    }

    /**
     * 刷新 Token
     *
     * @return 新的 JWT Token
     */
    public String refreshToken() {
        log.info("刷新 Token");
        // 實現 Token 刷新邏輯
        throw new RuntimeException("Token 已過期，請重新登入");
    }
}
