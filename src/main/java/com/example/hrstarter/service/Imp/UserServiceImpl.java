package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.UserDTO;
import com.example.hrstarter.dto.UserDetailDTO;
import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.entity.Roles;
import com.example.hrstarter.entity.User;
import com.example.hrstarter.exception.BusinessException;
import com.example.hrstarter.mapper.EmployeeMapper;
import com.example.hrstarter.mapper.RoleMapper;
import com.example.hrstarter.mapper.UserMapper;
import com.example.hrstarter.mapper.UserRoleMapper;
import com.example.hrstarter.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasAnyAuthority('user:view')")
    public Optional<UserDTO> findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        return Optional.ofNullable(user).map(this::toUserDto);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('user:view')")
    public Optional<UserDTO> findById(Long id) {
        User user = userMapper.findById(id);
        return Optional.ofNullable(user).map(this::toUserDto);
    }

    @Override
    public Optional<UserDetailDTO> findUserDetailDTOById(Long id) {
        return Optional.ofNullable(userMapper.findByIdWithRolesAndPermissions(id));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('user:view')")
    public List<UserDTO> findAll() {
        return userMapper.findAll().stream().map(this::toUserDto).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyAuthority('user:create')")
    public void insert(User user) {
        validateCreateUser(user);
        validateEmployeeBinding(null, user.getEmployeeId());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(user.getEnabled() == null ? true : user.getEnabled());
        userMapper.insert(user);

        if (!CollectionUtils.isEmpty(user.getRoleIds())) {
            userRoleMapper.batchInsertUserRoles(user.getId(), user.getRoleIds());
        }

        syncEmployeeBinding(user.getId(), user.getEmployeeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        log.info("update User {}", user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyAuthority('user:update')")
    public void updateBaseInfo(UserDetailDTO userDetailDTO) {
        Long userId = userDetailDTO.getId();
        User oldUser = userMapper.findById(userId);
        if (oldUser == null) {
            throw BusinessException.notFound("User not found");
        }

        if (StringUtils.hasText(userDetailDTO.getUsername())) {
            User sameUsername = userMapper.findByUsername(userDetailDTO.getUsername());
            if (sameUsername != null && !sameUsername.getId().equals(userId)) {
                throw BusinessException.conflict("Username already exists");
            }
        }

        validateEmployeeBinding(userId, userDetailDTO.getEmployeeId());

        if (StringUtils.hasText(userDetailDTO.getPassword())) {
            userDetailDTO.setPassword(passwordEncoder.encode(userDetailDTO.getPassword()));
        } else {
            userDetailDTO.setPassword(null);
        }

        int affectedRows = userMapper.updateBaseInfo(userDetailDTO);
        if (affectedRows == 0) {
            throw BusinessException.conflict("User update conflict, please reload and try again");
        }

        userRoleMapper.deleteUserRolesByUserId(userId);
        if (!CollectionUtils.isEmpty(userDetailDTO.getRoleIds())) {
            userRoleMapper.batchInsertUserRoles(userId, userDetailDTO.getRoleIds());
        }

        if (oldUser.getEmployeeId() != null && !oldUser.getEmployeeId().equals(userDetailDTO.getEmployeeId())) {
            employeeMapper.clearUserBindingByUserId(userId);
        }
        syncEmployeeBinding(userId, userDetailDTO.getEmployeeId());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('user:delete')")
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        employeeMapper.clearUserBindingByUserId(id);
        userMapper.delete(id);
    }

    @Override
    public Optional<User> validateCredentials(String username, String rawPassword) {
        return Optional.empty();
    }

    @Override
    public Roles getRoles(User user) {
        return roleMapper.findRolesByUserId(user.getId());
    }

    private void validateCreateUser(User user) {
        if (!StringUtils.hasText(user.getUsername())) {
            throw BusinessException.badRequest("Username is required");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw BusinessException.badRequest("Password is required");
        }
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw BusinessException.conflict("Username already exists");
        }
    }

    private void validateEmployeeBinding(Long currentUserId, Long employeeId) {
        if (employeeId == null) {
            return;
        }

        Employee employee = employeeMapper.findById(employeeId);
        if (employee == null) {
            throw BusinessException.badRequest("Employee not found");
        }
        if (employee.getUserId() != null && !employee.getUserId().equals(currentUserId)) {
            throw BusinessException.conflict("Employee is already bound to another user");
        }

        User boundUser = userMapper.findByEmployeeId(employeeId);
        if (boundUser != null && (currentUserId == null || !boundUser.getId().equals(currentUserId))) {
            throw BusinessException.conflict("Employee is already bound to another user");
        }
    }

    private void syncEmployeeBinding(Long userId, Long employeeId) {
        if (employeeId == null) {
            return;
        }

        int bound = employeeMapper.bindUser(employeeId, userId);
        if (bound == 0) {
            throw BusinessException.conflict("Employee is already bound to another user");
        }
    }

    private UserDTO toUserDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt() == null ? null : user.getCreatedAt().toString());
        dto.setUpdatedAt(user.getUpdatedAt() == null ? null : user.getUpdatedAt().toString());
        dto.setEmployeeId(user.getEmployeeId());

        if (user.getEmployeeId() != null) {
            Employee employee = employeeMapper.findById(user.getEmployeeId());
            if (employee != null) {
                dto.setEmployeeName(employee.getName());
                dto.setEmpNo(employee.getEmpNo());
            }
        }

        return dto;
    }
}
