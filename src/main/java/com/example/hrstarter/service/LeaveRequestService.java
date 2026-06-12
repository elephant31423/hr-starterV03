package com.example.hrstarter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hrstarter.dto.LeaveRequestDTO;
import com.example.hrstarter.entity.LeaveRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * 請假申請業務邏輯接口
 *
 * @author HR System
 * @date 2024-01-30
 */
public interface LeaveRequestService extends IService<LeaveRequest> {

    /**
     * 提交請假申請
     *
     * @param leaveRequest 請假申請
     * @return 是否成功
     */
    boolean submitLeaveRequest(LeaveRequest leaveRequest);

    /**
     * 分頁查詢員工的請假申請
     *
     * @param pageNum    頁碼
     * @param pageSize   每頁數量
     * @param employeeId 員工 ID
     * @return 分頁結果
     */
    Page<LeaveRequestDTO> getEmployeeLeaveRequests(Integer pageNum, Integer pageSize, Long employeeId);

    /**
     * 分頁查詢所有請假申請
     *
     * @param pageNum      頁碼
     * @param pageSize     每頁數量
     * @param status       申請狀態（可選）
     * @param employeeName 員工姓名（可選）
     * @param leaveType    請假類型（可選）
     * @return 分頁結果
     */
    Page<LeaveRequestDTO> getAllLeaveRequests(Integer pageNum, Integer pageSize, String status, String employeeName, String leaveType);

    /**
     * 查詢指定日期範圍內的請假申請
     *
     * @param employeeId 員工 ID
     * @param startDate  開始日期
     * @param endDate    結束日期
     * @return 請假申請列表
     */
    List<LeaveRequest> getLeaveRequestsByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 批准請假申請
     *
     * @param leaveRequestId 請假申請 ID
     * @param approvedBy     審核人 ID
     * @return 是否成功
     */
    boolean approveLeaveRequest(Long leaveRequestId, Long approvedBy);

    /**
     * 拒絕請假申請
     *
     * @param leaveRequestId 請假申請 ID
     * @param approvedBy     審核人 ID
     * @return 是否成功
     */
    boolean rejectLeaveRequest(Long leaveRequestId, Long approvedBy);

    /**
     * 獲取待審核的請假申請數量
     *
     * @return 數量
     */
    Integer getPendingRequestCount();

    /**
     * 檢查員工在指定日期是否有請假申請
     *
     * @param employeeId 員工 ID
     * @param date       日期
     * @return 是否存在
     */
    boolean hasLeaveRequestOnDate(Long employeeId, LocalDate date);

    /**
     * 驗證請假申請
     *
     * @param leaveRequest 請假申請
     * @return 驗證結果
     */
    String validateLeaveRequest(LeaveRequest leaveRequest);

    void applyLeave(LeaveRequest request);

    void updateApprovalStatus(Long leaveRequestId, String status);

    void finalizeApprovedLeave(Long leaveRequestId, Long approvedBy);
}
