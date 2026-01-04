package com.example.hrstarter.controller;

import com.example.hrstarter.entity.WorkSchedule;
import com.example.hrstarter.service.WorkScheduleService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@PreAuthorize("hasAuthority('schedule:manage')")
public class WorkScheduleController {

    @Resource
    private WorkScheduleService service;

    @GetMapping("/monthly")
    public List<WorkSchedule> monthly(
            @RequestParam Long employeeId,
            @RequestParam String month // yyyy-MM
    ) {
        return service.getMonthlySchedule(
                employeeId,
                YearMonth.parse(month)
        );
    }

    @PostMapping
    public void assign(@RequestBody WorkSchedule schedule) {
        service.assignShift(schedule);
    }
}
