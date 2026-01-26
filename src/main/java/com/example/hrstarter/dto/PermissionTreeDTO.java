package com.example.hrstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionTreeDTO {
    private Long id;
    private Long parentId;
    private String permissionCode; // 對應 permission_code
    private String permissionName; // 對應 permission_name
    private String description;
    private List<PermissionTreeDTO> children = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserPrincipal implements UserDetails {
        private Long userId;
        private String username;
        private String password;
        private Long employeeId;
        private Collection<? extends GrantedAuthority> authorities;

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
