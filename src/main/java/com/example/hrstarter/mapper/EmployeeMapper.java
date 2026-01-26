package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;


public interface EmployeeMapper {

    List<Employee> findAll();
    List<Employee> findAllActive();
    Employee findById(Long id);
    Employee findByEmployeeName(String name);
    void insert(Employee employee);
    void update(Employee employee);
    void delete(Long id);
    Long count();

    LocalDate findBirthday(Long employeeId);

    List<Employee>findByDepartmentId(Long departmentId);

}