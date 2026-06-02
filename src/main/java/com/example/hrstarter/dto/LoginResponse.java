package com.example.hrstarter.dto;

import com.example.hrstarter.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 登入響應數據
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    /**
     * JWT Token
     */
    private String token;

    /**
     * 用戶名
     */
    private String username;

    /**
     * 用戶 ID
     */
    private Long userId;

    /**
     * 用戶角色列表
     */
    private Roles roles;

    private Long employeeId;
    /**
     * 用戶權限列表
     */
    private List<String> permissions;

    /**
     * Token 過期時間（毫秒）
     */
    private Long expiresIn;
}