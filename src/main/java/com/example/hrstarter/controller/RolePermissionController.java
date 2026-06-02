package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.entity.Permissions;
import com.example.hrstarter.service.PermissionService;
import com.example.hrstarter.service.RolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/rolePermissions")
public class RolePermissionController {
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private PermissionService permissionService;
    /**
     * 獲取指定角色的權限 ID 列表
     * 用於前端 RoleEdit.vue 的 Tree 組件回顯勾選
     */
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<?> getRolePermissionData(@PathVariable Long roleId) {
        Map<String, Object> result = new HashMap<>();
        // 全系統所有的權限樹 (用於 Tree 組件的 :data)
        result.put("allPermissions", permissionService.getAllPermissionTree());
        log.info("allPermissions {}",result.size());
        // 該角色目前的權限 ID 清單 (用於 Tree 組件的 setCheckedKeys)
        result.put("checkedIds", rolePermissionService.getPermissionIdsByRole(roleId));
        log.info("checkedIds {}",result.get("checkedIds"));

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success( result));
    }

    /**
     * 保存角色的權限設定
     */
    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<?> updateRolePermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {

        rolePermissionService.updateRolePermissions(roleId, permissionIds);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permission:view')")
    public ResponseEntity<?> list() {
        List<Permissions> permissionList = permissionService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(permissionList));
    }



}
