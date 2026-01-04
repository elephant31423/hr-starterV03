package com.example.hrstarter.aop;

import com.example.hrstarter.annotation.AuditLog;
import com.example.hrstarter.entity.AuditLogEntity;
import com.example.hrstarter.entity.UserPrincipal;
import com.example.hrstarter.mapper.AuditLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogMapper auditLogMapper;
    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;


    @Pointcut("@annotation(com.example.hrstarter.annotation.AuditLog)")
    public void auditPointcut() {}

    @Around("auditPointcut()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AuditLog annotation = signature.getMethod().getAnnotation(AuditLog.class);

        String action = annotation.action();
        String entityType = annotation.entityType();
        String idParam = annotation.idParam();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        Long userId = null;

        if (principal instanceof UserPrincipal userPrincipal) {
            userId = userPrincipal.getId();
            username = userPrincipal.getUsername();
        }
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Long entityId = extractId(joinPoint, idParam);

        Object oldValue = null;

        try {
            Object result = joinPoint.proceed();
            saveLog(userId, username, action, entityType, entityId, oldValue, result, "SUCCESS", null, ip, userAgent);
            return result;

        } catch (Exception ex) {
            saveLog(userId, username, action, entityType, entityId, oldValue, null, "FAILURE", ex.getMessage(), ip, userAgent);
            throw ex;
        }
    }

    private Long extractId(ProceedingJoinPoint joinPoint, String idParam) {
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        for (int i = 0; i < args.length; i++) {
            if (paramNames[i].equals(idParam)) {
                return Long.valueOf(args[i].toString());
            }
        }
        return null;
    }

    private void saveLog(Long userId, String username, String action, String entityType,
                         Long entityId, Object oldValue, Object newValue, String status,
                         String errorMessage, String ip, String userAgent) {

        try {
            AuditLogEntity log = new AuditLogEntity();
            log.setUserId(userId);
            log.setUsername(username);
            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setOldValue(objectMapper.writeValueAsString(oldValue));
            log.setNewValue(objectMapper.writeValueAsString(newValue));
            log.setStatus(status);
            log.setErrorMessage(errorMessage);
            log.setIpAddress(ip);
            log.setUserAgent(userAgent);

            auditLogMapper.insert(log);

        } catch (Exception e) {
            log.error("審計紀錄寫入失敗: {}", e.getMessage());
        }
    }
}
