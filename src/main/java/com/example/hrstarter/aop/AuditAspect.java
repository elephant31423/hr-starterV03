package com.example.hrstarter.aop;

import com.example.hrstarter.annotation.AuditLog;
import com.example.hrstarter.dto.PermissionTreeDTO;
import com.example.hrstarter.entity.AuditLogEntity;
import com.example.hrstarter.mapper.AuditLogMapper;
import com.example.hrstarter.mapper.UserMapper;
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
    private final UserMapper userMapper;


    @Pointcut("@annotation(com.example.hrstarter.annotation.AuditLog)")
    public void auditPointcut() {
    }

    @Around("auditPointcut()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AuditLog annotation = signature.getMethod().getAnnotation(AuditLog.class);
        String action = annotation.action();
        String entityType = annotation.entityType();
        String idParam = annotation.idParam();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal {}", principal);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        Long userId = null;

        if (principal instanceof PermissionTreeDTO.UserPrincipal userPrincipal) {
            userId = userPrincipal.getUserId();

            username = userPrincipal.getUsername();
            log.info("成功從 UserPrincipal 獲取 ID: {}", userId);
        } else {
            log.warn("登入主體類型不符: {}", principal.getClass().getName());
        }
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Long entityId = extractId(joinPoint, idParam);

        Object oldValue = null;

// 如果是 UPDATE 或 DELETE，嘗試在執行前抓取舊資料
        if (("UPDATE".equals(action) || "DELETE".equals(action)) && entityId != null) {
            oldValue = findOldData(entityType, entityId); // 自定義抓取邏輯
        }

        try {
            Object result = joinPoint.proceed();
            // 對於 DELETE，result 可能是 null，我們可以把剛抓到的 oldValue 作為 newValue 存入或標記已刪除
            saveLog(userId, username, action, entityType, entityId, oldValue, result, "SUCCESS", null, ip, userAgent);
            return result;
        } catch (Exception ex) {
            saveLog(userId, username, action, entityType, entityId, oldValue, null, "FAILURE", ex.getMessage(), ip, userAgent);
            throw ex;
        }
    }

    // 根據 entityType 動態找對應的 Service 查資料 (範例)
    private Object findOldData(String entityType, Long id) {
        try {
            if ("USER".equals(entityType)) {
                return userMapper.findById(id); // 需要在 Aspect 注入 userService
            }
            // ... 其他實體類 ...
        } catch (Exception e) {
            log.warn("無法獲取舊資料供審計: {}", e.getMessage());
        }
        return null;
    }

    private Long extractId(ProceedingJoinPoint joinPoint, String idParam) {
        if (idParam == null || idParam.isEmpty()) return null;

        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) continue;

            // 情況 1: 直接匹配參數名 (例如: public void delete(Long id))
            if (paramNames[i].equals(idParam)) {
                return Long.valueOf(arg.toString());
            }

            // 情況 2: 從物件中提取屬性 (例如: @RequestBody User user)
            try {
                // 使用反射嘗試獲取 getId()
                var method = arg.getClass().getMethod("get" + capitalize(idParam));
                Object val = method.invoke(arg);
                if (val != null) return Long.valueOf(val.toString());
            } catch (Exception ignored) {
                // 如果物件沒有此屬性，繼續找下一個參數
            }
        }
        return null;
    }

    // 輔助方法：首字母大寫
    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void saveLog(Long userId, String username, String action, String entityType, Long entityId, Object oldValue, Object newValue, String status, String errorMessage, String ip, String userAgent) {

        log.info("saveLog userId {}",userId );
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
