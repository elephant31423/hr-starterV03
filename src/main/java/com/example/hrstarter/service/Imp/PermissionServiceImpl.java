package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.PermissionTreeDTO;
import com.example.hrstarter.entity.Permission;
import com.example.hrstarter.mapper.PermissionMapper;
import com.example.hrstarter.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;


    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    /**
     * 獲取所有權限
     */
    @PreAuthorize("hasAuthority('role:view')")
    @Override
    public List<Permission> findAll() {
//        log.info("獲取所有權限");
        return permissionMapper.selectAll();
    }

    /**
     * 根據角色 ID 獲取權限
     */
//    @PreAuthorize("hasAuthority('role:view')")
    @Override
    public List<Permission> selectPermissionCodesByRoleId(Long roleId) {
        return permissionMapper.selectPermissionCodesByRoleId(roleId);
    }

    @Override
    public List<PermissionTreeDTO> getAllPermissionTree() {
        // 1. 取得資料庫所有權限 (平鋪 List)
        List<Permission> allPermissions = permissionMapper.selectAll( );

        // 2. 轉換為 DTO 並放入 Map 中方便快速查找
        Map<Long, PermissionTreeDTO> nodeMap = allPermissions.stream()
                .map(p -> {
                    PermissionTreeDTO dto = new PermissionTreeDTO();
                    BeanUtils.copyProperties(p, dto);
                    return dto;
                })
                .collect(Collectors.toMap(PermissionTreeDTO::getId, dto -> dto));

        List<PermissionTreeDTO> tree = new ArrayList<>();

        // 3. 建立層級關係
        for (PermissionTreeDTO node : nodeMap.values()) {
            if (node.getParentId() == null || node.getParentId() == 0) {
                // 沒有父 ID，說明是根節點
                tree.add(node);
            } else {
                // 有父 ID，找到父節點並把自己加入進去
                PermissionTreeDTO parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return tree;
    }

    /**
     * 根據用戶 ID 查詢用戶的所有權限碼
     *
     * @param userId 用戶 ID
     * @return 權限碼列表
     */
    public List<String> getUserPermissions(Long userId) {
        log.info("查詢用戶權限: userId = {}", userId);
        List<String> permissions = permissionMapper.selectPermissionCodesByUserId(userId);
        log.info("用戶權限: {} - {}", userId, permissions);
        return permissions;
    }

    /**
     * 根據用戶 ID 查詢用戶的所有權限對象
     *
     * @param userId 用戶 ID
     * @return 權限對象列表
     */
    public List<Permission> getUserPermissionDetails(Long userId) {
        log.info("查詢用戶權限詳情: userId = {}", userId);
        return permissionMapper.selectUserPermissionsByUserId(userId);
    }
    /**
     * 查詢所有權限
     *
     * @return 權限列表
     */
    public List<Permission> getAllPermissions() {
        log.info("查詢所有權限");
        return permissionMapper.selectAll();
    }


}
