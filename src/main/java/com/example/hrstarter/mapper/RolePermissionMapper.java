package com.example.hrstarter.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolePermissionMapper {
    /**
     * 根據角色ID刪除所有權限關聯 (用於更新前的清理)
     */
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色與權限的關聯
     */
    void batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 根據角色ID查詢擁有的權限ID清單
     */
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
