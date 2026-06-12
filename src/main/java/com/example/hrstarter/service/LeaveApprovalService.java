package com.example.hrstarter.service;

import com.example.hrstarter.entity.LeaveApprovalStep;
import com.example.hrstarter.entity.LeaveRequest;

import java.util.List;

public interface LeaveApprovalService {
    void createApprovalFlow(LeaveRequest leaveRequest);

    List<LeaveApprovalStep> listPendingForCurrentUser();

    List<LeaveApprovalStep> listSteps(Long leaveRequestId);

    void approve(Long stepId, String comment);

    void reject(Long stepId, String comment);
}
