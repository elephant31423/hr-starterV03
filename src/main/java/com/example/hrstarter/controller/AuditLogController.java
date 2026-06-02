package com.example.hrstarter.controller;


import com.example.hrstarter.annotation.RateLimit;
import com.example.hrstarter.dto.AuditLogQueryDTO;
import com.example.hrstarter.dto.PageData;
import com.example.hrstarter.entity.AuditLogEntity;
import com.example.hrstarter.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    AuditLogService auditLogService;

    AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/logs")
    public PageData<AuditLogEntity> queryLogs(
            AuditLogQueryDTO queryDTO
    ) {

       return auditLogService.query(queryDTO);


    }
//    @GetMapping("/logs")
//    public PageData<AuditLogEntity> queryLogs(
//            @RequestParam(required = false) String username,
//            @RequestParam(required = false) String action,
//            @RequestParam(required = false) String entityType,
//            @RequestParam(required = false) String  startDate,
//            @RequestParam(required = false) String  endDate,
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        LocalDateTime start = (startDate == null || startDate.isEmpty()) ? null : LocalDateTime.parse(startDate + "T00:00:00");
//        LocalDateTime end   = (endDate == null || endDate.isEmpty()) ? null : LocalDateTime.parse(endDate + "T23:59:59");
//
//       return auditLogService.query(username, action, entityType, start, end, page, size);
//
//
//    }

}
