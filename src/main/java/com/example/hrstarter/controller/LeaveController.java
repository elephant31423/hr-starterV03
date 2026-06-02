package com.example.hrstarter.controller;

import com.example.hrstarter.entity.LeaveRequest;
import com.example.hrstarter.service.LeaveRequestService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leave")
@PreAuthorize("hasAuthority('leave:apply')")
public class LeaveController {

    @Resource
    private LeaveRequestService leaveService;

    @PostMapping
    public void apply(@RequestBody LeaveRequest request) {
        leaveService.applyLeave(request);
    }
}