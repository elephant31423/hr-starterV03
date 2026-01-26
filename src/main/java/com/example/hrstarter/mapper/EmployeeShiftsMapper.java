package com.example.hrstarter.mapper;

import com.example.hrstarter.dto.EmployeeShiftDTO;
import com.example.hrstarter.dto.ShiftDTO;
import com.example.hrstarter.entity.EmployeeShifts;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeShiftsMapper {

    List<ShiftDTO> findByEmployeeAndDateRange(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


 Optional<EmployeeShifts> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    void save(EmployeeShifts s);
    void upsert( EmployeeShifts shifts);

    void update(EmployeeShifts existingShift);

    Long countTodayWorker();

    Long countTodayOnDuty();

}
