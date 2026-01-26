package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.AuditLogQueryDTO;
import com.example.hrstarter.dto.PageData;
import com.example.hrstarter.entity.AuditLogEntity;
import com.example.hrstarter.mapper.AuditLogMapper;
import com.example.hrstarter.service.AuditLogService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j

public class AuditLogServiceImpl implements AuditLogService {

    private  AuditLogMapper auditLogMapper;

    public AuditLogServiceImpl(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }


    @Override
    public PageData<AuditLogEntity> query(AuditLogQueryDTO queryDTO) {
// 1. 防呆：page 至少為 1
        int currentPage = Math.max(1, queryDTO.getPage());
        int offset = (currentPage - 1) * queryDTO.getSize();

        List<AuditLogEntity> list = auditLogMapper.selectPage(
                queryDTO
        );

        long total = auditLogMapper.count(queryDTO); // 同樣傳入 DTO 計算總數
        return new PageData<>(list, total, queryDTO.getPage(), queryDTO.getSize());
    }

}
