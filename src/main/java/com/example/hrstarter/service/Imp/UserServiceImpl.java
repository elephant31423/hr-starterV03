package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.UserDTO;
import com.example.hrstarter.dto.UserDetailDTO;
import com.example.hrstarter.entity.Roles;
import com.example.hrstarter.entity.User;
import com.example.hrstarter.exception.BusinessException;
import com.example.hrstarter.mapper.RoleMapper;
import com.example.hrstarter.mapper.UserMapper;
import com.example.hrstarter.mapper.UserRoleMapper;
import com.example.hrstarter.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;


//    public UserServiceImpl(UserMapper userMapper, RoleMapper roleMapper, PasswordEncoder passwordEncoder) {
//        this.userMapper = userMapper;
//        this.roleMapper = roleMapper;
//        this.passwordEncoder = passwordEncoder;
//
//    }

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
    public Optional<UserDetailDTO> findUserDetailDTOById(Long id) {
        UserDetailDTO userDetailDTO = userMapper.findByIdWithRolesAndPermissions(id);

        log.info("findUserDetailDTOById userDetailDTO {}", userDetailDTO);
// 業務邏輯：確保只取出一個角色
        if (userDetailDTO != null && userDetailDTO.getRoles() != null && !userDetailDTO.getRoles().isEmpty()) {
            // 如果 DTO 裡改成了單個 Role 物件
            // dto.setRole(dto.getRoles().get(0));
        }
        return Optional.ofNullable(userDetailDTO);

    }

    @Override
    public List<UserDTO> findAll() {

        return userMapper.findAll().stream().map(this::toUserDto).toList();
    }


    @Override
    public void insert(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(User user) {

        log.info("update User {}", user);

//        userMapper.update(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @PreAuthorize("hasAnyAuthority('user:update')")
    public void updateBaseInfo(UserDetailDTO userDetailDTO) {
        Long userId = userDetailDTO.getId();

        if(StringUtils.hasText(userDetailDTO.getPassword())){
            userDetailDTO.setPassword(passwordEncoder.encode(userDetailDTO.getPassword()));
        }else {
            // ⚡️ 重要：如果沒傳密碼，設為 null，確保 MyBatis 的 <if> 標籤不會去更新這個欄位
            userDetailDTO.setPassword(null);
        }

        int affectedRows = userMapper.updateBaseInfo(userDetailDTO);
        if (affectedRows == 0) {
            log.warn("用戶更新衝突：ID={}, Version={}", userDetailDTO.getId(), userDetailDTO.getVersion());
            throw BusinessException.conflict("更新失敗：資料已被其他管理員修改，請重新載入頁面後再試。");
        }
        // 2. 獲取要更新的角色列表
        userRoleMapper.deleteUserRolesByUserId(userId);

        List<Long> idsToUpdate = userDetailDTO.getRoleIds();
        log.info("userDetailDTO {}", userDetailDTO);
        if (!CollectionUtils.isEmpty(idsToUpdate)) {
            userRoleMapper.batchInsertUserRoles(userDetailDTO.getId(), idsToUpdate);
            log.info("使用者 {} 角色更新成功，數量: {}", userDetailDTO.getId(), userDetailDTO.getRoleIds().size());
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public void delete(Long id) {
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
