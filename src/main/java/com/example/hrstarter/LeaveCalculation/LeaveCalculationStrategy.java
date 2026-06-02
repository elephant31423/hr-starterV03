package com.example.hrstarter.LeaveCalculation;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface LeaveCalculationStrategy {
    /**
     * 計算員工在指定年份應得的特休天數
     */
    BigDecimal calculateDays(LocalDate hireDate, int targetYear);
}