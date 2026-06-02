package com.example.hrstarter.controller;

import com.example.hrstarter.dto.employee.EmployeeShiftDTO;
import com.example.hrstarter.service.EmployeeService;
import com.example.hrstarter.service.EmployeeShiftService;
import com.example.hrstarter.util.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/shift")
@AllArgsConstructor
public class EmployeeShiftController {
    EmployeeShiftService employeeShiftService;
    EmployeeService employeeService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String DASHBOARD_CACHE_KEY = "dashboard:stats:data";

    @PostMapping("/assign/{employeeId}")
//    @PreAuthorize("hasAuthority('shift:edit')")
    public void assignShift(@PathVariable Long employeeId, @RequestBody EmployeeShiftDTO employeeShiftDTO) {
        log.info("Assigning shift: {}", employeeShiftDTO);
        log.info("Current user employee ID: {}", SecurityUtils.getEmployeeId());
        log.info("Target employee ID: {}", employeeId);
        log.info("值班狀態 {}", employeeShiftDTO.getOnDuty());


        employeeShiftDTO.setEmployeeId(employeeId);
        // 3.
        employeeShiftService.assign(employeeShiftDTO);

    }


}
