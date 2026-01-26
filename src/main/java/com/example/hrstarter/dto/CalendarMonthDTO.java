package com.example.hrstarter.dto;

import lombok.Data;

import java.util.List;

@Data
public class CalendarMonthDTO {

    private Long employeeId;
    private int year;
    private int month;

    private int totalWorkDays;
    private int totalLeaveDays;
    private int totalWorkHours;

    private List<CalendarDayDTO> days;
}
