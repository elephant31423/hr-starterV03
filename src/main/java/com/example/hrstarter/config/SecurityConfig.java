package com.example.hrstarter.config;

import com.example.hrstarter.filter.JwtAuthFilter;
import com.example.hrstarter.util.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173,http://127.0.0.1:3000}")
    private List<String> allowedOrigins;

    @Bean
    public JwtAuthFilter jwtAuthFilter(
            JwtUtils jwtUtil) {
        return new JwtAuthFilter(jwtUtil);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                // ✅ 添加 CORS 配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 禁用 CSRF（因為使用 JWT）
                .csrf(AbstractHttpConfigurer::disable)

                // 無狀態會話
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"未登入或 Token 無效\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"權限不足\"}");
                        })
                )
                // 授權配置
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/system-settings/branding", "/uploads/**", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**","/ws-hr/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 添加 JWT 過濾器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允許的前端源
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",   // Vite 開發伺服器
                "http://localhost:3000",   // 備用前端端口
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        ));

        // 允許的 HTTP 方法
        configuration.setAllowedOrigins(allowedOrigins);

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // 允許的請求頭
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 允許發送認證信息（如 Cookie）
        configuration.setAllowCredentials(true);

        // 預檢請求的有效期（秒）
        configuration.setMaxAge(3600L);

        // 暴露的響應頭（前端可以訪問）
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"  // 用於分頁
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
