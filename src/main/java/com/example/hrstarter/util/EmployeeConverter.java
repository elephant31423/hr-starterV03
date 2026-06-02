package com.example.hrstarter.util;

import com.example.hrstarter.dto.employee.EmployeeDTO;
import com.example.hrstarter.entity.Employee;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EmployeeConverter {
    EmployeeDTO toDTO(Employee employee);
}
