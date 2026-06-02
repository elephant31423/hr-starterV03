package com.example.hrstarter.service;

import com.example.hrstarter.dto.UserDTO;
import com.example.hrstarter.dto.UserDetailDTO;
import com.example.hrstarter.entity.Roles;
import com.example.hrstarter.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
  Optional<UserDTO>  findByUsername(String username);

  Optional<UserDTO>  findById(Long id);
  Optional<UserDetailDTO> findUserDetailDTOById(Long id);

    List<UserDTO> findAll();

    void insert(User user);

    void update(User user);

    void updateBaseInfo(UserDetailDTO user);

    void delete(Long id);

    Optional<User> validateCredentials(String username, String rawPassword);

    Roles getRoles(User user);


}
