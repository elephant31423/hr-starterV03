package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.EmployeeAnnualLeave;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeAnnualLeaveMapper {
    EmployeeAnnualLeave findByEmployeeAndYear(
            @Param("employeeId") Long employeeId,
            @Param("year") int year
    );

    void insert(EmployeeAnnualLeave record);

    void useLeave(
            @Param("employeeId") Long employeeId,
            @Param("days") BigDecimal days
    );
    List<EmployeeAnnualLeave> findByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
