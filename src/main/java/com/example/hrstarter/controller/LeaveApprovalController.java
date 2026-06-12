package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.LeaveApprovalActionDTO;
import com.example.hrstarter.entity.LeaveApprovalStep;
import com.example.hrstarter.service.LeaveApprovalService;
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
@RequestMapping("/api/leave-approvals")
public class LeaveApprovalController {
    private final LeaveApprovalService leaveApprovalService;

    @GetMapping("/pending")
    public ApiResponse<List<LeaveApprovalStep>> pendingForCurrentUser() {
        return ApiResponse.success(leaveApprovalService.listPendingForCurrentUser());
    }

    @GetMapping("/requests/{leaveRequestId}/steps")
    public ApiResponse<List<LeaveApprovalStep>> steps(@PathVariable Long leaveRequestId) {
        return ApiResponse.success(leaveApprovalService.listSteps(leaveRequestId));
    }

    @PatchMapping("/{stepId}/approve")
    public ApiResponse<?> approve(@PathVariable Long stepId, @RequestBody(required = false) LeaveApprovalActionDTO action) {
        leaveApprovalService.approve(stepId, action == null ? null : action.getComment());
        return ApiResponse.success("核准成功");
    }

    @PatchMapping("/{stepId}/reject")
    public ApiResponse<?> reject(@PathVariable Long stepId, @RequestBody(required = false) LeaveApprovalActionDTO action) {
        leaveApprovalService.reject(stepId, action == null ? null : action.getComment());
        return ApiResponse.success("駁回成功");
    }
}
