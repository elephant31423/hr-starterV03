package com.example.hrstarter.controller;

import com.example.hrstarter.annotation.RateLimit;
import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.LoginResponse;
import com.example.hrstarter.entity.Permissions;
import com.example.hrstarter.entity.Roles;
import com.example.hrstarter.entity.User;
import com.example.hrstarter.service.AuthService;
import com.example.hrstarter.service.PermissionService;
import com.example.hrstarter.service.RoleService;
import com.example.hrstarter.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/auth" )
public class AuthController {
    private final AuthService authService;
    private final RoleService roleService;

    private final PermissionService permissionService;

    public AuthController(AuthService authService, RoleService roleService, PermissionService permissionService) {
        this.authService = authService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 登入端點
     *
     * 請求：
     * POST /api/auth/login
     * {
     *   "username": "admin",
     *   "password": "123456"
     * }
     *
     * 成功響應（200）：
     * {
     *   "code": 200,
     *   "message": "登入成功",
     *   "data": {
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *     "username": "admin",
     *     "userId": 1,
     *     "roles": ["ADMIN"],
     *     "permissions": ["role:create", "role:update", "role:delete"],
     *     "expiresIn": 3600000
     *   },
     *   "timestamp": "2025-11-27T16:00:00"
     * }
     *
     * 失敗響應（401）：
     * {
     *   "code": 401,
     *   "message": "帳號或密碼錯誤",
     *   "timestamp": "2025-11-27T16:00:00"
     * }
     */
    @RateLimit(count = 5, time = 60, message = "登入嘗試過於頻繁，請一分鐘後再試")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody com.example.hrstarter.dto.LoginReq req) {
        log.info("用戶登入: {}", req.getUsername());

        try {
            // 驗證用戶是否存在
            Optional<User> opt = authService.validateCredentials(req.getUsername(), req.getPassword());
            if (opt.isEmpty()) {
                log.warn("登入失敗: 帳號或密碼錯誤 - {}", req.getUsername());
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.unauthorized("帳號或密碼錯誤"));
            }

            User user = opt.get();
            Long userId = user.getId();

            // 生成 JWT Token
            String token = authService.loginAndGenerateToken(user);
            log.info("用戶 {} 的 token :{} employeeId={}",user.getUsername(),token ,user.getEmployeeId());
            // 獲取用戶角色
            Roles role = roleService.getRolesByUserId(userId);
            log.info(role.getRoleName());

            // 獲取用戶權限
            List<String> permissionCodes = permissionService.selectPermissionCodesByRoleId(role.getId()).stream()
                    .map(Permissions::getPermissionCode)
                    .toList();

            // 構建登入響應
            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .userId(user.getId())
                    .employeeId(user.getEmployeeId())
                    .roles(role)
                    .permissions(permissionCodes)
                    .expiresIn(3600000L) // 1 小時
                    .build();

            log.info("用戶登入成功: {} (ID: {})", req.getUsername(), user.getId());
            return ResponseEntity.ok(ApiResponse.success("登入成功", loginResponse));

        } catch (Exception e) {
            log.error("登入異常", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("登入失敗，請稍後重試"));
        }
    }

    /**
     * 登出端點
     *
     * 請求：
     * POST /api/auth/logout
     *
     * 成功響應（200）：
     * {
     *   "code": 200,
     *   "message": "登出成功",
     *   "timestamp": "2025-11-27T16:00:00"
     * }
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 獲取該 Token 剩餘有效秒數
                long remainingSeconds = jwtUtils.getRemainingExpiration(token);

                if (remainingSeconds > 0) {
                    // 存入 Redis，Key 設定為 "jwt:denylist:{token}"
                    String cacheKey = "jwt:denylist:" + token;
                    redisTemplate.opsForValue().set(cacheKey, "logout", remainingSeconds, TimeUnit.SECONDS);
                    log.info("Token 已成功作廢，剩餘時效: {} 秒", remainingSeconds);
                }
            } catch (ExpiredJwtException e) {
                log.info("Token 已過期，無需加入黑名單");
            } catch (Exception e) {
                log.error("登出處理失敗", e);
            }
        }
        return ResponseEntity.ok(ApiResponse.success("登出成功"));
    }
    /**
     * 刷新 Token 端點
     *
     * 請求：
     * POST /api/auth/refresh
     *
     * 成功響應（200）：
     * {
     *   "code": 200,
     *   "message": "Token 刷新成功",
     *   "data": {
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *     "expiresIn": 3600000
     *   },
     *   "timestamp": "2025-11-27T16:00:00"
     * }
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refresh() {
        log.info("用戶刷新 Token");
        try {
            String newToken = authService.refreshToken();
            return ResponseEntity.ok(ApiResponse.success(
                    "Token 刷新成功",
                    Map.of("token", newToken, "expiresIn", 3600000L)
            ));
        } catch (Exception e) {
            log.error("刷新 Token 失敗", e);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.unauthorized("Token 已過期，請重新登入"));
        }
    }

}

