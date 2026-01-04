package com.example.hrstarter.service.Imp;

import com.example.hrstarter.annotation.AuditLog;
import com.example.hrstarter.entity.AuditLogEntity;
import com.example.hrstarter.entity.Role;
import com.example.hrstarter.mapper.AuditLogMapper;
import com.example.hrstarter.mapper.RoleMapper;
import com.example.hrstarter.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;
    public RoleServiceImpl(RoleMapper roleMapper, AuditLogMapper auditLogMapper, ObjectMapper objectMapper) {
        this.roleMapper = roleMapper;
        this.auditLogMapper = auditLogMapper;
        this.objectMapper = objectMapper;
    }
//    @PreAuthorize("hasAuthority('role:view')")
    @Override
    public Role getRolesByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId);
    }

    @PreAuthorize("hasAuthority('role:view')")
    @Override
    public List<Role> findAll() {
        return roleMapper.findAll();
    }
    @PreAuthorize("hasAuthority('role:view')")
    @Override
    public Role findById(Long id) {
        return roleMapper.findById(id);
    }

    @PreAuthorize("hasAuthority('role:create')")
    @Transactional
    @Override
    @AuditLog(action = "insert" ,entityType = "ROLE")
    public void insert(Role role) {

        try {
            Role existing = roleMapper.findByRoleKey(role.getRoleKey());
            if(existing!=null){
                throw new RuntimeException("角色 Key 已存在: " + role.getRoleKey());
            }
            // 檢查系統內置角色是否可修改
            if (isSystemRole(role.getRoleKey())) {
                throw new RuntimeException("無法修改系統內置角色");
            }

            role.setCreatedAt(LocalDateTime.now());
            role.setUpdatedAt(LocalDateTime.now());
            role.setStatus("ACTIVE");

            roleMapper.insert(role);

            // 記錄審計日誌
            recordAuditLog("CREATE", "ROLE", role.getId(), null, role, "SUCCESS", null);
            log.info("角色創建成功: {}", role.getRoleName());
        } catch (Exception e) {
            recordAuditLog("CREATE", "ROLE", null, null, null, "FAILURE", e.getMessage());
            throw e;
        }
    }
    @PreAuthorize("hasAuthority('role:update')")
    @Transactional
    @Override
    public void update(Role role) {
        try {
            // 查詢原始角色
            Role oldRole = roleMapper.findById(role.getId());
            if (oldRole == null) {
                throw new RuntimeException("角色不存在: " + role.getId());
            }

            // 檢查系統內置角色是否可修改
            if (isSystemRole(oldRole.getRoleKey())) {
                throw new RuntimeException("無法修改系統內置角色");
            }

            role.setUpdatedAt(LocalDateTime.now());
            roleMapper.update(role);

            // 記錄審計日誌
            recordAuditLog("UPDATE", "ROLE", role.getId(), oldRole, role, "SUCCESS", null);
            log.info("角色修改成功: {}", role.getRoleName());
        } catch (Exception e) {
            recordAuditLog("UPDATE", "ROLE", role.getId(), null, null, "FAILURE", e.getMessage());
            throw e;
        }
    }
    @PreAuthorize("hasAuthority('role:delete')")
    @Transactional
    @Override
    public void delete(Long id) {
        try {
            // 查詢要刪除的角色
            Role role = roleMapper.findById(id);
            if (role == null) {
                throw new RuntimeException("角色不存在: " + id);
            }

            // 檢查系統內置角色是否可刪除
            if (isSystemRole(role.getRoleKey())) {
                throw new RuntimeException("無法刪除系統內置角色");
            }

            // 檢查是否有用戶使用此角色
            int userCount = roleMapper.countUsersByRoleId(id);
            if (userCount > 0) {
                throw new RuntimeException("無法刪除此角色，因為有 " + userCount + " 個用戶正在使用");
            }

            roleMapper.delete(id);

            // 記錄審計日誌
            recordAuditLog("DELETE", "ROLE", id, role, null, "SUCCESS", null);
            log.info("角色刪除成功: {}", role.getRoleName());
        } catch (Exception e) {
            recordAuditLog("DELETE", "ROLE", id, null, null, "FAILURE", e.getMessage());
            throw e;
        }
    }



    private boolean isSystemRole(String roleKey) {
        return roleKey.equals("ADMIN") || roleKey.equals("SYSTEM");
    }

    private void recordAuditLog(String action, String entityType, Long entityId,
                                Object oldValue, Object newValue, String status, String errorMessage) {
        try {
            AuditLogEntity auditLog = new AuditLogEntity();
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setOldValue(oldValue != null ? objectMapper.writeValueAsString(oldValue) : null);
            auditLog.setNewValue(newValue != null ? objectMapper.writeValueAsString(newValue) : null);
            auditLog.setStatus(status);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setCreatedAt(LocalDateTime.now());

            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.error("記錄審計日誌失敗", e);
        }
    }

}
