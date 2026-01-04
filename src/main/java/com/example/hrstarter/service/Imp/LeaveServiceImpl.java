package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.LeaveRequest;
import com.example.hrstarter.mapper.EmployeeAnnualLeaveMapper;
import com.example.hrstarter.mapper.LeaveRequestMapper;
import com.example.hrstarter.service.LeaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LeaveServiceImpl implements LeaveService {

    LeaveRequestMapper leaveRequestMapper;

    EmployeeAnnualLeaveMapper employeeAnnualLeaveMapper;

    public LeaveServiceImpl(LeaveRequestMapper leaveRequestMapper, EmployeeAnnualLeaveMapper employeeAnnualLeaveMapper) {
        this.employeeAnnualLeaveMapper = employeeAnnualLeaveMapper;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    @Override
    public void applyLeave(LeaveRequest req) {
        leaveRequestMapper.insert(req);

        // 特休才扣
        if (req.getLeaveTypeId() == 1L) {
            employeeAnnualLeaveMapper.useLeave(
                    req.getEmployeeId(),
                    req.getDays()
            );
        }
        log.info("员工{}提交请假申请，类型{}，天数{}", req.getEmployeeId(), req.getLeaveTypeId(), req.getDays());

    }
}



