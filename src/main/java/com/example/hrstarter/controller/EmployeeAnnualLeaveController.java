package com.example.hrstarter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.EmployeeAnnualLeaveDTO;
import com.example.hrstarter.entity.EmployeeAnnualLeaves;
import com.example.hrstarter.service.EmployeeAnnualLeaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 員工年度特休控制器
 *
 * @author HR System
 * @date 2024-01-30
 */
@Slf4j
@RestController
@RequestMapping("/api/annual-leave")

public class EmployeeAnnualLeaveController {

    @Autowired
    private EmployeeAnnualLeaveService employeeAnnualLeaveService;

    /**
     * 獲取員工指定年份的特休配額
     */
    @GetMapping("/{employeeId}/{year}")

    public ApiResponse<EmployeeAnnualLeaveDTO> getAnnualLeaveByEmployeeIdAndYear(
            @PathVariable Long employeeId,
            @PathVariable Integer year) {
        log.info("獲取員工年度特休配額：員工ID={}, 年份={}", employeeId, year);

        EmployeeAnnualLeaveDTO annualLeave = employeeAnnualLeaveService.getAnnualLeaveByEmployeeIdAndYear(employeeId, year);
        return ApiResponse.success("查詢成功", annualLeave);
    }

    /**
     * 分頁查詢員工所有年度的特休配額
     */
    @GetMapping("/employee/{employeeId}")
    public ApiResponse<Page<EmployeeAnnualLeaveDTO>> getEmployeeAnnualLeaves(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查詢員工所有年度特休配額：員工ID={}, 頁碼={}, 每頁數量={}", employeeId, pageNum, pageSize);

        Page<EmployeeAnnualLeaveDTO> page = employeeAnnualLeaveService.getEmployeeAnnualLeaves(pageNum, pageSize, employeeId);
        return ApiResponse.success("查詢成功", page);
    }

    /**
     * 分頁查詢指定年份所有員工的特休配額
     */
    @GetMapping("/year/{year}")

    public ApiResponse<Page<EmployeeAnnualLeaveDTO>> getAnnualLeavesByYear(
            @PathVariable Integer year,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查詢指定年份所有員工的特休配額：年份={}, 頁碼={}, 每頁數量={}", year, pageNum, pageSize);

        Page<EmployeeAnnualLeaveDTO> page = employeeAnnualLeaveService.getAnnualLeavesByYear(pageNum, pageSize, year);
        return ApiResponse.success("查詢成功", page);
    }

    /**
     * 查詢指定年份所有員工的特休配額（不分頁）
     */
    @GetMapping("/year/{year}/all")

    public ApiResponse<List<EmployeeAnnualLeaves>> getAllAnnualLeavesByYear(
            @PathVariable Integer year) {
        log.info("查詢指定年份所有員工的特休配額（不分頁）：年份={}", year);

        List<EmployeeAnnualLeaves> list = employeeAnnualLeaveService.getAllAnnualLeavesByYear(year);
        return ApiResponse.success("查詢成功", list);
    }

    /**
     * 查詢員工最新年份的特休配額
     */
    @GetMapping("/employee/{employeeId}/latest")

    public ApiResponse<EmployeeAnnualLeaves> getLatestAnnualLeaveByEmployeeId(
            @PathVariable Long employeeId) {
        log.info("查詢員工最新年份的特休配額：員工ID={}", employeeId);

        EmployeeAnnualLeaves annualLeave = employeeAnnualLeaveService.getLatestAnnualLeaveByEmployeeId(employeeId);
        return ApiResponse.success("查詢成功", annualLeave);
    }

    /**
     * 批量查詢員工的特休配額
     */
    @PostMapping("/batch")

    public ApiResponse<List<EmployeeAnnualLeaves>> getAnnualLeavesByEmployeeIdsAndYear(
            @RequestBody List<Long> employeeIds,
            @RequestParam Integer year) {
        log.info("批量查詢員工特休配額：員工ID列表={}, 年份={}", employeeIds, year);

        List<EmployeeAnnualLeaves> list = employeeAnnualLeaveService.getAnnualLeavesByEmployeeIdsAndYear(employeeIds, year);
        return ApiResponse.success("查詢成功", list);
    }

    /**
     * 新增年度特休配額
     */
    @PostMapping

    public ApiResponse<Boolean> addAnnualLeave(@RequestBody EmployeeAnnualLeaves annualLeave) {
        log.info("新增年度特休配額：員工ID={}, 年份={}, 總天數={}",
                annualLeave.getEmployeeId(), annualLeave.getYear(), annualLeave.getTotalHours());

        boolean added = employeeAnnualLeaveService.addAnnualLeave(annualLeave);
        return ApiResponse.success("新增成功", added);

    }

    /**
     * 批量新增年度特休配額
     */
    @PostMapping("/batch/add")

    public ApiResponse<?> addAnnualLeaveBatch(@RequestBody List<EmployeeAnnualLeaves> annualLeaves) {
        log.info("批量新增年度特休配額：共 {} 條記錄", annualLeaves.size());


        employeeAnnualLeaveService.addAnnualLeaveBatch(annualLeaves);
        return ApiResponse.success("批量新增成功");

    }

    /**
     * 更新年度特休配額
     */
    @PutMapping("/{id}")

    public ApiResponse<Boolean> updateAnnualLeave(
            @PathVariable Long id,
            @RequestBody EmployeeAnnualLeaves annualLeave) {
        log.info("更新年度特休配額：ID={}", id);

        annualLeave.setId(id);

        boolean updated = employeeAnnualLeaveService.updateAnnualLeave(annualLeave);
        return ApiResponse.success("更新成功", updated);

    }

    /**
     * 刪除年度特休配額
     */
    @DeleteMapping("/{id}")

    public ApiResponse<Boolean> deleteAnnualLeave(
            @PathVariable Long id) {
        log.info("刪除年度特休配額：ID={}", id);

        boolean deleted = employeeAnnualLeaveService.removeById(id);
        return ApiResponse.success("刪除成功", deleted);

    }

    /**
     * 查詢特休配額不足的員工數量
     */
    @GetMapping("/year/{year}/low-remain")

    public ApiResponse<Integer> countLowRemainDays(
            @PathVariable Integer year) {
        log.info("查詢特休配額不足的員工數量：年份={}", year);

        Integer count = employeeAnnualLeaveService.countLowRemainDays(year);
        return ApiResponse.success("查詢成功", count);
    }

    /**
     * 查詢特休已用完的員工數量
     */
    @GetMapping("/year/{year}/no-remain")

    public ApiResponse<Integer> countNoRemainDays(
            @PathVariable Integer year) {
        log.info("查詢特休已用完的員工數量：年份={}", year);

        Integer count = employeeAnnualLeaveService.countNoRemainDays(year);
        return ApiResponse.success("查詢成功", count);
    }
}