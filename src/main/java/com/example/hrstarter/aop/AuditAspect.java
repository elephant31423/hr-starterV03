package com.example.hrstarter.aop;

import com.example.hrstarter.annotation.AuditLog;
import com.example.hrstarter.dto.PermissionTreeDTO;
import com.example.hrstarter.entity.AuditLogEntity;
import com.example.hrstarter.mapper.AuditLogMapper;
import com.example.hrstarter.mapper.UserMapper;
import com.example.hrstarter.service.AsyncLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
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
    private final ApplicationContext applicationContext;
    private final AsyncLogService asyncLogService;
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
        Long entityId = extractId(joinPoint, idParam);
        log.info("entityId--{}--idParam--{}",entityId,idParam);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("now principal {}", principal);

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


        Object oldValue = null;

// 如果是 UPDATE 或 DELETE，嘗試在執行前抓取舊資料
        if (("UPDATE".equals(action) || "DELETE".equals(action)) && entityId != null) {
            oldValue = findOldData(entityType, entityId); // 自定義抓取邏輯
        }

        try {
            Object result = joinPoint.proceed();
            // 如果是 CREATE，嘗試從結果中抓取新創建的 ID
            if ("CREATE".equals(action) && entityId == null) {
                entityId = tryGetIdFromResult(result, idParam);
            }

            saveLog(userId, username, action, entityType, entityId, oldValue, result, "SUCCESS", null, ip, userAgent);
            return result;
        } catch (Exception ex) {
            saveLog(userId, username, action, entityType, entityId, oldValue, null, "FAILURE", ex.getMessage(), ip, userAgent);
            throw ex;
        }
    }

    /**
     * 通用的舊資料查詢邏輯
     * 約定：entityType = "EMPLOYEE" -> 尋找 "employeeMapper" 並調用 "selectById"
     */
    private Object findOldData(String entityType, Long id) {
        if (id == null) return null;

        try {
            // 1. 根據實體名稱推導 Mapper 的 Bean 名稱 (首字母小寫 + Mapper)
            // 例如: EMPLOYEE -> employeeMapper
            String mapperName = entityType.toLowerCase() + "Mapper";
            Object mapper = applicationContext.getBean(mapperName);

            // 2. 利用反射調用 selectById (這是 MyBatis-Plus 的標準方法)
            // 如果你不是用 MyBatis-Plus，改為你的通用方法名如 "findById"
            var method = mapper.getClass().getMethod("selectById", java.io.Serializable.class);
            return method.invoke(mapper, id);

        } catch (Exception e) {
            log.warn("審計 AOP 自動抓取舊資料失敗 [Entity: {}, ID: {}]: {}", entityType, id, e.getMessage());
            return null;
        }
    }
    private Long extractId(ProceedingJoinPoint joinPoint, String idParam) {
        if (idParam == null || idParam.isEmpty()) return null;

        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) continue;

            // 情況 A: 參數名直接匹配 (例如 delete(@PathVariable Long id))
            if (paramNames != null && idParam.equals(paramNames[i])) {
                return Long.valueOf(arg.toString());
            }

            // 情況 B: 物件屬性 (例如 update(@RequestBody Employee e))
            try {
                // 嘗試反射 getXxx()
                String methodName = "get" + capitalize(idParam);
                var method = arg.getClass().getMethod(methodName);
                Object val = method.invoke(arg);
                if (val != null) return Long.valueOf(val.toString());
            } catch (NoSuchMethodException e) {
                // 該參數沒有此方法，繼續尋找下一個參數
            } catch (Exception e) {
                log.error("從參數提取 ID 失敗: {}", e.getMessage());
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
            AuditLogEntity audit = new AuditLogEntity();
            audit.setUserId(userId);
            audit.setUsername(username);
            audit.setAction(action);
            audit.setEntityType(entityType);
            audit.setEntityId(entityId);
            audit.setOldValue(oldValue != null ? objectMapper.writeValueAsString(oldValue) : null);
            audit.setNewValue(newValue != null ? objectMapper.writeValueAsString(newValue) : null);
            audit.setStatus(status);
            audit.setErrorMessage(errorMessage);
            audit.setIpAddress(ip);
            audit.setUserAgent(userAgent);
//            auditLogMapper.insert(audit);
            // 為了不阻塞主線程，改為異步寫入
            asyncLogService.saveLogAsync(audit);

        } catch (Exception e) {
            log.error("審計紀錄寫入失敗: {}", e.getMessage());
        }
    }

    private Long tryGetIdFromResult(Object result, String idParam) {
        if (result == null) return null;
        try {
            Object body = result;
            // 1. 如果是 ResponseEntity，拆開拿 body (ApiResponse)
            if (result instanceof org.springframework.http.ResponseEntity<?> response) {
                body = response.getBody();
            }

            if (body == null) return null;

            // 2. 關鍵點：如果你的 body 是 ApiResponse，ID 在 body.getData() 裡面
            Object targetObj = body;
            try {
                // 嘗試看有沒有 getData() 方法 (針對 ApiResponse 結構)
                var getDataMethod = body.getClass().getMethod("getData");
                Object data = getDataMethod.invoke(body);
                if (data != null) targetObj = data;
            } catch (NoSuchMethodException e) {
                // 如果 body 本身就是實體物件，則維持不變
            }

            // 3. 從目標物件拿 ID (例如 getId())
            String methodName = "get" + capitalize(idParam);
            var method = targetObj.getClass().getMethod(methodName);
            Object val = method.invoke(targetObj);

            return val != null ? Long.valueOf(val.toString()) : null;
        } catch (Exception e) {
            log.warn("無法從結果中提取 ID: {}", e.getMessage());
            return null;
        }
    }
}
