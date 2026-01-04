package com.example.hrstarter.service;

import com.example.hrstarter.entity.Role;

import java.util.List;

public interface RoleService {
    Role getRolesByUserId(Long userId);
    List<Role> findAll();
    Role findById(Long id);
    void insert(Role role);
    void update(Role role);
    void delete(Long id);

}
