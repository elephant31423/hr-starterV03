package com.example.hrstarter.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        
        String remoteAddr = request.getRemoteAddr();
        // 如果 remoteAddr 不是我們認識的代理伺服器（例如 Nginx），
        // 就不應該信任 x-forwarded-for
        if (!isTrustedProxy(remoteAddr)) {
            return remoteAddr;
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 對於透過多個代理的情況，第一個 IP 才是真實 IP
        return ip.contains(",") ? ip.split(",")[0] : ip;
    }

    private static boolean isTrustedProxy(String ip) {
        // 這裡定義你 Nginx 的 IP
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);
    }
}
