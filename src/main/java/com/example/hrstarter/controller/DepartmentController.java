package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.entity.Department;
import com.example.hrstarter.service.DepartmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@Slf4j
@AllArgsConstructor
public class DepartmentController {

    final DepartmentService departmentService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        log.info("📥 獲取部門: {}", id);

        try {
            Department department = departmentService.getDepartmentById(id);

            if (department == null) {
                log.warn("⚠️ 部門不存在: {}", id);
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.internalServerError("部門不存在"));
            }

            log.info("✅ 部門獲取成功: {}", department.getName());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(department));
        } catch (Exception e) {
            log.error("❌ 獲取部門失敗", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("獲取部門失敗"));
        }
    }
    @GetMapping()
    public ResponseEntity<?> getAllDepartments() {
        log.info("📥 獲取所有部門");

        try {
            List<Department> allDepartments = departmentService.getAllDepartments();

            log.info("✅ 部門列表獲取成功: {} 個部門", allDepartments.size());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(allDepartments));
        } catch (Exception e) {
            log.error("❌ 獲取部門列表失敗", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("獲取部門列表失敗"));
        }
    }
    @PostMapping()
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        log.info("📥 創建部門: {}", department.getName());

        try {
            Department createdDepartment = departmentService.createDepartment(department);

            log.info("✅ 部門創建成功: {}", createdDepartment.getName());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdDepartment));
        } catch (Exception e) {
            log.error("❌ 創建部門失敗", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("創建部門失敗"));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Long id,
            @RequestBody Department department) {
        log.info("📥 更新部門: {}", id);

        try {
            department.setId(id);
            Department updatedDepartment = departmentService.updateDepartment(department);

            log.info("✅ 部門更新成功: {}", updatedDepartment.getName());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(updatedDepartment));
        } catch (Exception e) {
            log.error("❌ 更新部門失敗", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("更新部門失敗"));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        log.info("📥 刪除部門: {}", id);

        try {
            departmentService.deleteDepartment(id);

            log.info("✅ 部門刪除成功: {}", id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(null));
        } catch (Exception e) {
            log.error("❌ 刪除部門失敗", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("刪除部門失敗"));
        }
    }
}
