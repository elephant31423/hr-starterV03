package com.example.hrstarter.dto.employee;

import com.example.hrstarter.dto.CalendarDayDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCalendarDTO {
    private Long employeeId;
    private String employeeName;
    private Long departmentId;
    private List<CalendarDayDTO> days;

}
