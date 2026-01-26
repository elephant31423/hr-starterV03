package com.example.hrstarter.service;

import com.example.hrstarter.dto.CalendarDTO;
import com.example.hrstarter.dto.CalendarDayDTO;
import com.example.hrstarter.dto.CalendarMonthDTO;
import com.example.hrstarter.dto.EmployeeCalendarDTO;

import java.time.YearMonth;
import java.util.List;

public interface CalendarService {
     CalendarDTO getCalendar(Long employeeId, YearMonth month);

     List<EmployeeCalendarDTO> getDepartmentCalendar(YearMonth ym, Long departmentId);


//     CalendarMonthDTO getEmployeeCalendar(Long employeeId,  int year, int month);
//
//     List<CalendarDayDTO> getEmployeeMonthlyCalendar(Long employeeId, YearMonth of);
}
