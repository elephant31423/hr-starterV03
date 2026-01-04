package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.PageData;
import com.example.hrstarter.entity.AuditLogEntity;
import com.example.hrstarter.mapper.AuditLogMapper;
import com.example.hrstarter.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

    public AuditLogServiceImpl(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }


    @Override
    public PageData<AuditLogEntity> query(String username, String action, String entityType, LocalDateTime startDate, LocalDateTime endDate, Integer page, Integer size) {

        int offset = (page - 1) * size;

        List<AuditLogEntity> items = auditLogMapper.query(username, action, entityType, startDate, endDate, offset, size);

//        Long total = auditLogMapper.count(
//                username, action, entityType, startDate, endDate
//        );
//        int totalPages = (int) Math.ceil(total * 1.0 / size);
        int totalPages =  11;
        Long total =  11L;

        return PageData.<AuditLogEntity>builder()
                .items(items)
                .total(total)
                .pageNumber(page)
                .pageSize(size)
                .totalPages(totalPages)
                .build();

    }
}
