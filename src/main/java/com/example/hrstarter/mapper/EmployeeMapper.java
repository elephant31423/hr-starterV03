package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface EmployeeMapper {

    List<Employee> findAll();
    Employee findById(Long id);
    Employee findByEmployeeName(String name);
    void insert(Employee employee);
    void update(Employee employee);
    void delete(Long id);

    Long count();
}