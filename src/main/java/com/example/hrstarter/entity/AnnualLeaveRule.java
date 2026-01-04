package com.example.hrstarter.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnnualLeaveRule {
    private Long id;
    private String ruleName;
    private Integer minYearsOfService;
    private Integer maxYearsOfService;
    private BigDecimal annualLeaveDays;
}
