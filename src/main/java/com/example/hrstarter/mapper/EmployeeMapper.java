package com.example.hrstarter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hrstarter.dto.employee.EmployeeDTO;
import com.example.hrstarter.dto.employee.EmployeeQueryDTO;
import com.example.hrstarter.entity.Employee;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;


public interface EmployeeMapper extends BaseMapper<Employee> {

    List<EmployeeDTO> findAll();
    List<Employee> findAllActive();

    Employee findById(Long id);
    Employee findByEmployeeName(String name);

    LocalDate findBirthday(Long employeeId);

    List<Employee>findByDepartmentId(Long departmentId);
//    List<Employee>findByParams(Page<EmployeeQueryDTO> employeeQueryDTO);
    /**
     * @param page     分頁參數，MP 會自動攔截並處理 LIMIT
     * @param queryDTO 查詢條件
     */
    IPage<Employee> findByParams(IPage<Employee> page,@Param("query") EmployeeQueryDTO queryDTO);

    void update(Employee employee);

    void delete(Long id);

    Long count();

}