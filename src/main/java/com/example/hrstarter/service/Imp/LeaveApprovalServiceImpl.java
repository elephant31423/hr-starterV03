package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.entity.LeaveApprovalStep;
import com.example.hrstarter.entity.LeaveRequest;
import com.example.hrstarter.mapper.EmployeeMapper;
import com.example.hrstarter.mapper.LeaveApprovalStepMapper;
import com.example.hrstarter.service.LeaveApprovalService;
import com.example.hrstarter.service.LeaveRequestService;
import com.example.hrstarter.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveApprovalServiceImpl implements LeaveApprovalService {
    private static final BigDecimal FINAL_APPROVAL_THRESHOLD_HOURS = new BigDecimal("24");

    private final LeaveApprovalStepMapper leaveApprovalStepMapper;
    private final LeaveRequestService leaveRequestService;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApprovalFlow(LeaveRequest leaveRequest) {
        List<LeaveApprovalStep> steps = buildSteps(leaveRequest);
        for (LeaveApprovalStep step : steps) {
            leaveApprovalStepMapper.insert(step);
        }
    }

    @Override
    public List<LeaveApprovalStep> listPendingForCurrentUser() {
        List<LeaveApprovalStep> pendingSteps = leaveApprovalStepMapper.findPendingSteps();
        return pendingSteps.stream()
                .filter(this::canApproveStep)
                .toList();
    }

    @Override
    public List<LeaveApprovalStep> listSteps(Long leaveRequestId) {
        return leaveApprovalStepMapper.findByLeaveRequestId(leaveRequestId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long stepId, String comment) {
        LeaveApprovalStep step = leaveApprovalStepMapper.findById(stepId);
        validatePendingStep(step);
        if (!canApproveStep(step)) {
            throw new AccessDeniedException("沒有此請假單的審核權限");
        }

        Long userId = currentUserId();
        leaveApprovalStepMapper.approve(stepId, userId, comment);

        LeaveApprovalStep nextStep = leaveApprovalStepMapper.findNextWaitingStep(
                step.getLeaveRequestId(),
                step.getStepOrder()
        );

        if (nextStep != null) {
            leaveApprovalStepMapper.markPending(nextStep.getId());
            leaveRequestService.updateApprovalStatus(step.getLeaveRequestId(), statusForStep(nextStep.getStepCode()));
            return;
        }

        leaveRequestService.finalizeApprovedLeave(step.getLeaveRequestId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long stepId, String comment) {
        LeaveApprovalStep step = leaveApprovalStepMapper.findById(stepId);
        validatePendingStep(step);
        if (!canApproveStep(step)) {
            throw new AccessDeniedException("沒有此請假單的審核權限");
        }

        leaveApprovalStepMapper.reject(stepId, currentUserId(), comment);
        leaveRequestService.updateApprovalStatus(step.getLeaveRequestId(), "REJECTED");
    }

    private List<LeaveApprovalStep> buildSteps(LeaveRequest leaveRequest) {
        List<LeaveApprovalStep> steps = new ArrayList<>();
        steps.add(newStep(leaveRequest.getId(), 1, "DEPARTMENT", "DEPARTMENT_MANAGER", "PENDING"));
        steps.add(newStep(leaveRequest.getId(), 2, "HR", "HR", "WAITING"));

        BigDecimal hours = leaveRequest.getLeaveHours() == null ? BigDecimal.ZERO : leaveRequest.getLeaveHours();
        if (hours.compareTo(FINAL_APPROVAL_THRESHOLD_HOURS) > 0) {
            steps.add(newStep(leaveRequest.getId(), 3, "FINAL", "EXECUTIVE", "WAITING"));
        }

        return steps;
    }

    private LeaveApprovalStep newStep(Long leaveRequestId, int order, String code, String role, String status) {
        LeaveApprovalStep step = new LeaveApprovalStep();
        step.setLeaveRequestId(leaveRequestId);
        step.setStepOrder(order);
        step.setStepCode(code);
        step.setApproverRole(role);
        step.setStatus(status);
        return step;
    }

    private boolean canApproveStep(LeaveApprovalStep step) {
        return switch (step.getStepCode()) {
            case "DEPARTMENT" -> SecurityUtils.hasPermission("leave:approve:department") && isSameDepartment(step);
            case "HR" -> SecurityUtils.hasPermission("leave:approve:hr");
            case "FINAL" -> SecurityUtils.hasPermission("leave:approve:final");
            default -> false;
        };
    }

    private boolean isSameDepartment(LeaveApprovalStep step) {
        Long currentEmployeeId = SecurityUtils.getEmployeeId();
        if (currentEmployeeId == null || step.getLeaveRequest() == null) {
            return false;
        }

        Employee currentEmployee = employeeMapper.selectById(currentEmployeeId);
        Employee requestEmployee = employeeMapper.selectById(step.getLeaveRequest().getEmployeeId());
        if (currentEmployee == null || requestEmployee == null) {
            return false;
        }

        Long currentDepartmentId = currentEmployee.getDepartmentId();
        Long requestDepartmentId = requestEmployee.getDepartmentId();
        return currentDepartmentId != null && currentDepartmentId.equals(requestDepartmentId);
    }

    private void validatePendingStep(LeaveApprovalStep step) {
        if (step == null) {
            throw new RuntimeException("找不到簽核步驟");
        }
        if (!"PENDING".equals(step.getStatus())) {
            throw new RuntimeException("此簽核步驟目前不可審核");
        }
    }

    private Long currentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new AccessDeniedException("尚未登入");
        }
        return userId;
    }

    private String statusForStep(String stepCode) {
        return switch (stepCode) {
            case "DEPARTMENT" -> "PENDING_DEPARTMENT";
            case "HR" -> "PENDING_HR";
            case "FINAL" -> "PENDING_FINAL";
            default -> "PENDING";
        };
    }
}
