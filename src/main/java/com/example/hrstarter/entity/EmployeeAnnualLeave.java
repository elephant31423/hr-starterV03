package com.example.hrstarter.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeAnnualLeave {
    private Long id;
    private Long employeeId;
    private Integer year;
    private BigDecimal totalLeaveDays;
    private BigDecimal  usedLeaveDays;
    private BigDecimal  remainingLeaveDays;

}
