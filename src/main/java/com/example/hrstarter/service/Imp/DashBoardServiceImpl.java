package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.DashboardStatsDTO;
import com.example.hrstarter.mapper.*;
import com.example.hrstarter.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService {

    private final EmployeeMapper employeeMapper;
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final EmployeeShiftsMapper employeeShiftsMapper;
    private final LeaveRecordMapper leaveRecordMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY = "dashboard:stats:data";

    @Override
    public DashboardStatsDTO getDashboardStats() {
        // 1. 嘗試從 Redis 拿
        DashboardStatsDTO stats = (DashboardStatsDTO) redisTemplate.opsForValue().get(CACHE_KEY);
        if (stats != null) {
            log.info("🚀 命中 Redis 緩存");
            return stats;
        }
        Long employeeCount = employeeMapper.count();
        Long permissionCount = permissionMapper.count();
        Long roleCount = roleMapper.count();
        Long userCount = userMapper.count();
        Long todayWorkerCount = employeeShiftsMapper.countTodayWorker();
        Long todayOnDutyCount = employeeShiftsMapper.countTodayOnDuty();
        Long todayVacation = leaveRecordMapper.countVacation();
        log.info("employeeCount--{}--", employeeCount);
        log.info("permissionCount--{}--", permissionCount);
        log.info("roleCount--{}--", roleCount);
        log.info("userCount--{}--", userCount);
        log.info("todayWorkerCount--{}--", todayWorkerCount);
        log.info("todayOnDutyCount--{}--", todayOnDutyCount);
        log.info("todayVacationCount--{}--", todayVacation);
        // TODO: 2025/12/7 未完成下面統計
        Long inactiveUsers = 0L;
        Long activeUsers = 0L;

        DashboardStatsDTO newStats = new DashboardStatsDTO();
        newStats.setUserCount(userCount);
        newStats.setEmployeeCount(employeeCount);
        newStats.setRoleCount(roleCount);
        newStats.setPermissionCount(permissionCount);
        newStats.setWorkingTodayCount(todayWorkerCount);
        newStats.setCurrentlyOnShiftCount(todayOnDutyCount);
        newStats.setInactiveUsers(inactiveUsers);
        newStats.setActiveUsers(activeUsers);
        newStats.setVacationCount(todayVacation);

        redisTemplate.opsForValue().set(CACHE_KEY, newStats, 10, TimeUnit.MINUTES);


        return newStats;
    }

    /**
     * 提供給外部調用的「刷緩存」方法
     */
    public void clearCache() {
        redisTemplate.delete(CACHE_KEY);
        log.info("📌 已主動清除儀表板緩存");
    }

    @EventListener
    public void handleShiftChanged(EmployeeShiftServiceImpl.ShiftChangedEvent event) {
        if (LocalDate.now().equals(event.date())) {
            this.clearCache(); // 只有今天的變動才刷儀表板
            log.info("🔔 收到 ShiftChangedEvent，日期：{}，已清除儀表板緩存", event.date());
        }
    }
}
