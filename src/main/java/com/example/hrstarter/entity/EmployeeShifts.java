package com.example.hrstarter.entity;

import com.example.hrstarter.enums.ShiftType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class EmployeeShifts {
    private Long employeeId;
    private LocalDate shiftDate;
    private ShiftType shiftType;
    private Boolean onDuty;
    private String remark;
    private Long createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}
