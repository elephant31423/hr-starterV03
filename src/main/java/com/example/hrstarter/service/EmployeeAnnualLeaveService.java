package com.example.hrstarter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hrstarter.dto.EmployeeAnnualLeaveDTO;
import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.entity.EmployeeAnnualLeaves;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
        * 員工年度特休業務邏輯接口
        *
        * @author HR System
        * @date 2024-01-30
        */
public interface EmployeeAnnualLeaveService extends IService<EmployeeAnnualLeaves> {

    /**
     * 查詢員工指定年份的特休配額
     *
     * @param employeeId 員工 ID
     * @param year 年份
     * @return 年度特休信息
     */
    EmployeeAnnualLeaveDTO getAnnualLeaveByEmployeeIdAndYear(Long employeeId, Integer year);

    /**
     * 分頁查詢員工所有年度的特休配額
     *
     * @param pageNum 頁碼
     * @param pageSize 每頁數量
     * @param employeeId 員工 ID
     * @return 分頁結果
     */
    Page<EmployeeAnnualLeaveDTO> getEmployeeAnnualLeaves(Integer pageNum, Integer pageSize, Long employeeId);

    /**
     * 分頁查詢指定年份所有員工的特休配額
     *
     * @param pageNum 頁碼
     * @param pageSize 每頁數量
     * @param year 年份
     * @return 分頁結果
     */
    Page<EmployeeAnnualLeaveDTO> getAnnualLeavesByYear(Integer pageNum, Integer pageSize, Integer year);

    /**
     * 查詢指定年份所有員工的特休配額
     *
     * @param year 年份
     * @return 年度特休列表
     */
    List<EmployeeAnnualLeaves> getAllAnnualLeavesByYear(Integer year);

    /**
     * 查詢員工最新年份的特休配額
     *
     * @param employeeId 員工 ID
     * @return 年度特休信息
     */
    EmployeeAnnualLeaves getLatestAnnualLeaveByEmployeeId(Long employeeId);

    /**
     * 批量查詢員工的特休配額
     *
     * @param employeeIds 員工 ID 列表
     * @param year 年份
     * @return 年度特休列表
     */
    List<EmployeeAnnualLeaves> getAnnualLeavesByEmployeeIdsAndYear(List<Long> employeeIds, Integer year);

    /**
     * 新增年度特休配額
     *
     * @param annualLeave 年度特休信息
     * @return 是否成功
     */
    boolean addAnnualLeave(EmployeeAnnualLeaves annualLeave);

    /**
     * 批量新增年度特休配額
     *
     * @param annualLeaves 年度特休列表
     * @return 是否成功
     */
     void addAnnualLeaveBatch(List<EmployeeAnnualLeaves> annualLeaves);

    /**
     * 更新年度特休配額
     *
     * @param annualLeave 年度特休信息
     * @return 是否成功
     */
    boolean updateAnnualLeave(EmployeeAnnualLeaves annualLeave);

    /**
     * 查詢特休配額不足的員工數量
     *
     * @param year 年份
     * @return 數量
     */
    Integer countLowRemainDays(Integer year);

    /**
     * 查詢特休已用完的員工數量
     *
     * @param year 年份
     * @return 數量
     */
    Integer countNoRemainDays(Integer year);

    /**
     * 檢查員工是否已有指定年份的特休配額
     *
     * @param employeeId 員工 ID
     * @param year 年份
     * @return 是否存在
     */
    boolean hasAnnualLeaveByEmployeeIdAndYear(Long employeeId, Integer year);

    /**
     * 刷新員工的特休配額
     *
     * @param employee 員工信息
     * @param year 年份
     * @param systemType 系統類型
     */
   void refreshEmployeeLeave(Employee employee, int year, String systemType);

    void updateById(EmployeeAnnualLeaveDTO annualLeave);
}