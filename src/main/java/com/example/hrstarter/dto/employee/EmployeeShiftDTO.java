package com.example.hrstarter.dto.employee;

import com.example.hrstarter.enums.ShiftType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeShiftDTO {

    private Long employeeId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate shiftDate;
    private ShiftType shiftType;
    @JsonProperty("onDuty")
    private Boolean onDuty;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime shiftStartTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime shiftEndTime;



}
