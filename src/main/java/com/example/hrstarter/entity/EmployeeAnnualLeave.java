package com.example.hrstarter.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeAnnualLeave {
    private Long id;
    private Long employeeId;
    private Integer year;
    private BigDecimal totalDays;
    private BigDecimal  usedDays;
    private BigDecimal  remainDays;



}
