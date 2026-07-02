package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.OvertimeApprovalActionDTO;
import com.example.hrstarter.entity.OvertimeApprovalStep;
import com.example.hrstarter.service.OvertimeApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/overtime-approvals")
public class OvertimeApprovalController {
    private final OvertimeApprovalService overtimeApprovalService;

    @GetMapping("/pending")
    public ApiResponse<List<OvertimeApprovalStep>> pendingForCurrentUser() {
        return ApiResponse.success(overtimeApprovalService.listPendingForCurrentUser());
    }

    @GetMapping("/requests/{overtimeRequestId}/steps")
    public ApiResponse<List<OvertimeApprovalStep>> steps(@PathVariable Long overtimeRequestId) {
        return ApiResponse.success(overtimeApprovalService.listSteps(overtimeRequestId));
    }

    @PatchMapping("/{stepId}/approve")
    public ApiResponse<?> approve(@PathVariable Long stepId, @RequestBody(required = false) OvertimeApprovalActionDTO action) {
        overtimeApprovalService.approve(stepId, action == null ? null : action.getComment());
        return ApiResponse.success("加班審核已核准");
    }

    @PatchMapping("/{stepId}/reject")
    public ApiResponse<?> reject(@PathVariable Long stepId, @RequestBody(required = false) OvertimeApprovalActionDTO action) {
        overtimeApprovalService.reject(stepId, action == null ? null : action.getComment());
        return ApiResponse.success("加班審核已駁回");
    }
}
