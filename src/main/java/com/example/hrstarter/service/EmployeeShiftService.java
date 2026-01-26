package com.example.hrstarter.service;

import com.example.hrstarter.dto.EmployeeShiftDTO;
import com.example.hrstarter.enums.ShiftType;

import java.util.Date;

public interface EmployeeShiftService {

    void assign(EmployeeShiftDTO employeeShiftDTO);

}
