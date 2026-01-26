package com.example.hrstarter.mapper;


import com.example.hrstarter.dto.AuditLogQueryDTO;
import com.example.hrstarter.entity.AuditLogEntity;
import java.time.LocalDateTime;
import java.util.List;


public interface AuditLogMapper {

    void insert(AuditLogEntity auditLog);


    Long count(AuditLogQueryDTO auditLogQueryDTO);

    List<AuditLogEntity> selectPage(AuditLogQueryDTO auditLogQueryDTO);
}
