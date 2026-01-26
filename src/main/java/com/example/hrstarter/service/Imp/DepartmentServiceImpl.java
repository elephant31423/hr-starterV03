package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.Department;
import com.example.hrstarter.mapper.DepartmentMapper;
import com.example.hrstarter.service.DepartmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    final DepartmentMapper departmentMapper;

    @Override
    public List<Department> getAllDepartments() {
        log.info("Fetching all departments");
        return departmentMapper.findAll();
    }

    @Override
    public Department getDepartmentById(Long id) {
        log.info("Fetching department with id: {}", id);
        return departmentMapper.findById(id);
    }

    @Override
    public Department createDepartment(Department department) {
        return null;
    }

    @Override
    public Department updateDepartment(Department department) {
        return null;
    }

    @Override
    public void deleteDepartment(Long id) {

    }
}
