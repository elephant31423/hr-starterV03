package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.LeaveRecords;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LeaveRecordMapper {


    List<LeaveRecords> findByEmployeeAndDateRange(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
