package com.example.hrstarter.service.Imp;

import com.example.hrstarter.mapper.RolePermissionMapper;
import com.example.hrstarter.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    final private RolePermissionMapper rolePermissionMapper;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
// 1. 刪除舊有關聯
        rolePermissionMapper.deleteByRoleId(roleId);

        // 2. 如果傳入的權限清單不為空，則批量插入
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, permissionIds);
        }

        // 3. (選做) 這裡可以加上清理快取的邏輯，如果你的權限有存入 Redis
    }

    public List<Long> getPermissionIdsByRole(Long roleId) {
        return rolePermissionMapper.findPermissionIdsByRoleId(roleId);
    }




}
