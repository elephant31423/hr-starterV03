package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.Holiday;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HolidayMapper {


    List<Holiday> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
