package com.example.hrstarter.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;


public interface UserRoleMapper {

    void addRole(Long userId, Long roleId);
    void removeRole(Long userId, Long roleId);
}
