package com.example.hrstarter.mapper;


import com.example.hrstarter.entity.AuditLogEntity;
import java.time.LocalDateTime;
import java.util.List;


public interface AuditLogMapper {

    void insert(AuditLogEntity auditLog);

    List<AuditLogEntity> query(String username, String action, String entityType, LocalDateTime startDate, LocalDateTime endDate, Integer offset, Integer size);

    Long count(String username,
               String action,
               String entityType,
               LocalDateTime startDate,
               LocalDateTime endDate);
}
