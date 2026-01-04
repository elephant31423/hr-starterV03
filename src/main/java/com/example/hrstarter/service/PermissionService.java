package com.example.hrstarter.service;

import com.example.hrstarter.entity.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> findAll();

    List<Permission> selectPermissionCodesByRoleId(Long roleId);
}
