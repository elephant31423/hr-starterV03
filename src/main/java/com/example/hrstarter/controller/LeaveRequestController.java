package com.example.hrstarter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.LeaveRequestDTO;
import com.example.hrstarter.entity.LeaveRequest;
import com.example.hrstarter.enums.ErrorCode;
import com.example.hrstarter.service.LeaveRequestService;
import com.example.hrstarter.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 請假申請控制器
 *
 * @author HR System
 * @date 2024-01-30
 */
@Slf4j
@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private NotificationService notificationService;

    /**
     * 提交請假申請
     */
    @PostMapping
//    @Operation(summary = "提交請假申請", description = "員工提交新的請假申請")
    public ApiResponse<LeaveRequest> submitLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        log.info("提交請假申請：員工ID={}, 開始日期={}, 結束日期={} leaveRequest={}",
                leaveRequest.getEmployeeId(), leaveRequest.getStartDate(), leaveRequest.getEndDate(),leaveRequest);


        leaveRequestService.submitLeaveRequest(leaveRequest);
        notifyHrLeavePending();
        return ApiResponse.success("請假申請提交成功", leaveRequest);

    }

    /**
     * 獲取請假申請詳情
     */
    @GetMapping("/{id}")
//    @Operation(summary = "獲取請假申請詳情", description = "根據 ID 獲取請假申請的詳細信息")
    public ApiResponse<LeaveRequest> getLeaveRequest(
            @PathVariable Long id) {
        log.info("獲取請假申請詳情：ID={}", id);

        LeaveRequest leaveRequest = leaveRequestService.getById(id);


        return ApiResponse.success("獲取成功", leaveRequest);
    }

    /**
     * 分頁查詢員工的請假申請
     */
    @GetMapping("/employee/{employeeId}")
//    @Operation(summary = "查詢員工請假申請", description = "分頁查詢指定員工的所有請假申請")
    public ApiResponse<Page<LeaveRequestDTO>> getEmployeeLeaveRequests(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查詢員工請假申請：員工ID={}, 頁碼={}, 每頁數量={}", employeeId, pageNum, pageSize);

        Page<LeaveRequestDTO> page = leaveRequestService.getEmployeeLeaveRequests(pageNum, pageSize, employeeId);
        return ApiResponse.success("查詢成功", page);
    }

    /**
     * 分頁查詢所有請假申請
     */
    @GetMapping
    public ApiResponse<Page<LeaveRequestDTO>> getAllLeaveRequests(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String leaveType)
    {
        log.info("查詢所有請假申請：狀態={}, 頁碼={}, 每頁數量={},員工姓名={},假別={}", status, pageNum, pageSize, employeeName, leaveType);

        Page<LeaveRequestDTO> page = leaveRequestService.getAllLeaveRequests(pageNum, pageSize, status, employeeName, leaveType);
        log.info("查詢結果：總記錄數={}, 總頁數={}, 當前頁數={}, 每頁數量={}, pagegetRecords={}",
                page.getTotal(), page.getPages(), page.getCurrent(), page.getSize(),page.getRecords());

        return ApiResponse.success("查詢成功", page);
    }

    /**
     * 查詢日期範圍內的請假申請
     */
    @GetMapping("/range")
//    @Operation(summary = "查詢日期範圍內的請假申請", description = "查詢指定員工在指定日期範圍內的請假申請")
    public ApiResponse<List<LeaveRequest>> getLeaveRequestsByDateRange(
            @RequestParam Long employeeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.info("查詢日期範圍內的請假申請：員工ID={}, 開始日期={}, 結束日期={}", employeeId, startDate, endDate);

        List<LeaveRequest> list = leaveRequestService.getLeaveRequestsByDateRange(employeeId, startDate, endDate);
        return ApiResponse.success("查詢成功", list);
    }

    /**
     * 更新請假申請
     */
    @PutMapping("/{id}")
//    @Operation(summary = "更新請假申請", description = "更新待審核的請假申請信息")
    public ApiResponse<Boolean> updateLeaveRequest(
            @PathVariable Long id,
            @RequestBody LeaveRequest leaveRequest) {
        log.info("更新請假申請：ID={}", id);

        leaveRequest.setId(id);
        boolean updated = leaveRequestService.updatePendingByApplicant(id, leaveRequest);

        return ApiResponse.success("更新成功", true);

    }

    /**
     * 刪除請假申請
     */
    @DeleteMapping("/{id}")
//    @Operation(summary = "刪除請假申請", description = "刪除待審核的請假申請")
    public ApiResponse<Boolean> deleteLeaveRequest(
            @PathVariable Long id) {
        log.info("刪除請假申請：ID={}", id);

        boolean deleted = leaveRequestService.cancelPendingByApplicant(id);

        return ApiResponse.success("刪除成功", true);

    }

    /**
     * 批准請假申請
     */
    @PutMapping("/{id}/approve")
//    @Operation(summary = "批准請假申請", description = "經理批准員工的請假申請")
    public ApiResponse<Boolean> approveLeaveRequest(
            @PathVariable Long id,
            @RequestParam Long approvedBy) {
        log.info("批准請假申請：ID={}, 審核人ID={}", id, approvedBy);


        boolean approved = leaveRequestService.approveLeaveRequest(id, approvedBy);
        return ApiResponse.success("批准成功", approved);

    }

    /**
     * 拒絕請假申請
     */
    @PutMapping("/{id}/reject")
//    @Operation(summary = "拒絕請假申請", description = "經理拒絕員工的請假申請")
    public ApiResponse<Boolean> rejectLeaveRequest(
            @PathVariable Long id,
            @RequestParam Long approvedBy) {
        log.info("拒絕請假申請：ID={}, 審核人ID={}", id, approvedBy);


        boolean rejected = leaveRequestService.rejectLeaveRequest(id, approvedBy);
        return ApiResponse.success("拒絕成功", rejected);

    }

    /**
     * 獲取待審核的請假申請數量
     */
    @GetMapping("/pending/count")
//    @Operation(summary = "獲取待審核數量", description = "獲取待審核的請假申請數量")
    public ApiResponse<Integer> getPendingRequestCount() {
        log.debug("獲取待審核的請假申請數量");

        Integer count = leaveRequestService.getPendingRequestCount();
        return ApiResponse.success("查詢成功", count);
    }

    /**
     * 提交請假申請 (增加審計日誌)
     */
    @com.example.hrstarter.annotation.AuditLog(action = "CREATE", entityType = "LEAVE_REQUEST", idParam = "id")
    @PostMapping("/submit")
    public ApiResponse<LeaveRequest> submitQuickLeave(@RequestBody LeaveRequest leaveRequest) {
        log.info("接收快速請假申請：員工ID={}, 類型={}, 開始={}, 結束={}, 總小時={}",
                leaveRequest.getEmployeeId(), leaveRequest.getLeaveType(),
                leaveRequest.getStartTime(), leaveRequest.getEndTime(), leaveRequest.getLeaveHours());

        // 核心邏輯應在 Service 中判斷：1. 剩餘時數是否足夠 2. 是否重複申請 3. 扣除特休
        leaveRequestService.submitLeaveRequest(leaveRequest);
        notifyHrLeavePending();
        return ApiResponse.success("請假申請已提交", leaveRequest);
    }

    private void notifyHrLeavePending() {
        notificationService.notifyRole(
                "HR",
                "LEAVE_PENDING",
                "新的待辦事項",
                "有新的請假申請需要處理。"
        );
    }
}
