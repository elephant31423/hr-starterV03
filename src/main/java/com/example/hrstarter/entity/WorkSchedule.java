package com.example.hrstarter.entity;

import lombok.Data;

import java.time.LocalDate;
@Data
public class WorkSchedule {
    private Long id;
    private Long employeeId;
    private LocalDate workDate;
    private Long shiftId;
    private Boolean isOnCall;
    private String remark;
}
