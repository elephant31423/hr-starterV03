package com.example.hrstarter.service;

import com.example.hrstarter.dto.AuditLogQueryDTO;
import com.example.hrstarter.dto.PageData;
import com.example.hrstarter.entity.AuditLogEntity;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface AuditLogService {

     PageData<AuditLogEntity> query(AuditLogQueryDTO queryDTO);

}
