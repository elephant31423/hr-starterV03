package com.example.hrstarter.service;

import com.example.hrstarter.entity.Employee;

public interface AnnualLeaveService {
    /**
     * 计算员工的年假天数
     *
     * @param employeeId 员工ID
     * @return 年假天数
     */
    int calculateAnnualLeaveDays(String employeeId);
    public void generateAnnualLeave(Employee employee);
}
