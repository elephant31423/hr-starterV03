package com.example.hrstarter.service;

import com.example.hrstarter.entity.OvertimeApprovalStep;

import java.util.List;

public interface OvertimeApprovalService {
    void createApprovalFlow(Long overtimeRequestId);

    List<OvertimeApprovalStep> listPendingForCurrentUser();

    List<OvertimeApprovalStep> listSteps(Long overtimeRequestId);

    void approve(Long stepId, String comment);

    void reject(Long stepId, String comment);
}
