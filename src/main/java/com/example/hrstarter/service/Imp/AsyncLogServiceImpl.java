package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.AuditLogEntity;
import com.example.hrstarter.mapper.AuditLogMapper;
import com.example.hrstarter.service.AsyncLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncLogServiceImpl implements AsyncLogService {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;


    @Async("logExecutor")
    @Override
    public void saveLogAsync(AuditLogEntity audit) {
        try {
            // 執行耗時的資料庫寫入
            log.info("當前執行緒名稱: {}", Thread.currentThread().getName());
            auditLogMapper.insert(audit);
        } catch (Exception e) {
            log.error("非同步審計紀錄寫入失敗: {}", e.getMessage());
        }
    }

}
