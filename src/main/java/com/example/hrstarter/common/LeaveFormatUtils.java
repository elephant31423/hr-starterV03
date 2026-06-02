package com.example.hrstarter.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LeaveFormatUtils {
    private static final double HOURS_PER_DAY = 8.0;

    public static String formatHoursToDayHour(BigDecimal totalHours) {
        if (totalHours == null || totalHours.compareTo(BigDecimal.ZERO) <= 0) {
            return "0 小時";
        }

        // 1. 計算整數天數 (例如 12.5 小時 / 8 = 1.5625 -> 取整數 = 1 天)
        BigDecimal days = totalHours.divide(BigDecimal.valueOf(HOURS_PER_DAY), 0, RoundingMode.FLOOR);

        // 2. 計算餘下的時數 (12.5 - (1 * 8) = 4.5 小時)
        BigDecimal remainingHours = totalHours.subtract(days.multiply(BigDecimal.valueOf(HOURS_PER_DAY))).stripTrailingZeros();

        // 3. 組合字串
        StringBuilder result = new StringBuilder();
        if (days.compareTo(BigDecimal.ZERO) > 0) {
            result.append(days.toPlainString()).append(" 天 ");
        }

        if (remainingHours.compareTo(BigDecimal.ZERO) > 0 || days.compareTo(BigDecimal.ZERO) == 0) {
            result.append(remainingHours.toPlainString()).append(" 小時");
        }

        return result.toString().trim();
    }
}