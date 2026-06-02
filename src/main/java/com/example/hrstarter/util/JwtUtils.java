package com.example.hrstarter.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        Object uid = claims.get("userId");
        return  uid == null ? null : Long.valueOf(uid.toString());
    }
    public long getRemainingExpiration(String token) {
        // 解析 JWT 拿到過期時間 (Expiration Date)
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getKey()) // 你的簽名 Key
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        long now = System.currentTimeMillis();
        long diff = expiration.getTime() - now;

        // 回傳剩餘秒數，最少回傳 0
        return diff > 0 ? diff / 1000 : 0;
    }
    public String generateToken(String username, Long userId,Long employeeId, List<String> permissions ) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("employeeId", employeeId)
                .claim("authorities", permissions)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey())
                .compact();
    }

    public String validateAndGetUsername(String token) {
        try {

            Claims claims = parseClaims(token);
            return claims.getSubject();

        } catch (JwtException ex) {

            return null;
        }
    }

    public boolean isTokenValid(String token, String username) {

        String sub = validateAndGetUsername(token);
        return sub != null && sub.equals(username);
    }

    public List<String> getAuthorities(String token) {

        Claims claims = parseClaims(token);

        Object authObj = claims.get("authorities");

        if (authObj == null) {
            return List.of();
        }

        return ((List<?>) authObj).stream()
                .map(Object::toString)
                .toList();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getEmployeeId(String token) {

        Claims claims = parseClaims(token);
        Object empId = claims.get("employeeId");
        return empId == null ? null : Long.valueOf(claims.get("employeeId").toString());
    }


}
