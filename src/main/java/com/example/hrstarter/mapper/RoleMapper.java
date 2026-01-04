package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface RoleMapper {

    Role findById(Long roleId);
    Role findByRoleKey(String roleKey);
    List<String> findRoleKeysByUserId(Long userId);
    Role findRolesByUserId(Long userId);

    List<Role> findAll();
    void insert(Role role);
    void update(Role role);
    void delete(Long id);
    int countUsersByRoleId(Long id);
    Long count();

}
