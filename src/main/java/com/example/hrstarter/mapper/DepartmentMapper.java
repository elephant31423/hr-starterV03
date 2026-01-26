package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.Department;

import java.util.List;

public interface DepartmentMapper {

    Department findById(Long id);
    List<Department> findAll();



}
