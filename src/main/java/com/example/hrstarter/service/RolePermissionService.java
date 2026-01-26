package com.example.hrstarter.service;

import com.example.hrstarter.dto.PermissionTreeDTO;

import java.util.List;

public interface RolePermissionService {
    void updateRolePermissions(Long roleId, List<Long> permissionIds);


    List<Long> getPermissionIdsByRole(Long roleId);


}
