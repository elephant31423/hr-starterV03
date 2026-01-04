package com.example.hrstarter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 統一的 API 響應格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 不序列化 null 值
public class ApiResponse<T> {
    /**
     * 響應碼（HTTP 狀態碼）
     * 200: 成功
     * 400: 請求參數錯誤
     * 401: 未認證
     * 403: 無權限
     * 404: 資源不存在
     * 500: 服務器錯誤
     */
    private Integer code;

    /**
     * 響應信息（簡短的文字說明）
     */
    private String message;

    /**
     * 響應數據（泛型，可以是任何類型）
     */
    private T data;

    /**
     * 錯誤詳情（僅在出錯時返回）
     */
    private Object errors;

    /**
     * 時間戳（ISO 8601 格式）
     */
    private LocalDateTime timestamp;

    /**
     * 成功響應（帶數據）
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 成功響應（帶自定義信息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 成功響應（無數據）
     */
    public static ApiResponse<?> success() {
        return ApiResponse.builder()
                .code(200)
                .message("操作成功")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 成功響應（帶自定義信息，無數據）
     */
    public static ApiResponse<?> success(String message) {
        return ApiResponse.builder()
                .code(200)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 創建成功響應（201 Created）
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .code(201)
                .message("資源創建成功")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 錯誤響應
     */
    public static ApiResponse<?> error(Integer code, String message) {
        return ApiResponse.builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 錯誤響應（帶詳情）
     */
    public static ApiResponse<?> error(Integer code, String message, Object errors) {
        return ApiResponse.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 請求參數錯誤（400）
     */
    public static ApiResponse<?> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 未認證（401）
     */
    public static ApiResponse<?> unauthorized(String message) {
        return error(401, message);
    }

    /**
     * 無權限（403）
     */
    public static ApiResponse<?> forbidden(String message) {
        return error(403, message);
    }

    /**
     * 資源不存在（404）
     */
    public static ApiResponse<?> notFound(String message) {
        return error(404, message);
    }

    /**
     * 資源衝突（409）
     */
    public static ApiResponse<?> conflict(String message) {
        return error(409, message);
    }

    /**
     * 服務器錯誤（500）
     */
    public static ApiResponse<?> internalServerError(String message) {
        return error(500, message);
    }
}