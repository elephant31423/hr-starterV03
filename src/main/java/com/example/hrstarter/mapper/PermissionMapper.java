package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.Permissions;

import java.util.List;

public interface PermissionMapper {
    /**
     * 查詢所有權限
     *
     * @return 權限列表
     */
    List<Permissions> selectAll();
    /**
     * 根據角色 ID 查詢角色的所有權限碼
     *
     * @param roleId 角色 ID
     * @return 權限碼列表
     */
    List<Permissions> selectPermissionCodesByRoleId(Long roleId);
    /**
     * 根據用戶 ID 查詢用戶的所有權限對象
     *
     * @param userId 用戶 ID
     * @return 權限對象列表
     */
    List<Permissions> selectUserPermissionsByUserId(Long userId);
    /**
     * 根據用戶 ID 查詢用戶的所有權限碼
     *
     * SQL：
     * SELECT DISTINCT p.permission_code
     * FROM users u
     * JOIN user_roles ur ON u.id = ur.user_id
     * JOIN roles r ON ur.role_id = r.id
     * JOIN role_permissions rp ON r.id = rp.role_id
     * JOIN permissions p ON rp.permission_id = p.id
     * WHERE u.id = ? AND r.status = 'ACTIVE'
     * ORDER BY p.permission_code
     *
     * @param userId 用戶 ID
     * @return 權限碼列表
     */
    List<String> selectPermissionCodesByUserId(Long userId);

    List<String> selectPermissionCodesByRole(Long role);

    void insert(Permissions permission);

    void update(Permissions permission);
    void delete(Long id);

    Long count();

}
