package com.example.hrstarter.util;

import com.example.hrstarter.dto.PermissionTreeDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static PermissionTreeDTO.UserPrincipal getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof PermissionTreeDTO.UserPrincipal)) {
            return null;
        }
        return (PermissionTreeDTO.UserPrincipal) auth.getPrincipal();
    }

    public static Long getEmployeeId() {
        PermissionTreeDTO.UserPrincipal user = getLoginUser();
        return user == null ? null : user.getEmployeeId();
    }

    public static Long getUserId() {
        PermissionTreeDTO.UserPrincipal user = getLoginUser();
        return user == null ? null : user.getUserId();
    }

    /**
     * 判斷當前使用者是否具有特定權限 (例如 'shift:edit')
     */
    public static boolean hasPermission(String permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }

    /**
     * 判斷是否為管理員/主管 (根據角色)
     */
    public static boolean isManager() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
    }
    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    public static boolean hasRole(String roleKey) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + roleKey)
                        || a.getAuthority().equals(roleKey));
    }
}
