package com.example.hrstarter.service;

import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface EmployeeService {

    List<Employee> findAll();

    Employee findById(Long id);

    void insert(Employee e);

    void update(Employee e);

    void delete(Long id);
}
