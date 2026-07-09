package com.example.hrstarter.mapper; ;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hrstarter.entity.EmployeeAnnualLeaves;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 員工年度特休 Mapper 接口
 *
 * @author HR System
 * @date 2024-01-30
 */
@Mapper
public interface EmployeeAnnualLeaveMapper extends BaseMapper<EmployeeAnnualLeaves> {

    /**
     * 查詢員工指定年份的特休配額
     *
     * @param employeeId 員工 ID
     * @param year 年份
     * @return 年度特休信息
     */
    EmployeeAnnualLeaves selectByEmployeeIdAndYear(
            @Param("employeeId") Long employeeId,
            @Param("year") Integer year
    );

    /**
     * 查詢員工所有年度的特休配額
     *
     * @param page 分頁參數
     * @param employeeId 員工 ID
     * @return 分頁結果
     */
    Page<EmployeeAnnualLeaves> selectByEmployeeId(
            Page<EmployeeAnnualLeaves> page,
            @Param("employeeId") Long employeeId
    );

    /**
     * 分頁查詢所有員工的年度特休配額
     *
     * @param page 分頁參數
     * @param year 年份
     * @return 分頁結果
     */
    Page<EmployeeAnnualLeaves> selectByYear(
            Page<EmployeeAnnualLeaves> page,
            @Param("year") Integer year
    );

    /**
     * 查詢指定年份所有員工的特休配額
     *
     * @param year 年份
     * @return 年度特休列表
     */
    List<EmployeeAnnualLeaves> selectAllByYear(@Param("year") Integer year);

    /**
     * 查詢員工最新年份的特休配額
     *
     * @param employeeId 員工 ID
     * @return 年度特休信息
     */
    EmployeeAnnualLeaves selectLatestByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 批量查詢員工的特休配額
     *
     * @param employeeIds 員工 ID 列表
     * @param year 年份
     * @return 年度特休列表
     */
    List<EmployeeAnnualLeaves> selectByEmployeeIdsAndYear(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("year") Integer year
    );

    /**
     * 更新員工已使用天數和剩餘天數
     *
     * @param id 記錄 ID
     * @param usedDays 已使用天數
     * @param remainDays 剩餘天數
     * @return 更新行數
     */
    Integer updateUsedDays(
            @Param("id") Long id,
            @Param("usedDays") Integer usedDays,
            @Param("remainDays") Integer remainDays
    );

    /**
     * 查詢特休配額不足的員工數量
     *
     * @param year 年份
     * @return 數量
     */
    @Select("SELECT COUNT(*) FROM employee_annual_leaves WHERE year = #{year} AND remain_hours < 40")
    Integer countLowRemainDays(@Param("year") Integer year);

    /**
     * 查詢特休已用完的員工數量
     *
     * @param year 年份
     * @return 數量
     */
    @Select("SELECT COUNT(*) FROM employee_annual_leaves WHERE year = #{year} AND remain_hours = 0")
    Integer countNoRemainDays(@Param("year") Integer year);

    /**
     * 查詢員工是否已有指定年份的特休配額
     *
     * @param employeeId 員工 ID
     * @param year 年份
     * @return 是否存在
     */
    Integer existsByEmployeeIdAndYear(
            @Param("employeeId") Long employeeId,
            @Param("year") Integer year
    );
}
