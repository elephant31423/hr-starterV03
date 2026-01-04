package com.example.hrstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    private Long userCount;
    private Long roleCount;
    private Long employeeCount;
    private Long permissionCount;
    private Long activeUsers;
    private Long inactiveUsers;


}
