package com.example.hrstarter.LeaveCalculation.imp;

import com.example.hrstarter.LeaveCalculation.LeaveCalculationStrategy;
import com.example.hrstarter.common.SeniorityUtils;
import com.example.hrstarter.mapper.AnnualLeaveRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component("CALENDAR")
public class CalendarStrategy implements LeaveCalculationStrategy {
    @Autowired
    private AnnualLeaveRuleMapper ruleService;

    @Override
    public BigDecimal calculateDays(LocalDate hireDate, int targetYear) {
        LocalDate yearStart = LocalDate.of(targetYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(targetYear, 12, 31);
        LocalDate anniversaryDay = hireDate.withYear(targetYear);

        // 如果週年日在年初之前（入職不到一年）或年底之後，邏輯會更複雜，這裡處理標準情況：
        // 1. 前段：1/1 ~ 週年日前一天 (適用舊年資)
        BigDecimal preSeniority = SeniorityUtils.calculateSeniorityAtDate(hireDate, anniversaryDay.minusDays(1));
        int preFullDays = ruleService.getDaysBySeniority(preSeniority);
        BigDecimal preRatio = SeniorityUtils.getYearRatio(yearStart, anniversaryDay.minusDays(1));

        // 2. 後段：週年日 ~ 12/31 (適用新年資)
        BigDecimal postSeniority = SeniorityUtils.calculateSeniorityAtDate(hireDate, anniversaryDay);
        int postFullDays = ruleService.getDaysBySeniority(postSeniority);
        BigDecimal postRatio = SeniorityUtils.getYearRatio(anniversaryDay, yearEnd);

        // 公式：(前段全額天數 * 前段佔全年比例) + (後段全額天數 * 後段佔全年比例)
        return BigDecimal.valueOf(preFullDays).multiply(preRatio)
                .add(BigDecimal.valueOf(postFullDays).multiply(postRatio))
                .setScale(1, RoundingMode.HALF_UP); // 歷年制通常保留一位小數
    }
}