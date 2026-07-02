package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.OvertimeRecord;
import com.example.hrstarter.entity.OvertimeRequest;
import com.example.hrstarter.mapper.OvertimeRecordMapper;
import com.example.hrstarter.mapper.OvertimeRequestMapper;
import com.example.hrstarter.service.NotificationService;
import com.example.hrstarter.service.OvertimeApprovalService;
import com.example.hrstarter.service.OvertimeRequestService;
import com.example.hrstarter.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OvertimeRequestServiceImpl implements OvertimeRequestService {
    private final OvertimeRequestMapper overtimeRequestMapper;
    private final OvertimeRecordMapper overtimeRecordMapper;
    private final NotificationService notificationService;

    @Autowired
    @Lazy
    private OvertimeApprovalService overtimeApprovalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OvertimeRequest apply(OvertimeRequest request) {
        validate(request);

        Long employeeId = request.getEmployeeId();
        if (employeeId == null) {
            employeeId = SecurityUtils.getEmployeeId();
        }
        if (employeeId == null) {
            throw new AccessDeniedException("目前登入帳號尚未綁定員工，不能申請加班");
        }

        request.setEmployeeId(employeeId);
        request.setHours(calculateHours(request));
        request.setStatus("PENDING_DEPARTMENT");
        request.setCreatedAt(LocalDateTime.now());
        overtimeRequestMapper.insert(request);
        overtimeApprovalService.createApprovalFlow(request.getId());
        notificationService.notifyRole("DEPARTMENT_MANAGER", "OVERTIME_PENDING", "新的加班申請", "有新的加班申請需要部門主管審核");
        return overtimeRequestMapper.findDetailById(request.getId());
    }

    @Override
    public List<OvertimeRequest> listMine() {
        Long employeeId = SecurityUtils.getEmployeeId();
        if (employeeId == null) {
            throw new AccessDeniedException("目前登入帳號尚未綁定員工");
        }
        return overtimeRequestMapper.findByEmployeeId(employeeId);
    }

    @Override
    public List<OvertimeRequest> listAll() {
        assertCanViewAll();
        return overtimeRequestMapper.findAll();
    }

    @Override
    public List<OvertimeRecord> listMyRecords() {
        Long employeeId = SecurityUtils.getEmployeeId();
        if (employeeId == null) {
            throw new AccessDeniedException("目前登入帳號尚未綁定員工");
        }
        return overtimeRecordMapper.findByEmployeeId(employeeId);
    }

    @Override
    public List<OvertimeRecord> listAllRecords() {
        assertCanViewAll();
        return overtimeRecordMapper.findAll();
    }

    @Override
    public void updateApprovalStatus(Long overtimeRequestId, String status) {
        OvertimeRequest request = overtimeRequestMapper.selectById(overtimeRequestId);
        if (request == null) {
            throw new RuntimeException("加班申請不存在");
        }
        overtimeRequestMapper.updateStatus(overtimeRequestId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finalizeApprovedOvertime(Long overtimeRequestId, Long approvedBy) {
        OvertimeRequest request = overtimeRequestMapper.selectById(overtimeRequestId);
        if (request == null) {
            throw new RuntimeException("加班申請不存在");
        }
        if ("APPROVED".equals(request.getStatus())) {
            return;
        }

        overtimeRequestMapper.updateStatus(overtimeRequestId, "APPROVED");

        OvertimeRecord record = new OvertimeRecord();
        record.setOvertimeRequestId(request.getId());
        record.setEmployeeId(request.getEmployeeId());
        record.setOvertimeDate(request.getOvertimeDate());
        record.setStartTime(request.getStartTime());
        record.setEndTime(request.getEndTime());
        record.setHours(request.getHours());
        record.setReason(request.getReason());
        record.setApprovedBy(approvedBy);
        record.setCreatedAt(LocalDateTime.now());
        overtimeRecordMapper.insert(record);
        notificationService.notifyEmployee(request.getEmployeeId(), "OVERTIME_APPROVED", "加班申請已核准", "您的加班申請已完成審核");
    }

    private void validate(OvertimeRequest request) {
        if (request == null) {
            throw new RuntimeException("加班申請不能為空");
        }
        if (request.getOvertimeDate() == null) {
            throw new RuntimeException("加班日期不能為空");
        }
        if (request.getOvertimeDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("加班日期不能早於今天");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new RuntimeException("加班起訖時間不能為空");
        }
        if (request.getStartTime().equals(request.getEndTime())) {
            throw new RuntimeException("加班起訖時間不能相同");
        }
    }

    private BigDecimal calculateHours(OvertimeRequest request) {
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        if (minutes < 0) {
            minutes += 24 * 60;
        }
        if (minutes <= 0) {
            throw new RuntimeException("加班時數必須大於 0");
        }
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private void assertCanViewAll() {
        if (SecurityUtils.hasPermission("overtime:view")
                || SecurityUtils.hasPermission("overtime:approve:department")
                || SecurityUtils.hasPermission("overtime:approve:hr")
                || SecurityUtils.hasPermission("overtime:approve:final")) {
            return;
        }
        throw new AccessDeniedException("沒有加班資料查看權限");
    }
}
