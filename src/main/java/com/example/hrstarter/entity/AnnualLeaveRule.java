package com.example.hrstarter.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class AnnualLeaveRule {
    private Long id;
    private String ruleName;
    private Integer minYears;
    private Integer maxYears;
    private BigDecimal hours;

    public double getDays() {
        return hours.divide(BigDecimal.valueOf(8), 2, RoundingMode.HALF_UP).doubleValue();
    }
}
