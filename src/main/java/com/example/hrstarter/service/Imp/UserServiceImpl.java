package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.UserDTO;
import com.example.hrstarter.entity.Role;
import com.example.hrstarter.entity.User;
import com.example.hrstarter.mapper.RoleMapper;
import com.example.hrstarter.mapper.UserMapper;
import com.example.hrstarter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserMapper userMapper, RoleMapper roleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    @PreAuthorize("hasAnyAuthority('user:view')")
    public Optional<UserDTO> findByUsername(String username) {

        User user = userMapper.findByUsername(username);

        return Optional.of(toUserDto(user));


    }

    @Override
    @PreAuthorize("hasAnyAuthority('user:view')")
    public Optional<UserDTO> findById(Long id) {

        User user = userMapper.findById(id);

        return Optional.ofNullable(user).map(this::toUserDto);
    }

@Override
public List<UserDTO> findAll() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = auth.getName();
    boolean isAdmin = auth.getAuthorities().stream()
                          .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

    if (isAdmin) {
        // Admin can view all users
        return userMapper.findAll().stream().map(this::toUserDto).toList();
    } else {
        // Non-admin users can only view their own data
        User currentUser = userMapper.findByUsername(currentUsername);
        return List.of(toUserDto(currentUser));
    }
}


    @Override
    public void insert(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void delete(Long id) {
        userMapper.delete(id);
    }

    @Override
    public Optional<User> validateCredentials(String username, String rawPassword) {
        return Optional.empty();
    }


    @Override
    public Role getRoles(User user) {

        return roleMapper.findRolesByUserId(user.getId());
    }


    private UserDTO toUserDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt().toString());
        dto.setUpdatedAt(user.getUpdatedAt().toString());
        return dto;
    }

}
