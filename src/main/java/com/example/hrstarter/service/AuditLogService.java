package com.example.hrstarter.service;

import com.example.hrstarter.dto.AuditLogQueryDTO;
import com.example.hrstarter.dto.PageData;
import com.example.hrstarter.entity.AuditLogEntity;


/**
 * @ClassName AuditLogService
 * @Description 审计日志服务接口
 * @Author chiu
 * @Date 2026-01-31
 * @Version 1.0
 */

public interface AuditLogService {

     PageData<AuditLogEntity> query(AuditLogQueryDTO queryDTO);

}
