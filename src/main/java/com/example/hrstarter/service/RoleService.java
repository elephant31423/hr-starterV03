package com.example.hrstarter.service;

import com.example.hrstarter.entity.Roles;

import java.util.List;

public interface RoleService {
    Roles getRolesByUserId(Long userId);
    List<Roles> findAll();
    Roles findById(Long id);
    void insert(Roles role);
    void update(Roles role);
    void delete(Long id);

}
