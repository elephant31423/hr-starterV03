package com.example.hrstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 請假申請 DTO
 *
 * @author HR System
 * @date 2024-01-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;


    private Long employeeId;


    private String employeeName;


    private Long leaveTypeId;


    private String leaveTypeName;


    private LocalDate startDate;


    private LocalDate endDate;


    private BigDecimal days;


    private String reason;


    private String status;


    private LocalDateTime createdAt;

    private BigDecimal leaveHours;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}