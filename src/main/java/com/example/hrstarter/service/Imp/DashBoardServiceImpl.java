package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.DashboardStatsDTO;
import com.example.hrstarter.mapper.EmployeeMapper;
import com.example.hrstarter.mapper.PermissionMapper;
import com.example.hrstarter.mapper.RoleMapper;
import com.example.hrstarter.mapper.UserMapper;
import com.example.hrstarter.service.DashBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DashBoardServiceImpl implements DashBoardService {

    private final EmployeeMapper employeeMapper;
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;

    public DashBoardServiceImpl(EmployeeMapper employeeMapper, PermissionMapper permissionMapper, RoleMapper roleMapper, UserMapper userMapper) {
        this.employeeMapper = employeeMapper;
        this.permissionMapper = permissionMapper;
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
    }


    @Override
    public DashboardStatsDTO getDashboardStats() {

        Long employeeCount = employeeMapper.count();
        Long permissionCount = permissionMapper.count();
        Long roleCount = roleMapper.count();
        Long userCount = userMapper.count();
        log.info("employeeCount--{}--", employeeCount);
        log.info("permissionCount--{}--", permissionCount);
        log.info("roleCount--{}--", roleCount);
        log.info("userCount--{}--", userCount);
        // TODO: 2025/12/7 未完成下面統計
        Long inactiveUsers = 0L;
        Long activeUsers = 0L;
        return new DashboardStatsDTO(userCount,roleCount,employeeCount, permissionCount,   activeUsers, inactiveUsers);
    }
}
