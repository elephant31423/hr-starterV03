package com.example.hrstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO implements Serializable {

    private Long userCount;
    private Long roleCount;
    private Long employeeCount;
    private Long permissionCount;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long workingTodayCount; // 今日總上班人數
    private Long currentlyOnShiftCount; // 當前時段值班人數
    private Long vacationCount;        // 今日休假 (請假表)



}
