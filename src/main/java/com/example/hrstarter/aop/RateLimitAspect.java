package com.example.hrstarter.aop;

import com.example.hrstarter.annotation.RateLimit;
import com.example.hrstarter.dto.LoginReq;
import com.example.hrstarter.exception.RateLimitException;
import com.example.hrstarter.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object doAround(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        int count = rateLimit.count();
        int time = rateLimit.time();


        // 1. 產生 Redis Key (IP + 方法名)
//        String ip = request.getRemoteAddr();
        String ip = IpUtils.getIpAddr(request);
        String methodName = joinPoint.getSignature().getName();
        String username = "";
        log.info("📡 API 請求來自 IP: {}, 方法: {}", ip, joinPoint.getSignature().toShortString());
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof LoginReq req) {
            username = ":" + req.getUsername();
        }

        String key = "ratelimit:" + ip + ":" + methodName;

        // 2. 執行 Redis 遞增操作 (原子性)
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount != null && currentCount == 1) {
            // 如果是第一次請求，設定過期時間
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }

        // 3. 判斷是否超過限制
        if (currentCount != null && currentCount > count) {
            log.warn("🚨 API 限流觸發! IP: {}, 方法: {}, 次數: {}, 嘗試帳號: {}", ip, methodName, currentCount, username);
            // 拋出異常 (建議由 GlobalExceptionHandler 捕獲，回傳 429 狀態碼)
            throw new RateLimitException(rateLimit.message());
        }

        return joinPoint.proceed();
    }
}
