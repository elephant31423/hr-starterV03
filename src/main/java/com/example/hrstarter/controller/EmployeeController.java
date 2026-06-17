package com.example.hrstarter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrstarter.annotation.AuditLog;
import com.example.hrstarter.annotation.NoRepeatSubmit;
import com.example.hrstarter.common.PageResult;
import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.employee.EmployeeDTO;
import com.example.hrstarter.dto.employee.EmployeeQueryDTO;
import com.example.hrstarter.entity.Employee;

import com.example.hrstarter.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {


    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @GetMapping
    public ResponseEntity<?> list() {
        List<EmployeeDTO> employeeList = employeeService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(employeeList));
    }

    @GetMapping("/unbound")
    public ResponseEntity<?> unbound() {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(employeeService.findUnbound()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Employee employee = employeeService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(employee));
    }
    @NoRepeatSubmit(lockTime = 5)
    @AuditLog(action = "CREATE", entityType = "EMPLOYEE", idParam = "id")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Employee e) {
        log.info("requestBody --{}--", e);
        employeeService.insert(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(e));
    }

    @AuditLog(action = "UPDATE", entityType = "EMPLOYEE", idParam = "id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Employee e) {
        log.info("本次新增員工資訊 --{}--", e);
        e.setId(id);
        employeeService.update(e);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    @AuditLog(action = "DELETE", entityType = "EMPLOYEE", idParam = "id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("本次刪除的員工 id --{}--", id);
        employeeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<?> getByDepartment(@PathVariable Long id) {
        List<Employee> byDepartmentId = employeeService.findByDepartmentId(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(byDepartmentId));
    }

    // 新增：多條件分頁查詢
    @GetMapping("/search")
    public ResponseEntity<?> search(EmployeeQueryDTO queryDTO) {
        log.info("多條件查詢參數: {}", queryDTO);
        // 調用我們剛剛封裝好的 Service 方法
        IPage<EmployeeDTO> pageData = employeeService.getEmployeePage(queryDTO);
        log.info("分頁查詢結果: {}", pageData.getPages());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(PageResult.of(pageData)));
    }
}
