package com.example.hrstarter.dto;

import com.example.hrstarter.enums.LeaveType;
import com.example.hrstarter.enums.ShiftType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class CalendarDayDTO {
    // ===== 基本日期 =====
    private LocalDate date;      // 2025-03-15
    private int day;             // 15
    private int month;           // 3
    private int year;            // 2025
    private int dayOfWeek;       // 1~7 (Mon~Sun)

    // ===== 班表 =====
    private ShiftType shiftType; // MORNING / EVENING / NIGHT / OFF
    private boolean onDuty;      // 是否值班

    // ===== 請假 =====
    private LeaveType leaveType; // ANNUAL / SICK / PERSONAL
    private BigDecimal leaveHours;  // 4 / 8
    private boolean fullDayLeave;

    // ===== 假日 =====
    private boolean isHoliday;
    private String holidayName;  // 清明節 / 春節
    private boolean birthday;   // 員工生日

    // ===== 顯示用（前端不用再判斷）=====
    private String displayLabel; // "早班" / "特休" / "國定假日"
    private String color;        // #4CAF50
}
