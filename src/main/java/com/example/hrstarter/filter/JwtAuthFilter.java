package com.example.hrstarter.filter;


import com.example.hrstarter.dto.PermissionTreeDTO;
import com.example.hrstarter.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtil;

    public JwtAuthFilter(JwtUtils jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        // 💡 第一步：如果是 WebSocket 握手路徑，直接放行，不走後面的邏輯
        if (path.startsWith("/ws-hr")) {
            chain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {

                // 1. 新增：檢查 Redis 黑名單 (Denylist)
                String cacheKey = "jwt:denylist:" + token;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                    // 若在黑名單中，直接視為無效，清除 Context 並進入下一個 Filter (或拋出異常)
                    SecurityContextHolder.clearContext();
                    chain.doFilter(request, response);
                    return;
                }

                // 2. 原本的 JWT 驗證與解析邏輯
                String username = jwtUtil.validateAndGetUsername(token);
                Long userId = jwtUtil.getUserId(token);
                Long employeeId = jwtUtil.getEmployeeId(token);
                if (username != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    List<SimpleGrantedAuthority> authorities =
                            jwtUtil.getAuthorities(token).stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .toList();
                    PermissionTreeDTO.UserPrincipal loginUser = PermissionTreeDTO.UserPrincipal.builder()
                            .userId(userId) // 確保這裡拿得到值
                            .username(username)
                            .authorities(authorities)
                            .employeeId(employeeId)
                            .build();
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    loginUser,
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }

            } catch (Exception ex) {
                // 🔥 token 有問題，直接清 context（安全）
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }


}



