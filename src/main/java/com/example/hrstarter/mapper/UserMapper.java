package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserMapper {
    User findByUsername(String username);

    User findById(Long id);

    List<User> findAll();

    void insert(User user);

    void update(User user);

    void delete(Long id);

    Long count();
}
