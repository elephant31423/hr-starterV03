package com.example.hrstarter.service.Imp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.hrstarter.config.ConfigService;
import com.example.hrstarter.dto.EmployeeAnnualLeaveDTO;
import com.example.hrstarter.dto.LeaveRequestDTO;
import com.example.hrstarter.entity.LeaveRecord;
import com.example.hrstarter.entity.LeaveRequest;
import com.example.hrstarter.entity.LeaveTypeEntity;
import com.example.hrstarter.mapper.LeaveRequestMapper;
import com.example.hrstarter.mapper.LeaveTypeMapper;
import com.example.hrstarter.service.EmployeeAnnualLeaveService;
import com.example.hrstarter.service.LeaveRecordService;
import com.example.hrstarter.service.LeaveRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 請假申請業務邏輯實現類
 *
 * @author HR System
 * @date 2026-01-30
 */
@Slf4j
@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveRequestService {

    @Autowired
    private LeaveRecordService leaveRecordService;

    @Autowired
    private EmployeeAnnualLeaveService employeeAnnualLeaveService;

    @Autowired
    private LeaveTypeMapper leaveTypeMapper;

    @Autowired
    private ConfigService configService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitLeaveRequest(LeaveRequest leaveRequest) {
        log.info("提交請假申請：員工ID={}, 開始日期={}, 結束日期={}, leaveRequest={}",
                leaveRequest.getEmployeeId(), leaveRequest.getStartDate(), leaveRequest.getEndDate(), leaveRequest);

        // 驗證請假申請
        String validationResult = validateLeaveRequest(leaveRequest);
        if (validationResult != null) {
            log.warn("請假申請驗證失敗：{}", validationResult);
            throw new RuntimeException(validationResult);
        }

        // 計算請假天數
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        ) + 1;
        leaveRequest.setDays(BigDecimal.valueOf(daysBetween));
        if (leaveRequest.getLeaveHours() == null || leaveRequest.getLeaveHours().compareTo(BigDecimal.ZERO) <= 0) {
            leaveRequest.setLeaveHours(calculateLeaveHours(leaveRequest));
        }

        // 計算請假時數
//        long horsBetween = ChronoUnit.HOURS.between(
//                leaveRequest.getStartTime(),
//                leaveRequest.getEndTime()
//        ) ;
//        leaveRequest.setLeaveHours(BigDecimal.valueOf(horsBetween));

        // 設置初始狀態為待審核
        leaveRequest.setStatus("PENDING");
        leaveRequest.setCreatedAt(LocalDateTime.now());

        // 保存請假申請
        boolean saved = this.save(leaveRequest);
        if (!saved) {
            log.error("保存請假申請失敗");
            throw new RuntimeException("保存請假申請失敗");
        }

        log.info("請假申請提交成功，ID={}", leaveRequest.getId());
        return true;
    }

    @Override
    public Page<LeaveRequestDTO> getEmployeeLeaveRequests(Integer pageNum, Integer pageSize, Long employeeId) {
        log.info("查詢員工請假申請：員工ID={}, 頁碼={}, 每頁數量={}", employeeId, pageNum, pageSize);

        Page<LeaveRequest> page = new Page<>(pageNum, pageSize);
        Page<LeaveRequest> result = this.baseMapper.selectByEmployeeId(page, employeeId);

        return convertToDTO(result);
    }

    @Override
    public Page<LeaveRequestDTO> getAllLeaveRequests(Integer pageNum, Integer pageSize, String status, String employeeName, String leaveType) {
        log.info("查詢所有請假申請：狀態={}, 頁碼={}, 每頁數量={},員工姓名={},假別={}", status, pageNum, pageSize, employeeName, leaveType);

        Page<LeaveRequest> page = new Page<>(pageNum, pageSize);
        Page<LeaveRequest> result = this.baseMapper.selectAllWithDetails(page, status,employeeName,leaveType);
        log.info("查詢所有請假申請getAllLeaveRequests：result={} ",result.getRecords()  );

        return convertToDTO(result);
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        log.info("查詢日期範圍內的請假申請：員工ID={}, 開始日期={}, 結束日期={}", employeeId, startDate, endDate);

        return this.baseMapper.selectByDateRange(employeeId, startDate, endDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveLeaveRequest(Long leaveRequestId, Long approvedBy) {
        log.info("批准請假申請：申請ID={}, 審核人ID={}", leaveRequestId, approvedBy);

        LeaveRequest leaveRequest = this.getById(leaveRequestId);
        if (leaveRequest == null) {
            log.error("請假申請不存在：ID={}", leaveRequestId);
            throw new RuntimeException("請假申請不存在");
        }

        if (!"PENDING".equals(leaveRequest.getStatus())) {
            log.warn("請假申請狀態不正確：ID={}, 當前狀態={}", leaveRequestId, leaveRequest.getStatus());
            throw new RuntimeException("只能批准待審核的申請");
        }

        // 更新申請狀態
        leaveRequest.setStatus("APPROVED");
        this.updateById(leaveRequest);

        // 生成請假紀錄（逐日展開）
        generateLeaveRecords(leaveRequest, approvedBy);

        // 更新年度特休配額
        updateAnnualLeaveQuota(leaveRequest);

        log.info("請假申請批准成功：ID={}", leaveRequestId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectLeaveRequest(Long leaveRequestId, Long approvedBy) {
        log.info("拒絕請假申請：申請ID={}, 審核人ID={}", leaveRequestId, approvedBy);

        LeaveRequest leaveRequest = this.getById(leaveRequestId);
        if (leaveRequest == null) {
            log.error("請假申請不存在：ID={}", leaveRequestId);
            throw new RuntimeException("請假申請不存在");
        }

        if (!"PENDING".equals(leaveRequest.getStatus())) {
            log.warn("請假申請狀態不正確：ID={}, 當前狀態={}", leaveRequestId, leaveRequest.getStatus());
            throw new RuntimeException("只能拒絕待審核的申請");
        }

        // 更新申請狀態
        leaveRequest.setStatus("REJECTED");
        this.updateById(leaveRequest);

        log.info("請假申請拒絕成功：ID={}", leaveRequestId);
        return true;
    }

    @Override
    public Integer getPendingRequestCount() {
        log.debug("獲取待審核的請假申請數量");
        return this.baseMapper.countPendingRequests();
    }

    @Override
    public boolean hasLeaveRequestOnDate(Long employeeId, LocalDate date) {
        Integer count = this.baseMapper.existsByEmployeeIdAndDate(employeeId, date);
        return count != null && count > 0;
    }

    @Override
    public String validateLeaveRequest(LeaveRequest leaveRequest) {
        // 驗證員工 ID
        if (leaveRequest.getEmployeeId() == null) {
            return "員工 ID 不能為空";
        }

        // 驗證假別類型 ID
        if (leaveRequest.getLeaveTypeId() == null) {
            return "假別類型 ID 不能為空";
        }

        // 驗證開始日期
        if (leaveRequest.getStartDate() == null) {
            return "開始日期不能為空";
        }

        // 驗證結束日期
        if (leaveRequest.getEndDate() == null) {
            return "結束日期不能為空";
        }

        // 驗證日期邏輯
        if (leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
            return "開始日期不能晚於結束日期";
        }

        // 驗證日期不能是過去的日期
        if (leaveRequest.getStartDate().isBefore(LocalDate.now())) {
            return "請假日期不能早於今天";
        }

        if (leaveRequest.getLeaveHours() != null && leaveRequest.getLeaveHours().compareTo(BigDecimal.ZERO) <= 0) {
            return "請假小時必須大於 0";
        }

        if (leaveRequest.getLeaveHours() != null) {
            BigDecimal maxHours = calculateDefaultLeaveHours(leaveRequest);
            if (leaveRequest.getLeaveHours().compareTo(maxHours) > 0) {
                return "請假小時不可大於請假日期可用工時";
            }
        }

        return null;
    }

    @Override
    public void applyLeave(LeaveRequest request) {

    }

    /**
     * 生成請假紀錄（逐日展開）
     */
    private void generateLeaveRecords(LeaveRequest leaveRequest, Long approvedBy) {
        log.info("生成請假紀錄：申請ID={}, 開始日期={}, 結束日期={}, leaveType={}",
                leaveRequest.getId(), leaveRequest.getStartDate(), leaveRequest.getEndDate(), leaveRequest.getLeaveType());

        LeaveTypeEntity typeEntity = leaveTypeMapper.selectById(leaveRequest.getLeaveTypeId());
        String currentEnum = typeEntity.getCode();

        log.info("生成請假紀錄：申請ID={}, 假別={}", leaveRequest.getId(), currentEnum);

        LocalDate currentDate = leaveRequest.getStartDate();
        List<LeaveRecord> records = new ArrayList<>();
        BigDecimal hoursPerDay = getHoursPerDay();
        BigDecimal remainingHours = calculateLeaveHours(leaveRequest);

        while (!currentDate.isAfter(leaveRequest.getEndDate()) && remainingHours.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal recordHours = remainingHours.min(hoursPerDay);
            LeaveRecord record = LeaveRecord.builder()
                    .employeeId(leaveRequest.getEmployeeId())
                    .leaveType(currentEnum)
                    .startDate(leaveRequest.getStartDate())
                    .endDate(leaveRequest.getEndDate())
                    .leaveDate(currentDate)
                    .leaveHours(recordHours)
                    .reason(leaveRequest.getReason())
                    .status("APPROVED")
                    .createdBy(leaveRequest.getEmployeeId())
                    .approvedBy(approvedBy)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            records.add(record);
            remainingHours = remainingHours.subtract(recordHours);
            currentDate = currentDate.plusDays(1);
        }

        // 批量保存請假紀錄
        leaveRecordService.saveBatch(records);
        log.info("生成請假紀錄完成，共 {} 條記錄", records.size());
    }

    /**
     * 更新年度特休配額
     */
    private void updateAnnualLeaveQuota(LeaveRequest leaveRequest) {
        log.info("更新年度特休配額：員工ID={}, 請假天數={}",
                leaveRequest.getEmployeeId(), leaveRequest.getDays());

        int year = leaveRequest.getStartDate().getYear();
        EmployeeAnnualLeaveDTO annualLeave = employeeAnnualLeaveService
                .getAnnualLeaveByEmployeeIdAndYear(leaveRequest.getEmployeeId(), year);

        if (annualLeave == null) {
            log.warn("員工年度特休配額不存在：員工ID={}, 年份={}", leaveRequest.getEmployeeId(), year);
            return;
        }

        // 計算已使用天數
        BigDecimal leaveHours = calculateLeaveHours(leaveRequest);
        BigDecimal currentUsedHours = annualLeave.getUsedHours() != null ? annualLeave.getUsedHours() : BigDecimal.ZERO;
        BigDecimal totalHours = annualLeave.getTotalHours() != null ? annualLeave.getTotalHours() : BigDecimal.ZERO;
        BigDecimal usedHours = currentUsedHours.add(leaveHours);
        BigDecimal remainHours = totalHours.subtract(usedHours);

        if (remainHours.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("特休配額不足：員工ID={}, 年份={}, 剩餘天數={}",
                    leaveRequest.getEmployeeId(), year, remainHours);
            throw new RuntimeException("特休配額不足");
        }

        // 更新配額
        annualLeave.setUsedHours(usedHours);
        annualLeave.setRemainHours(remainHours);
        employeeAnnualLeaveService.updateById(annualLeave);

        log.info("年度特休配額更新成功：員工ID={}, 已使用={}, 剩餘={}",
                leaveRequest.getEmployeeId(), usedHours, remainHours);
    }

    /**
     * 轉換為 DTO
     */
    private Page<LeaveRequestDTO> convertToDTO(Page<LeaveRequest> page) {
        Page<LeaveRequestDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<LeaveRequestDTO> dtoList = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    /**
     * 轉換單個對象為 DTO
     */
    private LeaveRequestDTO convertToDTO(LeaveRequest leaveRequest) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setId(leaveRequest.getId());
        dto.setEmployeeId(leaveRequest.getEmployeeId());
        dto.setEmployeeName(leaveRequest.getEmployeeName());
        dto.setLeaveTypeId(leaveRequest.getLeaveTypeId());
        dto.setLeaveTypeName(leaveRequest.getLeaveTypeName());
        dto.setLeaveHours(leaveRequest.getLeaveHours());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setStartTime(leaveRequest.getStartTime());
        dto.setEndTime(leaveRequest.getEndTime());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setDays(leaveRequest.getDays());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus());
        dto.setCreatedAt(leaveRequest.getCreatedAt());
        return dto;
    }

    private BigDecimal calculateLeaveHours(LeaveRequest leaveRequest) {
        if (leaveRequest.getLeaveHours() != null && leaveRequest.getLeaveHours().compareTo(BigDecimal.ZERO) > 0) {
            return leaveRequest.getLeaveHours();
        }
        return calculateDefaultLeaveHours(leaveRequest);
    }

    private BigDecimal calculateDefaultLeaveHours(LeaveRequest leaveRequest) {
        long daysBetween = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
        return BigDecimal.valueOf(daysBetween).multiply(getHoursPerDay());
    }

    private BigDecimal getHoursPerDay() {
        BigDecimal hoursPerDay = configService.getHoursPerDay();
        if (hoursPerDay == null || hoursPerDay.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("8");
        }
        return hoursPerDay;
    }
}
