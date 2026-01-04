package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.mapper.EmployeeMapper;
import com.example.hrstarter.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;



    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

@PreAuthorize("hasAnyAuthority('employee:view')")
@Override
public List<Employee> findAll() {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    log.info("Current Authorities = {}", auth.getAuthorities());
    return employeeMapper.findAll();
}
    @PreAuthorize("hasAuthority('employee:view')")
    @Override
    public Employee findById(Long id) {
        return employeeMapper.findById(id);
    }

    @PreAuthorize("hasAuthority('employee:create')")
    @Override
    public void insert(Employee e) {
        employeeMapper.insert(e);
    }

    @PreAuthorize("hasAuthority('employee:update')")
    @Override
    public void update(Employee e) {
        employeeMapper.update(e);
    }

    @PreAuthorize("hasAuthority('employee:delete')")
    @Override
    public void delete(Long id) {
        employeeMapper.delete(id);
    }
}