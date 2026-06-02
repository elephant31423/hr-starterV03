package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.entity.LeaveTypeEntity;
import com.example.hrstarter.service.LeaveTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {
    @Autowired
    private LeaveTypeService leaveTypeService;

    @GetMapping("/active")
    public ApiResponse<List<LeaveTypeEntity>> getActiveLeaveTypes() {
        return ApiResponse.success("獲取成功", leaveTypeService.list());
    }
}
