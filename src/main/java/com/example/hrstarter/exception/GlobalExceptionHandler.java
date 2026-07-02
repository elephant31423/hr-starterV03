package com.example.hrstarter.exception;

import com.example.hrstarter.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局異常處理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 處理驗證異常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class )
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("驗證異常: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "請求參數驗證失敗", errors));
    }

    /**
     * 處理權限異常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        log.warn("權限異常: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.forbidden("您沒有權限執行此操作"));
    }

    /**
     * 處理業務異常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(
            BusinessException ex, WebRequest request) {

        log.warn("業務異常: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("Upload file is too large: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.badRequest("圖片檔案過大，請上傳 10MB 以下的圖片"));
    }

    /**
     * 處理通用異常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("系統異常", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.internalServerError("系統內部錯誤，請稍後重試"));
    }
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateKeyException(DuplicateKeyException ex) {
        log.warn("資料庫唯一約束衝突: {}", ex.getMessage());

        String message = "資料重複";
        if (ex.getMessage().contains("employee.email")) {
            message = "該 Email 已被使用，請更換 Email";
        } else if (ex.getMessage().contains("employee.emp_no")) {
            message = "員工編號已存在，請重新輸入";
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict
                .body(ApiResponse.error(409, message));
    }


    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiResponse<?>> handleRateLimit(RateLimitException e) {
        log.error("🛑 [限流防禦] 觸發攔截: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS) // 回傳 429
                .body(ApiResponse.error(429, e.getMessage()));
    }
}
