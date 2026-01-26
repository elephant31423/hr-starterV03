package com.example.hrstarter.entity;

import com.example.hrstarter.enums.LeaveType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LeaveRecords {

    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private LocalDate leaveDate;
    private LeaveType leaveType;
    private BigDecimal hours;
}
