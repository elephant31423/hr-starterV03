package com.example.hrstarter.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserRoleMapper {

    void batchInsertUserRoles(@Param("userId")Long userId, @Param("roleIds") List<Long> roleId);
    void deleteUserRolesByUserId(Long userId );
}
