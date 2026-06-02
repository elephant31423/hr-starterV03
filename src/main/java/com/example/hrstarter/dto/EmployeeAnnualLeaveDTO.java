package com.example.hrstarter.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 員工年度特休 DTO
 *
 * @author HR System
 * @date 2024-01-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class EmployeeAnnualLeaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;


    private Long employeeId;


    private String employeeName;


    private Integer year;




    private BigDecimal usagePercentage;

    // 數值欄位全部改用 BigDecimal
    private BigDecimal totalHours;
    private BigDecimal usedHours;
    private BigDecimal remainHours;
    private String remainDisplay;
}