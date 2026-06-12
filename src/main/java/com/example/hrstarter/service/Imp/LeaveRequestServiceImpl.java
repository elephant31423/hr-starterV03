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
import com.example.hrstarter.service.LeaveApprovalService;
import com.example.hrstarter.service.LeaveRecordService;
import com.example.hrstarter.service.LeaveRequestService;
import com.example.hrstarter.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    @Lazy
    private LeaveApprovalService leaveApprovalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitLeaveRequest(LeaveRequest leaveRequest) {
        String validationResult = validateLeaveRequest(leaveRequest);
        if (validationResult != null) {
            throw new RuntimeException(validationResult);
        }

        long daysBetween = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
        leaveRequest.setDays(BigDecimal.valueOf(daysBetween));
        if (leaveRequest.getLeaveHours() == null || leaveRequest.getLeaveHours().compareTo(BigDecimal.ZERO) <= 0) {
            leaveRequest.setLeaveHours(calculateLeaveHours(leaveRequest));
        }

        leaveRequest.setStatus("PENDING_DEPARTMENT");
        leaveRequest.setCreatedAt(LocalDateTime.now());

        boolean saved = this.save(leaveRequest);
        if (!saved) {
            throw new RuntimeException("保存請假申請失敗");
        }

        leaveApprovalService.createApprovalFlow(leaveRequest);
        return true;
    }

    @Override
    public Page<LeaveRequestDTO> getEmployeeLeaveRequests(Integer pageNum, Integer pageSize, Long employeeId) {
        Page<LeaveRequest> page = new Page<>(pageNum, pageSize);
        Page<LeaveRequest> result = this.baseMapper.selectByEmployeeId(page, employeeId);
        return convertToDTO(result);
    }

    @Override
    public Page<LeaveRequestDTO> getAllLeaveRequests(Integer pageNum, Integer pageSize, String status, String employeeName, String leaveType) {
        Page<LeaveRequest> page = new Page<>(pageNum, pageSize);
        Page<LeaveRequest> result = this.baseMapper.selectAllWithDetails(page, status, employeeName, leaveType);
        return convertToDTO(result);
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return this.baseMapper.selectByDateRange(employeeId, startDate, endDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveLeaveRequest(Long leaveRequestId, Long approvedBy) {
        LeaveRequest leaveRequest = this.getById(leaveRequestId);
        if (leaveRequest == null) {
            throw new RuntimeException("請假申請不存在");
        }
        if (!leaveRequest.getStatus().startsWith("PENDING")) {
            throw new RuntimeException("此請假申請目前不可核准");
        }
        finalizeApprovedLeave(leaveRequestId, approvedBy);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectLeaveRequest(Long leaveRequestId, Long approvedBy) {
        LeaveRequest leaveRequest = this.getById(leaveRequestId);
        if (leaveRequest == null) {
            throw new RuntimeException("請假申請不存在");
        }
        if (!leaveRequest.getStatus().startsWith("PENDING")) {
            throw new RuntimeException("此請假申請目前不可駁回");
        }
        leaveRequest.setStatus("REJECTED");
        this.updateById(leaveRequest);
        return true;
    }

    @Override
    public Integer getPendingRequestCount() {
        return this.baseMapper.countPendingRequests();
    }

    @Override
    public boolean hasLeaveRequestOnDate(Long employeeId, LocalDate date) {
        Integer count = this.baseMapper.existsByEmployeeIdAndDate(employeeId, date);
        return count != null && count > 0;
    }

    @Override
    public String validateLeaveRequest(LeaveRequest leaveRequest) {
        if (leaveRequest.getEmployeeId() == null) {
            return "員工 ID 不可為空";
        }
        if (leaveRequest.getLeaveTypeId() == null) {
            return "假別 ID 不可為空";
        }
        if (leaveRequest.getStartDate() == null) {
            return "開始日期不可為空";
        }
        if (leaveRequest.getEndDate() == null) {
            return "結束日期不可為空";
        }
        if (leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
            return "開始日期不可晚於結束日期";
        }
        if (leaveRequest.getStartDate().isBefore(LocalDate.now())) {
            return "請假日期不可早於今天";
        }
        if (leaveRequest.getLeaveHours() != null && leaveRequest.getLeaveHours().compareTo(BigDecimal.ZERO) <= 0) {
            return "請假時數必須大於 0";
        }
        if (leaveRequest.getLeaveHours() != null) {
            BigDecimal maxHours = calculateDefaultLeaveHours(leaveRequest);
            if (leaveRequest.getLeaveHours().compareTo(maxHours) > 0) {
                return "請假時數不可大於日期區間可用時數";
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyLeave(LeaveRequest request) {
        if (request == null) {
            throw new RuntimeException("請假申請不可為空");
        }

        if (request.getEmployeeId() == null) {
            Long loginEmployeeId = SecurityUtils.getEmployeeId();
            if (loginEmployeeId == null) {
                throw new RuntimeException("找不到登入員工");
            }
            request.setEmployeeId(loginEmployeeId);
        }

        LeaveTypeEntity leaveType = leaveTypeMapper.selectById(request.getLeaveTypeId());
        if (leaveType == null) {
            throw new RuntimeException("假別不存在");
        }

        submitLeaveRequest(request);

        if (Boolean.FALSE.equals(leaveType.getNeedApprove())) {
            Long approvedBy = SecurityUtils.getUserId();
            if (approvedBy == null) {
                approvedBy = request.getEmployeeId();
            }
            finalizeApprovedLeave(request.getId(), approvedBy);
        }
    }

    @Override
    public void updateApprovalStatus(Long leaveRequestId, String status) {
        LeaveRequest leaveRequest = this.getById(leaveRequestId);
        if (leaveRequest == null) {
            throw new RuntimeException("請假申請不存在");
        }
        leaveRequest.setStatus(status);
        this.updateById(leaveRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finalizeApprovedLeave(Long leaveRequestId, Long approvedBy) {
        LeaveRequest leaveRequest = this.getById(leaveRequestId);
        if (leaveRequest == null) {
            throw new RuntimeException("請假申請不存在");
        }
        if ("APPROVED".equals(leaveRequest.getStatus())) {
            return;
        }

        leaveRequest.setStatus("APPROVED");
        this.updateById(leaveRequest);
        generateLeaveRecords(leaveRequest, approvedBy);
        updateAnnualLeaveQuota(leaveRequest);
    }

    private void generateLeaveRecords(LeaveRequest leaveRequest, Long approvedBy) {
        LeaveTypeEntity typeEntity = leaveTypeMapper.selectById(leaveRequest.getLeaveTypeId());
        String leaveTypeCode = typeEntity.getCode();

        LocalDate currentDate = leaveRequest.getStartDate();
        List<LeaveRecord> records = new ArrayList<>();
        BigDecimal hoursPerDay = getHoursPerDay();
        BigDecimal remainingHours = calculateLeaveHours(leaveRequest);

        while (!currentDate.isAfter(leaveRequest.getEndDate()) && remainingHours.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal recordHours = remainingHours.min(hoursPerDay);
            LeaveRecord record = LeaveRecord.builder()
                    .employeeId(leaveRequest.getEmployeeId())
                    .leaveType(leaveTypeCode)
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

        leaveRecordService.saveBatch(records);
    }

    private void updateAnnualLeaveQuota(LeaveRequest leaveRequest) {
        int year = leaveRequest.getStartDate().getYear();
        EmployeeAnnualLeaveDTO annualLeave = employeeAnnualLeaveService
                .getAnnualLeaveByEmployeeIdAndYear(leaveRequest.getEmployeeId(), year);

        if (annualLeave == null) {
            log.warn("找不到員工特休資料，employeeId={}, year={}", leaveRequest.getEmployeeId(), year);
            return;
        }

        BigDecimal leaveHours = calculateLeaveHours(leaveRequest);
        BigDecimal currentUsedHours = annualLeave.getUsedHours() != null ? annualLeave.getUsedHours() : BigDecimal.ZERO;
        BigDecimal totalHours = annualLeave.getTotalHours() != null ? annualLeave.getTotalHours() : BigDecimal.ZERO;
        BigDecimal usedHours = currentUsedHours.add(leaveHours);
        BigDecimal remainHours = totalHours.subtract(usedHours);

        if (remainHours.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("特休時數不足");
        }

        annualLeave.setUsedHours(usedHours);
        annualLeave.setRemainHours(remainHours);
        employeeAnnualLeaveService.updateById(annualLeave);
    }

    private Page<LeaveRequestDTO> convertToDTO(Page<LeaveRequest> page) {
        Page<LeaveRequestDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<LeaveRequestDTO> dtoList = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

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
