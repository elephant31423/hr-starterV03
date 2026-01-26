package com.example.hrstarter.service;

import com.example.hrstarter.entity.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();

    Department getDepartmentById(Long id);

    Department createDepartment(Department department);

    Department updateDepartment(Department department);

    void deleteDepartment(Long id);
}
