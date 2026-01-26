package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.AnnualLeaveRule;
import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.entity.EmployeeAnnualLeave;
import com.example.hrstarter.mapper.AnnualLeaveRuleMapper;
import com.example.hrstarter.mapper.EmployeeAnnualLeaveMapper;
import com.example.hrstarter.service.AnnualLeaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Service
public class AnnualLeaveServiceImpl implements AnnualLeaveService {

    AnnualLeaveRuleMapper ruleMapper;

    EmployeeAnnualLeaveMapper leaveMapper;

    @Override
    public int calculateAnnualLeaveDays(String employeeId) {
        return 0;
    }

    @Override
    public void generateAnnualLeave(Employee employee) {
        int years = Period.between(
                LocalDate.from(employee.getHireDate()),
                LocalDate.now()
        ).getYears();


        AnnualLeaveRule rule = ruleMapper.findRule(years);

        EmployeeAnnualLeave leave = new EmployeeAnnualLeave();
        leave.setEmployeeId(employee.getId());
        leave.setYear(LocalDate.now().getYear());
        leave.setTotalDays(rule.getAnnualLeaveDays());
        leave.setRemainDays(rule.getAnnualLeaveDays());

        leaveMapper.insert(leave);
    }

}


