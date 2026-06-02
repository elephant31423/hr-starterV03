package com.example.hrstarter.controller;

import com.example.hrstarter.annotation.AuditLog;
import com.example.hrstarter.entity.Roles;
import com.example.hrstarter.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;


    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        List<Roles> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Roles> get(@PathVariable Long id) {
        Roles role = roleService.findById(id);
        log.info("查詢 roleID {}--{}",id,role);
        return ResponseEntity.ok(role);
    }
    @AuditLog(action = "CREATE", entityType = "ROLE", idParam = "id")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Roles role) {
        roleService.insert(role);
        return ResponseEntity.ok(Map.of("success", true, "message", "角色新增成功"));
    }
    /**
     * 修改角色
     */
    @AuditLog(action = "UPDATE", entityType = "ROLE", idParam = "id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Roles role) {
        try {
            role.setId(id);
            log.info("修改角色 ID {} 資料: {}", id, role);
            roleService.update(role);
            return ResponseEntity.ok(Map.of("success", true, "message", "角色修改成功"));
        } catch (Exception e) {
            log.error("修改角色失敗", e);
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }
    /**
     * 刪除角色
     */
    @AuditLog(action = "DELETE", entityType = "ROLE", idParam = "id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            roleService.delete(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "角色刪除成功"));
        } catch (Exception e) {
            log.error("刪除角色失敗", e);
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }
}
