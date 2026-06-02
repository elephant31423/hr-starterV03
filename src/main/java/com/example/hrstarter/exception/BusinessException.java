package com.example.hrstarter.exception;

import com.example.hrstarter.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 業務異常 - 升級版
 * 1. 支援業務代碼(Code)與 HTTP 狀態碼(Status)分離
 * 2. 提供更豐富的靜態工廠方法
 * 3. 優化預設狀態碼邏輯
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final HttpStatus status;

    // 基礎建構子：自定義 Code, Status 與 Message
    public BusinessException(Integer code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
    }

    // 常用建構子：從 ErrorCode 自動解析
    public BusinessException(ErrorCode errorCode) {
        this(errorCode.getCode(),
                parseStatus(errorCode.getCode()),
                errorCode.getMessage());
    }

    // 常用建構子：從 ErrorCode 解析，但覆蓋 Message
    public BusinessException(ErrorCode errorCode, String customMessage) {
        this(errorCode.getCode(),
                parseStatus(errorCode.getCode()),
                customMessage);
    }

    // 進階建構子：自定義一切（包含 ErrorCode、自定義狀態碼與訊息）
    public BusinessException(ErrorCode errorCode, HttpStatus status, String customMessage) {
        this(errorCode.getCode(), status, customMessage);
    }

    /**
     * 靜態解析邏輯：
     * 如果 Code 是 3 位數且符合 HTTP 規範則使用之，否則預設為 400 (業務請求錯誤)
     */
    private static HttpStatus parseStatus(Integer code) {
        if (code == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        HttpStatus resolved = HttpStatus.resolve(code);
        // 如果是 5 位數業務碼（例如 20001），resolve 會回傳 null，此時統一定義為 400 Bad Request
        return resolved != null ? resolved : HttpStatus.BAD_REQUEST;
    }

    // --- 強化版便利方法 (Static Factory Methods) ---

    public static BusinessException badRequest(String message) {
        return new BusinessException(ErrorCode.PARAM_ERROR.getCode(), HttpStatus.BAD_REQUEST, message);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(ErrorCode.UNAUTHORIZED.getCode(), HttpStatus.UNAUTHORIZED, message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(ErrorCode.FORBIDDEN.getCode(), HttpStatus.FORBIDDEN, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), HttpStatus.NOT_FOUND, message);
    }

    /**
     * 伺服器內部錯誤 (500)
     */
    public static BusinessException error(String message) {
        return new BusinessException(ErrorCode.INTERNAL_ERROR.getCode(), HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(ErrorCode.CONFLICT.getCode(), HttpStatus.CONFLICT, message);
    }
}