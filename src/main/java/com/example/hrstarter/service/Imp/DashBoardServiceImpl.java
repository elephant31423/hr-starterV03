package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.DashboardStatsDTO;
import com.example.hrstarter.mapper.*;
import com.example.hrstarter.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService {

    private final EmployeeMapper employeeMapper;
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final EmployeeShiftsMapper employeeShiftsMapper;


    @Override
    public DashboardStatsDTO getDashboardStats() {

        Long employeeCount = employeeMapper.count();
        Long permissionCount = permissionMapper.count();
        Long roleCount = roleMapper.count();
        Long userCount = userMapper.count();
        Long todayWorkerCount = employeeShiftsMapper.countTodayWorker();
        Long todayOnDutyCount = employeeShiftsMapper.countTodayOnDuty();
        log.info("employeeCount--{}--", employeeCount);
        log.info("permissionCount--{}--", permissionCount);
        log.info("roleCount--{}--", roleCount);
        log.info("userCount--{}--", userCount);
        log.info("todayWorkerCount--{}--", todayWorkerCount);
        log.info("todayOnDutyCount--{}--", todayOnDutyCount);
        // TODO: 2025/12/7 未完成下面統計
        Long inactiveUsers = 0L;
        Long activeUsers = 0L;

        DashboardStatsDTO dashboardStatsDTO = new DashboardStatsDTO();
        dashboardStatsDTO.setUserCount(userCount);
        dashboardStatsDTO.setEmployeeCount(employeeCount);
        dashboardStatsDTO.setRoleCount(roleCount);
        dashboardStatsDTO.setPermissionCount(permissionCount);
        dashboardStatsDTO.setWorkingTodayCount(todayWorkerCount);
        dashboardStatsDTO.setCurrentlyOnShiftCount(todayOnDutyCount);
        dashboardStatsDTO.setInactiveUsers(inactiveUsers);
        dashboardStatsDTO.setActiveUsers(activeUsers);
        return dashboardStatsDTO;
    }
}
