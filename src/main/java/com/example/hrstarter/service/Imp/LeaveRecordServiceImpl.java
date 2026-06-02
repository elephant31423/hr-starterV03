package com.example.hrstarter.service.Imp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.hrstarter.dto.LeaveRecordDTO;
import com.example.hrstarter.entity.LeaveRecord;
import com.example.hrstarter.mapper.LeaveRecordMapper;
import com.example.hrstarter.service.LeaveRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 請假紀錄業務邏輯實現類
 *
 * @author HR System
 * @date 2024-01-30
 */
@Slf4j
@Service
public class LeaveRecordServiceImpl extends ServiceImpl<LeaveRecordMapper, LeaveRecord> implements LeaveRecordService {

    @Override
    public Page<LeaveRecordDTO> getEmployeeLeaveRecords(Integer pageNum, Integer pageSize, Long employeeId) {
        log.info("查詢員工請假紀錄：員工ID={}, 頁碼={}, 每頁數量={}", employeeId, pageNum, pageSize);

        Page<LeaveRecord> page = new Page<>(pageNum, pageSize);
        Page<LeaveRecord> result = this.baseMapper.selectByEmployeeId(page, employeeId);

        return convertToDTO(result);
    }

    @Override
    public Page<LeaveRecordDTO> getLeaveRecordsByDateRange(Integer pageNum, Integer pageSize,
                                                           Long employeeId, LocalDate startDate, LocalDate endDate) {
        log.info("查詢日期範圍內的請假紀錄：員工ID={}, 開始日期={}, 結束日期={}, 頁碼={}, 每頁數量={}",
                employeeId, startDate, endDate, pageNum, pageSize);

        Page<LeaveRecord> page = new Page<>(pageNum, pageSize);
        Page<LeaveRecord> result = this.baseMapper.selectByDateRange(page, employeeId, startDate, endDate);

        return convertToDTO(result);
    }

    @Override
    public List<LeaveRecord> getLeaveRecordsByDateRangeAndType(LocalDate startDate, LocalDate endDate, String leaveType) {
        log.info("查詢日期範圍和假別類型的請假紀錄：開始日期={}, 結束日期={}, 假別類型={}",
                startDate, endDate, leaveType);

        return this.baseMapper.selectByDateRangeAndType(startDate, endDate, leaveType);
    }

    @Override
    public List<LeaveRecord> getLeaveRecordsByEmployeeIdAndDate(Long employeeId, LocalDate leaveDate) {
        log.info("查詢員工指定日期的請假紀錄：員工ID={}, 請假日期={}", employeeId, leaveDate);

        return this.baseMapper.selectByEmployeeIdAndDate(employeeId, leaveDate);
    }

    @Override
    public BigDecimal calculateLeaveHours(Long employeeId, LocalDate startDate, LocalDate endDate, String leaveType) {
        log.info("計算員工請假時數：員工ID={}, 開始日期={}, 結束日期={}, 假別類型={}",
                employeeId, startDate, endDate, leaveType);

        BigDecimal leaveHours = this.baseMapper.sumLeaveHours(employeeId, startDate, endDate, leaveType);
        return leaveHours != null ? leaveHours : BigDecimal.ZERO;
    }

    @Override
    public Integer calculateLeaveDays(Long employeeId, LocalDate startDate, LocalDate endDate, String leaveType) {
        log.info("計算員工請假天數：員工ID={}, 開始日期={}, 結束日期={}, 假別類型={}",
                employeeId, startDate, endDate, leaveType);

        Integer leaveDays = this.baseMapper.countLeaveDays(employeeId, startDate, endDate, leaveType);
        return leaveDays != null ? leaveDays : 0;
    }

    @Override
    public Integer getPendingRecordCount() {
        log.debug("獲取待審核的請假紀錄數量");
        return this.baseMapper.countPendingRecords();
    }

    @Override
    public List<LeaveRecord> getLeaveRecordsByMonth(Long employeeId, Integer year, Integer month) {
        log.info("查詢員工指定月份的請假紀錄：員工ID={}, 年份={}, 月份={}", employeeId, year, month);

        return this.baseMapper.selectByEmployeeIdAndMonth(employeeId, year, month);
    }

    @Override
    public List<LeaveRecord> getLeaveRecordsByEmployeeIdsAndDateRange(List<Long> employeeIds,
                                                                      LocalDate startDate, LocalDate endDate) {
        log.info("查詢多個員工的請假紀錄：員工ID列表={}, 開始日期={}, 結束日期={}",
                employeeIds, startDate, endDate);

        return this.baseMapper.selectByEmployeeIdsAndDateRange(employeeIds, startDate, endDate);
    }

    /**
     * 轉換為 DTO
     */
    private Page<LeaveRecordDTO> convertToDTO(Page<LeaveRecord> page) {
        Page<LeaveRecordDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<LeaveRecordDTO> dtoList = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    /**
     * 轉換單個對象為 DTO
     */
    private LeaveRecordDTO convertToDTO(LeaveRecord leaveRecord) {
        LeaveRecordDTO dto = new LeaveRecordDTO();
        dto.setId(leaveRecord.getId());
        dto.setEmployeeId(leaveRecord.getEmployeeId());
        dto.setEmployeeName(leaveRecord.getEmployeeName());
        dto.setLeaveType(leaveRecord.getLeaveType());
        dto.setStartDate(leaveRecord.getStartDate());
        dto.setEndDate(leaveRecord.getEndDate());
        dto.setLeaveDate(leaveRecord.getLeaveDate());
        dto.setLeaveHours(leaveRecord.getLeaveHours());
        dto.setReason(leaveRecord.getReason());
        dto.setStatus(leaveRecord.getStatus());
        dto.setCreatedByName(leaveRecord.getCreatedByName());
        dto.setApprovedByName(leaveRecord.getApprovedByName());
        dto.setCreatedAt(leaveRecord.getCreatedAt());
        dto.setUpdatedAt(leaveRecord.getUpdatedAt());
        return dto;
    }
}