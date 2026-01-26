package com.example.hrstarter.exception;

import com.example.hrstarter.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 業務異常
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final HttpStatus status;

    // 支援傳入自定義 ErrorCode
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.status = HttpStatus.resolve(this.code) != null ? HttpStatus.resolve(this.code) : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    // 支援覆蓋預設訊息 (適合細化錯誤原因)
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.status = HttpStatus.resolve(this.code) != null ? HttpStatus.resolve(this.code) : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public Integer getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // 便利方法
    public static BusinessException badRequest(String message) {
        return new BusinessException(ErrorCode.PARAM_ERROR, message);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(ErrorCode.INTERNAL_ERROR, message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(ErrorCode.PARAM_ERROR, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(ErrorCode.USER_NOT_FOUND, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(ErrorCode.INTERNAL_ERROR, message);
    }
}