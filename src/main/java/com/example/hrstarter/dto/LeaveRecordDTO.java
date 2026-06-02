package com.example.hrstarter.dto;


import com.example.hrstarter.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 請假紀錄 DTO
 *
 * @author HR System
 * @date 2024-01-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LeaveRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;


    private Long employeeId;


    private String employeeName;


    private String leaveType;


    private LocalDate startDate;


    private LocalDate endDate;

    private LocalDate leaveDate;


    private BigDecimal leaveHours;


    private String reason;


    private String status;


    private String createdByName;


    private String approvedByName;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;
}