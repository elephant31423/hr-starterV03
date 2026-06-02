package com.example.hrstarter.LeaveCalculation.imp;

import com.example.hrstarter.LeaveCalculation.LeaveCalculationStrategy;
import com.example.hrstarter.common.SeniorityUtils;
import com.example.hrstarter.mapper.AnnualLeaveRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component("ANNIVERSARY")
public class AnniversaryStrategy implements LeaveCalculationStrategy {
    @Autowired
    private AnnualLeaveRuleMapper ruleService;

    @Override
    public BigDecimal calculateDays(LocalDate hireDate, int targetYear) {
        // 週年制通常以該年度「會達到」的最高年資來算，或以週年日當天計算
        // 這裡採主流作法：計算該年度週年日當天的年資
        LocalDate anniversaryDay = hireDate.withYear(targetYear);
        BigDecimal seniority = SeniorityUtils.calculateSeniorityAtDate(hireDate, anniversaryDay);

        int days = ruleService.getDaysBySeniority(seniority);
        return BigDecimal.valueOf(days);
    }
}