package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.WorkSchedule;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkScheduleMapper {
    List<WorkSchedule> findByEmployeeAndMonth(
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    void insert(WorkSchedule schedule);

    void deleteByEmployeeAndDate(
            @Param("employeeId") Long employeeId,
            @Param("workDate") LocalDate workDate
    );
}
