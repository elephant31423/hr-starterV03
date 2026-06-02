package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.Roles;

import java.util.List;

public interface RoleMapper {

    Roles findById(Long roleId);
    Roles findByRoleKey(String roleKey);
    List<String> findRoleKeysByUserId(Long userId);
    Roles findRolesByUserId(Long userId);

    List<Roles> findAll();
    void insert(Roles role);
    void update(Roles role);
    void delete(Long id);
    int countUsersByRoleId(Long id);
    Long count();

}
