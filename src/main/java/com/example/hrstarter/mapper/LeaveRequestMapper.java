package com.example.hrstarter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.hrstarter.entity.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 請假申請 Mapper 接口
 *
 * @author HR System
 * @date 2024-01-30
 */
@Mapper
public interface LeaveRequestMapper extends BaseMapper<LeaveRequest> {

    /**
     * 分頁查詢員工的請假申請
     *
     * @param page 分頁參數
     * @param employeeId 員工 ID
     * @return 分頁結果
     */
    Page<LeaveRequest> selectByEmployeeId(Page<LeaveRequest> page, @Param("employeeId") Long employeeId);

    /**
     * 分頁查詢所有請假申請（帶員工和假別信息）
     *
     * @param page 分頁參數
     * @param status 申請狀態（可選）
     * @return 分頁結果
     */
    Page<LeaveRequest> selectAllWithDetails(Page<LeaveRequest> page, @Param("status") String status, String employeeName, String leaveType);

    /**
     * 查詢指定日期範圍內的請假申請
     *
     * @param employeeId 員工 ID
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @return 請假申請列表
     */
    List<LeaveRequest> selectByDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 查詢待審核的請假申請數量
     *
     * @return 數量
     */
    @Select("SELECT COUNT(*) FROM leave_requests WHERE status LIKE 'PENDING%'")
    Integer countPendingRequests();

    /**
     * 查詢指定員工在指定日期是否有請假申請
     *
     * @param employeeId 員工 ID
     * @param date 日期
     * @return 是否存在
     */
    Integer existsByEmployeeIdAndDate(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date
    );

    /**
     * 批量查詢員工的請假申請
     *
     * @param employeeIds 員工 ID 列表
     * @return 請假申請列表
     */
    List<LeaveRequest> selectByEmployeeIds(@Param("employeeIds") List<Long> employeeIds);
}
