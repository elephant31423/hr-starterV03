package com.example.hrstarter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.LeaveRecordDTO;
import com.example.hrstarter.entity.LeaveRecord;
import com.example.hrstarter.service.LeaveRecordService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 請假紀錄控制器
 *
 * @author HR System
 * @date 2024-01-30
 */
@Slf4j
@RestController
@RequestMapping("/api/leave-records")
//@Tag(name = "請假紀錄管理", description = "請假紀錄相關 API")
public class LeaveRecordController {

    @Autowired
    private LeaveRecordService leaveRecordService;

    /**
     * 分頁查詢員工的請假紀錄
     */
    @GetMapping("/employee/{employeeId}")
//    @Operation(summary = "查詢員工請假紀錄", description = "分頁查詢指定員工的所有請假紀錄")
    public ApiResponse<Page<LeaveRecordDTO>> getEmployeeLeaveRecords(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查詢員工請假紀錄：員工ID={}, 頁碼={}, 每頁數量={}", employeeId, pageNum, pageSize);

        Page<LeaveRecordDTO> page = leaveRecordService.getEmployeeLeaveRecords(pageNum, pageSize, employeeId);
        return ApiResponse.success("查詢成功", page);
    }

    /**
     * 分頁查詢日期範圍內的請假紀錄
     */
    @GetMapping("/range")
//    @Operation(summary = "查詢日期範圍內的請假紀錄", description = "分頁查詢指定員工在指定日期範圍內的請假紀錄")
    public ApiResponse<Page<LeaveRecordDTO>> getLeaveRecordsByDateRange(
            @RequestParam Long employeeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查詢日期範圍內的請假紀錄：員工ID={}, 開始日期={}, 結束日期={}, 頁碼={}, 每頁數量={}",
                employeeId, startDate, endDate, pageNum, pageSize);

        Page<LeaveRecordDTO> page = leaveRecordService.getLeaveRecordsByDateRange(pageNum, pageSize, employeeId, startDate, endDate);
        return ApiResponse.success("查詢成功", page);
    }

    /**
     * 查詢指定日期範圍內的所有請假紀錄
     */
    @GetMapping("/all-range")
//    @Operation(summary = "查詢所有請假紀錄", description = "查詢指定日期範圍內的所有請假紀錄，支持按假別篩選")
    public ApiResponse<List<LeaveRecord>> getLeaveRecordsByDateRangeAndType(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String leaveType) {
        log.info("查詢日期範圍內的所有請假紀錄：開始日期={}, 結束日期={}, 假別類型={}", startDate, endDate, leaveType);

        List<LeaveRecord> list = leaveRecordService.getLeaveRecordsByDateRangeAndType(startDate, endDate, leaveType);
        return ApiResponse.success("查詢成功", list);
    }

    /**
     * 查詢員工在指定日期的請假紀錄
     */
    @GetMapping("/date")
//    @Operation(summary = "查詢指定日期的請假紀錄", description = "查詢員工在指定日期的請假紀錄")
    public ApiResponse<List<LeaveRecord>> getLeaveRecordsByEmployeeIdAndDate(
            @RequestParam Long employeeId,
            @RequestParam LocalDate leaveDate) {
        log.info("查詢員工指定日期的請假紀錄：員工ID={}, 請假日期={}", employeeId, leaveDate);

        List<LeaveRecord> list = leaveRecordService.getLeaveRecordsByEmployeeIdAndDate(employeeId, leaveDate);
        return ApiResponse.success("查詢成功", list);
    }

    /**
     * 計算員工請假時數
     */
    @GetMapping("/hours")
//    @Operation(summary = "計算請假時數", description = "計算員工在指定日期範圍內的請假時數")
    public ApiResponse<BigDecimal> calculateLeaveHours(
            @RequestParam Long employeeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String leaveType) {
        log.info("計算員工請假時數：員工ID={}, 開始日期={}, 結束日期={}, 假別類型={}",
                employeeId, startDate, endDate, leaveType);

        BigDecimal leaveHours = leaveRecordService.calculateLeaveHours(employeeId, startDate, endDate, leaveType);
        return ApiResponse.success("計算成功", leaveHours);
    }

    /**
     * 計算員工請假天數
     */
    @GetMapping("/days")
//    @Operation(summary = "計算請假天數", description = "計算員工在指定日期範圍內的請假天數")
    public ApiResponse<Integer> calculateLeaveDays(
            @RequestParam Long employeeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String leaveType) {
        log.info("計算員工請假天數：員工ID={}, 開始日期={}, 結束日期={}, 假別類型={}",
                employeeId, startDate, endDate, leaveType);

        Integer leaveDays = leaveRecordService.calculateLeaveDays(employeeId, startDate, endDate, leaveType);
        return ApiResponse.success("計算成功", leaveDays);
    }

    /**
     * 查詢員工指定月份的請假紀錄
     */
    @GetMapping("/month")
//    @Operation(summary = "查詢月份請假紀錄", description = "查詢員工指定月份的請假紀錄")
    public ApiResponse<List<LeaveRecord>> getLeaveRecordsByMonth(
            @RequestParam Long employeeId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        log.info("查詢員工指定月份的請假紀錄：員工ID={}, 年份={}, 月份={}", employeeId, year, month);

        List<LeaveRecord> list = leaveRecordService.getLeaveRecordsByMonth(employeeId, year, month);
        return ApiResponse.success("查詢成功", list);
    }

    /**
     * 查詢多個員工的請假紀錄
     */
    @PostMapping("/batch")
//    @Operation(summary = "批量查詢請假紀錄", description = "查詢多個員工在指定日期範圍內的請假紀錄")
    public ApiResponse<List<LeaveRecord>> getLeaveRecordsByEmployeeIdsAndDateRange(
            @RequestBody List<Long> employeeIds,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.info("批量查詢員工請假紀錄：員工ID列表={}, 開始日期={}, 結束日期={}", employeeIds, startDate, endDate);

        List<LeaveRecord> list = leaveRecordService.getLeaveRecordsByEmployeeIdsAndDateRange(employeeIds, startDate, endDate);
        return ApiResponse.success("查詢成功", list);
    }

    /**
     * 獲取待審核的請假紀錄數量
     */
    @GetMapping("/pending/count")
//    @Operation(summary = "獲取待審核數量", description = "獲取待審核的請假紀錄數量")
    public ApiResponse<Integer> getPendingRecordCount() {
        log.debug("獲取待審核的請假紀錄數量");

        Integer count = leaveRecordService.getPendingRecordCount();
        return ApiResponse.success("查詢成功", count);
    }
}