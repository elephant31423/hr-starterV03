package com.example.hrstarter.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogQueryDTO {
    private String username;
    private String action;
    private String entityType;
    private String startDate;
    private String endDate;
    // 分頁參數
    private int page = 1;
    private int size = 10;

    // 輔助方法：轉換日期
    public LocalDateTime getStartDateTime() {
        return (startDate == null || startDate.isEmpty())
                ? null : LocalDateTime.parse(startDate + "T00:00:00");
    }

    public LocalDateTime getEndDateTime() {
        return (endDate == null || endDate.isEmpty())
                ? null : LocalDateTime.parse(endDate + "T23:59:59");
    }

    // 計算 SQL 的 Offset，防止負數出現
    public int getOffset() {
        return Math.max(0, (page - 1) * size);
    }
}
