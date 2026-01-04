package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.EmployeeAnnualLeave;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

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

}
