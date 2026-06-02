package com.example.hrstarter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.hrstarter.entity.LeaveRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 請假紀錄 Mapper 接口
 *
 * @author HR System
 * @date 2024-01-30
 */
@Mapper
public interface LeaveRecordMapper extends BaseMapper<LeaveRecord> {

    /**
     * 分頁查詢員工的請假紀錄
     *
     * @param page 分頁參數
     * @param employeeId 員工 ID
     * @return 分頁結果
     */
    Page<LeaveRecord> selectByEmployeeId(Page<LeaveRecord> page, @Param("employeeId") Long employeeId);

    /**
     * 分頁查詢指定日期範圍內的請假紀錄
     *
     * @param page 分頁參數
     * @param employeeId 員工 ID
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @return 分頁結果
     */
    Page<LeaveRecord> selectByDateRange(
            Page<LeaveRecord> page,
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 查詢指定日期範圍內的所有請假紀錄
     *
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @param leaveType 假別類型（可選）
     * @return 請假紀錄列表
     */
    List<LeaveRecord> selectByDateRangeAndType(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("leaveType") String leaveType
    );

    /**
     * 查詢員工在指定日期的請假紀錄
     *
     * @param employeeId 員工 ID
     * @param leaveDate 請假日期
     * @return 請假紀錄列表
     */
    List<LeaveRecord> selectByEmployeeIdAndDate(
            @Param("employeeId") Long employeeId,
            @Param("leaveDate") LocalDate leaveDate
    );

    /**
     * 計算員工在指定日期範圍內的請假時數
     *
     * @param employeeId 員工 ID
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @param leaveType 假別類型
     * @return 請假時數
     */
    BigDecimal sumLeaveHours(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("leaveType") String leaveType
    );

    /**
     * 計算員工在指定日期範圍內的請假天數
     *
     * @param employeeId 員工 ID
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @param leaveType 假別類型
     * @return 請假天數
     */
    Integer countLeaveDays(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("leaveType") String leaveType
    );

    /**
     * 查詢待審核的請假紀錄數量
     *
     * @return 數量
     */
    @Select("SELECT COUNT(*) FROM leave_records WHERE status = 'PENDING'")
    Integer countPendingRecords();

    /**
     * 查詢員工在指定月份的請假紀錄
     *
     * @param employeeId 員工 ID
     * @param year 年份
     * @param month 月份
     * @return 請假紀錄列表
     */
    List<LeaveRecord> selectByEmployeeIdAndMonth(
            @Param("employeeId") Long employeeId,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    /**
     * 批量查詢多個員工的請假紀錄
     *
     * @param employeeIds 員工 ID 列表
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @return 請假紀錄列表
     */
    List<LeaveRecord> selectByEmployeeIdsAndDateRange(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<LeaveRecord> findByEmployeeAndDateRange(@Param("employeeId")Long employeeId, @Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);

    Long countVacation();
}