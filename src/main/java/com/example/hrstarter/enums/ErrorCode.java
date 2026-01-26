package com.example.hrstarter.enums;

public enum ErrorCode {
    USER_NOT_FOUND(404, "找不到該使用者"),
    ROLE_ASSIGN_ERROR(400, "角色分配失敗"),
    PARAM_ERROR(400, "參數校驗失敗"),
    INTERNAL_ERROR(500, "系統內部錯誤");
    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
