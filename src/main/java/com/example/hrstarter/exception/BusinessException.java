package com.example.hrstarter.exception;

import org.springframework.http.HttpStatus;

/**
 * 業務異常
 */
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final HttpStatus status;

    public BusinessException(Integer code, String message ) {
        super(message);
        this.code = code;
        this.status = HttpStatus.valueOf(code);
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = HttpStatus.valueOf(code);
    }

    public Integer getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // 便利方法
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(403, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(409, message);
    }
}