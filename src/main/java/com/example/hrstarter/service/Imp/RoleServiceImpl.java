package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.Roles;
import com.example.hrstarter.mapper.RoleMapper;
import com.example.hrstarter.mapper.RolePermissionMapper;
import com.example.hrstarter.service.RoleService;
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
    private final RolePermissionMapper rolePermissionMapper;
    public RoleServiceImpl(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;

    }
//    @PreAuthorize("hasAuthority('role:view')")
    @Override
    public Roles getRolesByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId);
    }

    @PreAuthorize("hasAuthority('role:view')")
    @Override
    public List<Roles> findAll() {
        return roleMapper.findAll();
    }
    @PreAuthorize("hasAuthority('role:view')")
    @Override
    public Roles findById(Long id) {
        return roleMapper.findById(id);
    }

    @PreAuthorize("hasAuthority('role:create')")
    @Transactional
    @Override
    public void insert(Roles role) {

        try {
            Roles existing = roleMapper.findByRoleKey(role.getRoleKey());
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

        } catch (Exception e) {
            throw e;
        }
    }
    @PreAuthorize("hasAuthority('role:update')")
    @Transactional
    @Override
    public void update(Roles role) {
        try {
            // 查詢原始角色
            Roles oldRole = roleMapper.findById(role.getId());
            log.info("oldRole: {} ========> newRole: {}", oldRole,role);
            if (oldRole == null) {
                throw new RuntimeException("角色不存在: " + role.getId());
            }

            // 檢查系統內置角色是否可修改
            if (isSystemRole(oldRole.getRoleKey())) {
                throw new RuntimeException("無法修改系統內置角色");
            }
            rolePermissionMapper.deleteByRoleId(oldRole.getId());
            role.setUpdatedAt(LocalDateTime.now());
            // 3. 插入新權限
            if (role.getPermissionIds() != null && !role.getPermissionIds().isEmpty()) {
                rolePermissionMapper.batchInsert(role.getId(), role.getPermissionIds());
            }
            roleMapper.update(role);

        } catch (Exception e) {

            throw e;
        }
    }
    @PreAuthorize("hasAuthority('role:delete')")
    @Transactional
    @Override
    public void delete(Long id) {
        try {
            // 查詢要刪除的角色
            Roles role = roleMapper.findById(id);
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
            log.info("角色刪除成功: {}", role.getRoleName());
        } catch (Exception e) {

            throw e;
        }
    }



    private boolean isSystemRole(String roleKey) {
        return roleKey.equals("ADMIN") || roleKey.equals("SYSTEM");
    }

//    private void recordAuditLog(String action, String entityType, Long entityId,
//                                Object oldValue, Object newValue, String status, String errorMessage) {
//        try {
//            AuditLogEntity auditLog = new AuditLogEntity();
//            auditLog.setAction(action);
//            auditLog.setEntityType(entityType);
//            auditLog.setEntityId(entityId);
//            auditLog.setOldValue(oldValue != null ? objectMapper.writeValueAsString(oldValue) : null);
//            auditLog.setNewValue(newValue != null ? objectMapper.writeValueAsString(newValue) : null);
//            auditLog.setStatus(status);
//            auditLog.setErrorMessage(errorMessage);
//            auditLog.setCreatedAt(LocalDateTime.now());
//
//            auditLogMapper.insert(auditLog);
//        } catch (Exception e) {
//            log.error("記錄審計日誌失敗", e);
//        }
//    }

}
