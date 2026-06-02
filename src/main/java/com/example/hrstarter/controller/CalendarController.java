package com.example.hrstarter.controller;

import com.example.hrstarter.dto.CalendarDTO;
import com.example.hrstarter.dto.employee.EmployeeCalendarDTO;
import com.example.hrstarter.service.CalendarService;
import com.example.hrstarter.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {


   private final CalendarService calendarService;


    @GetMapping("/my")

    public CalendarDTO myCalendar(
            @RequestParam String month
    ) {
        log.info("獲取日曆，月份：{}", month);
        Long employeeId = SecurityUtils.getEmployeeId();
        log.info("當前用戶員工ID：{}", employeeId);
        YearMonth ym = YearMonth.parse(month);
        return calendarService.getCalendar(
                employeeId,
                ym
        );
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('calendar:view:all')")
    public CalendarDTO employeeCalendar(
            @PathVariable Long employeeId,
            @RequestParam String month
    ) {
        YearMonth ym = YearMonth.parse(month);
        return calendarService.getCalendar(employeeId, ym);
    }

    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('calendar:view:all')")
    public List<EmployeeCalendarDTO> overview(
            @RequestParam String month,
            @RequestParam(required = false) Long departmentId
    ) {
        YearMonth ym = YearMonth.parse(month);
        return calendarService.getDepartmentCalendar(ym, departmentId);
    }


}
