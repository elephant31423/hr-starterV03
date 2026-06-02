package com.example.hrstarter.service;

import com.example.hrstarter.entity.AuditLogEntity;

public interface AsyncLogService {
      void saveLogAsync(AuditLogEntity audit);
}
