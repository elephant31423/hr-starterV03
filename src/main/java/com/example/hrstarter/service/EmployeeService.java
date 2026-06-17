package com.example.hrstarter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrstarter.dto.employee.EmployeeDTO;
import com.example.hrstarter.dto.employee.EmployeeQueryDTO;
import com.example.hrstarter.entity.Employee;

import java.util.List;


public interface EmployeeService {

    List<EmployeeDTO> findAll();
    List<EmployeeDTO> findUnbound();

    Employee findById(Long id);

    void insert(Employee e);

    void update(Employee e);

    void delete(Long id);

    List<Employee>findByDepartmentId(Long departmentId);

    // 支援分頁與多條件篩選
    IPage<EmployeeDTO> getEmployeePage(EmployeeQueryDTO queryDTO);

    // 軟刪除邏輯
    void softDelete(Long id);

    // 恢復在職
    void restore(Long id);

}
