package com.example.hrstarter.aop;

import com.example.hrstarter.annotation.NoRepeatSubmit;
import com.example.hrstarter.util.IpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RepeatSubmitAspect {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(noRepeatSubmit)")
    public Object doAround(ProceedingJoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 1. 產生唯一的請求標識 (IP + URL + 參數的 MD5)
        String ip = IpUtils.getIpAddr(request);
        String url = request.getRequestURI();
        String params = objectMapper.writeValueAsString(joinPoint.getArgs());

        // 使用 Spring 內建工具做 MD5，避免 Key 太長
        String paramsMd5 = DigestUtils.md5DigestAsHex(params.getBytes());
        String key = "repeat_submit:" + ip + ":" + url + ":" + paramsMd5;

        // 2. 嘗試存入 Redis (SETNX)
        // setIfAbsent 回傳 true 代表之前沒這組 Key，存入成功；回傳 false 代表已存在
        Boolean isAbsent = redisTemplate.opsForValue().setIfAbsent(key, "locked", noRepeatSubmit.lockTime(), TimeUnit.SECONDS );

        if (Boolean.FALSE.equals(isAbsent)) {
            log.warn("🛑 偵測到重複提交: {}", key);
            // 這裡建議拋出自定義異常，或複用之前的 RateLimitException
            throw new RuntimeException(noRepeatSubmit.message());
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            // 如果執行過程中出錯，通常我們會選擇刪除 Key，讓使用者可以立即重試
            redisTemplate.delete(key);
            throw e;
        }
    }



}
