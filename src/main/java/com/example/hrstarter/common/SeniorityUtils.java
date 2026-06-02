package com.example.hrstarter.common;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SeniorityUtils {

    /**
     * 計算特定日期時的年資（以年為單位，保留兩位小數）
     */
    public static BigDecimal calculateSeniorityAtDate(LocalDate hireDate, LocalDate targetDate) {
        if (hireDate == null || targetDate == null || hireDate.isAfter(targetDate)) {
            return BigDecimal.ZERO;
        }
        long days = ChronoUnit.DAYS.between(hireDate, targetDate);
        // 使用 365.25 考慮閏年平均值
        return
                BigDecimal.valueOf(days)
                        .divide(BigDecimal.valueOf(365.25), 2, RoundingMode.HALF_UP);
    }

    /**
     * 計算比例（用於歷年制拆分）
     */
    public static BigDecimal getYearRatio(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end) + 1; // 包含當天
        return BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);
    }
}