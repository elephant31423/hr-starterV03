package com.example.hrstarter.controller;

import com.example.hrstarter.annotation.AuditLog;
import com.example.hrstarter.dto.EmployeeShiftDTO;
import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.service.EmployeeService;
import com.example.hrstarter.service.EmployeeShiftService;
import com.example.hrstarter.util.EmployeeContext;
import com.example.hrstarter.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/shift")
@AllArgsConstructor
public class EmployeeShiftController {
    EmployeeShiftService employeeShiftService;
    EmployeeService employeeService;

    @PostMapping("/assign/{employeeId}")
//    @PreAuthorize("hasAuthority('shift:edit')")
    public void assignShift(@PathVariable Long employeeId, @RequestBody EmployeeShiftDTO employeeShiftDTO) {
        log.info("Assigning shift: {}", employeeShiftDTO);
        log.info("Current user employee ID: {}", SecurityUtil.getEmployeeId());
        log.info("Target employee ID: {}", employeeId);
        log.info("值班狀態 {}" ,employeeShiftDTO.getOnDuty()  );


        employeeShiftDTO.setEmployeeId(employeeId);
        // 3.
        employeeShiftService.assign(employeeShiftDTO);
    }


}
