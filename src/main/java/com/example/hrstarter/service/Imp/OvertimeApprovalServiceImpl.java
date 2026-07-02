package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.entity.OvertimeApprovalStep;
import com.example.hrstarter.mapper.EmployeeMapper;
import com.example.hrstarter.mapper.OvertimeApprovalStepMapper;
import com.example.hrstarter.service.NotificationService;
import com.example.hrstarter.service.OvertimeApprovalService;
import com.example.hrstarter.service.OvertimeRequestService;
import com.example.hrstarter.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OvertimeApprovalServiceImpl implements OvertimeApprovalService {
    private final OvertimeApprovalStepMapper overtimeApprovalStepMapper;
    private final OvertimeRequestService overtimeRequestService;
    private final EmployeeMapper employeeMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApprovalFlow(Long overtimeRequestId) {
        overtimeApprovalStepMapper.insert(newStep(overtimeRequestId, 1, "DEPARTMENT", "DEPARTMENT_MANAGER", "PENDING"));
        overtimeApprovalStepMapper.insert(newStep(overtimeRequestId, 2, "HR", "HR", "WAITING"));
        overtimeApprovalStepMapper.insert(newStep(overtimeRequestId, 3, "FINAL", "EXECUTIVE", "WAITING"));
    }

    @Override
    public List<OvertimeApprovalStep> listPendingForCurrentUser() {
        return overtimeApprovalStepMapper.findPendingSteps()
                .stream()
                .filter(this::canApproveStep)
                .toList();
    }

    @Override
    public List<OvertimeApprovalStep> listSteps(Long overtimeRequestId) {
        return overtimeApprovalStepMapper.findByOvertimeRequestId(overtimeRequestId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long stepId, String comment) {
        OvertimeApprovalStep step = overtimeApprovalStepMapper.findById(stepId);
        validatePendingStep(step);
        if (!canApproveStep(step)) {
            throw new AccessDeniedException("沒有此加班審核步驟的核准權限");
        }

        Long userId = currentUserId();
        overtimeApprovalStepMapper.approve(stepId, userId, comment);

        OvertimeApprovalStep nextStep = overtimeApprovalStepMapper.findNextWaitingStep(
                step.getOvertimeRequestId(),
                step.getStepOrder()
        );

        if (nextStep != null) {
            overtimeApprovalStepMapper.markPending(nextStep.getId());
            overtimeRequestService.updateApprovalStatus(step.getOvertimeRequestId(), statusForStep(nextStep.getStepCode()));
            notifyNextRole(nextStep);
            return;
        }

        overtimeRequestService.finalizeApprovedOvertime(step.getOvertimeRequestId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long stepId, String comment) {
        OvertimeApprovalStep step = overtimeApprovalStepMapper.findById(stepId);
        validatePendingStep(step);
        if (!canApproveStep(step)) {
            throw new AccessDeniedException("沒有此加班審核步驟的駁回權限");
        }

        overtimeApprovalStepMapper.reject(stepId, currentUserId(), comment);
        overtimeRequestService.updateApprovalStatus(step.getOvertimeRequestId(), "REJECTED");
        if (step.getOvertimeRequest() != null) {
            notificationService.notifyEmployee(step.getOvertimeRequest().getEmployeeId(), "OVERTIME_REJECTED", "加班申請已駁回", "您的加班申請未通過審核");
        }
    }

    private OvertimeApprovalStep newStep(Long overtimeRequestId, int order, String code, String role, String status) {
        OvertimeApprovalStep step = new OvertimeApprovalStep();
        step.setOvertimeRequestId(overtimeRequestId);
        step.setStepOrder(order);
        step.setStepCode(code);
        step.setApproverRole(role);
        step.setStatus(status);
        return step;
    }

    private boolean canApproveStep(OvertimeApprovalStep step) {
        return switch (step.getStepCode()) {
            case "DEPARTMENT" -> SecurityUtils.hasPermission("overtime:approve:department") && isSameDepartment(step);
            case "HR" -> SecurityUtils.hasPermission("overtime:approve:hr");
            case "FINAL" -> SecurityUtils.hasPermission("overtime:approve:final");
            default -> false;
        };
    }

    private boolean isSameDepartment(OvertimeApprovalStep step) {
        Long currentEmployeeId = SecurityUtils.getEmployeeId();
        if (currentEmployeeId == null || step.getOvertimeRequest() == null) {
            return false;
        }

        Employee currentEmployee = employeeMapper.selectById(currentEmployeeId);
        Employee requestEmployee = employeeMapper.selectById(step.getOvertimeRequest().getEmployeeId());
        if (currentEmployee == null || requestEmployee == null) {
            return false;
        }

        Long currentDepartmentId = currentEmployee.getDepartmentId();
        Long requestDepartmentId = requestEmployee.getDepartmentId();
        return currentDepartmentId != null && currentDepartmentId.equals(requestDepartmentId);
    }

    private void validatePendingStep(OvertimeApprovalStep step) {
        if (step == null) {
            throw new RuntimeException("加班審核步驟不存在");
        }
        if (!"PENDING".equals(step.getStatus())) {
            throw new RuntimeException("此加班審核步驟目前不能處理");
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

    private void notifyNextRole(OvertimeApprovalStep nextStep) {
        String role = switch (nextStep.getStepCode()) {
            case "HR" -> "HR";
            case "FINAL" -> "EXECUTIVE";
            default -> "DEPARTMENT_MANAGER";
        };
        notificationService.notifyRole(role, "OVERTIME_PENDING", "加班申請待審核", "有加班申請等待您審核");
    }
}
