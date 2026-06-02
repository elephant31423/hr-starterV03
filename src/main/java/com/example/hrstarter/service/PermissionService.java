package com.example.hrstarter.service;

import com.example.hrstarter.dto.PermissionTreeDTO;
import com.example.hrstarter.entity.Permissions;

import java.util.List;

public interface PermissionService {
    List<Permissions> findAll();

    List<Permissions> selectPermissionCodesByRoleId(Long roleId);

    List<PermissionTreeDTO> getAllPermissionTree();


}
