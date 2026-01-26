package com.example.hrstarter.mapper;

import com.example.hrstarter.dto.UserDetailDTO;
import com.example.hrstarter.entity.User;

import java.util.List;


public interface UserMapper {
    User findByUsername(String username);

    User findById(Long id);

    UserDetailDTO findByIdWithRolesAndPermissions(Long id);


    List<User> findAll();

    void insert(User user);

//    void update(User user);

    int updateBaseInfo(UserDetailDTO user);

    void delete(Long id);

    Long count();
}
