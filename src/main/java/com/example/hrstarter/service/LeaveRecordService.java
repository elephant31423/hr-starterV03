package com.example.hrstarter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hrstarter.dto.LeaveRecordDTO;
import com.example.hrstarter.entity.LeaveRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 請假紀錄業務邏輯接口
 *
 * @author HR System
 * @date 2024-01-30
 */
public interface LeaveRecordService extends IService<LeaveRecord> {

    /**
     * 分頁查詢員工的請假紀錄
     *
     * @param pageNum 頁碼
     * @param pageSize 每頁數量
     * @param employeeId 員工 ID
     * @return 分頁結果
     */
    Page<LeaveRecordDTO> getEmployeeLeaveRecords(Integer pageNum, Integer pageSize, Long employeeId);

    /**
     * 分頁查詢指定日期範圍內的請假紀錄
     *
     * @param pageNum 頁碼
     * @param pageSize 每頁數量
     * @param employeeId 員工 ID
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @return 分頁結果
     */
    Page<LeaveRecordDTO> getLeaveRecordsByDateRange(Integer pageNum, Integer pageSize,
                                                    Long employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 查詢指定日期範圍內的所有請假紀錄
     *
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @param leaveType 假別類型（可選）
     * @return 請假紀錄列表
     */
    List<LeaveRecord> getLeaveRecordsByDateRangeAndType(LocalDate startDate, LocalDate endDate, String leaveType);

    /**
     * 查詢員工在指定日期的請假紀錄
     *
     * @param employeeId 員工 ID
     * @param leaveDate 請假日期
     * @return 請假紀錄列表
     */
    List<LeaveRecord> getLeaveRecordsByEmployeeIdAndDate(Long employeeId, LocalDate leaveDate);

    /**
     * 計算員工在指定日期範圍內的請假時數
     *
     * @param employeeId 員工 ID
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @param leaveType 假別類型
     * @return 請假時數
     */
    BigDecimal calculateLeaveHours(Long employeeId, LocalDate startDate, LocalDate endDate, String leaveType);

    /**
     * 計算員工在指定日期範圍內的請假天數
     *
     * @param employeeId 員工 ID
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @param leaveType 假別類型
     * @return 請假天數
     */
    Integer calculateLeaveDays(Long employeeId, LocalDate startDate, LocalDate endDate, String leaveType);

    /**
     * 獲取待審核的請假紀錄數量
     *
     * @return 數量
     */
    Integer getPendingRecordCount();

    /**
     * 查詢員工在指定月份的請假紀錄
     *
     * @param employeeId 員工 ID
     * @param year 年份
     * @param month 月份
     * @return 請假紀錄列表
     */
    List<LeaveRecord> getLeaveRecordsByMonth(Long employeeId, Integer year, Integer month);

    /**
     * 查詢多個員工在指定日期範圍內的請假紀錄
     *
     * @param employeeIds 員工 ID 列表
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @return 請假紀錄列表
     */
    List<LeaveRecord> getLeaveRecordsByEmployeeIdsAndDateRange(List<Long> employeeIds,
                                                               LocalDate startDate, LocalDate endDate);
}