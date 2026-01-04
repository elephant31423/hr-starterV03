package com.example.hrstarter.service;

import com.example.hrstarter.entity.WorkSchedule;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface WorkScheduleService {

      List<WorkSchedule> getMonthlySchedule(
            Long employeeId, YearMonth month);

      void assignShift(WorkSchedule  schedule);




}
