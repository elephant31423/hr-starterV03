package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.entity.OvertimeRecord;
import com.example.hrstarter.entity.OvertimeRequest;
import com.example.hrstarter.service.OvertimeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/overtime-requests")
public class OvertimeRequestController {
    private final OvertimeRequestService overtimeRequestService;

    @PostMapping
    public ApiResponse<OvertimeRequest> apply(@RequestBody OvertimeRequest request) {
        return ApiResponse.success("加班申請已送出", overtimeRequestService.apply(request));
    }

    @GetMapping("/mine")
    public ApiResponse<List<OvertimeRequest>> mine() {
        return ApiResponse.success(overtimeRequestService.listMine());
    }

    @GetMapping
    public ApiResponse<List<OvertimeRequest>> all() {
        return ApiResponse.success(overtimeRequestService.listAll());
    }

    @GetMapping("/records/mine")
    public ApiResponse<List<OvertimeRecord>> myRecords() {
        return ApiResponse.success(overtimeRequestService.listMyRecords());
    }

    @GetMapping("/records")
    public ApiResponse<List<OvertimeRecord>> allRecords() {
        return ApiResponse.success(overtimeRequestService.listAllRecords());
    }
}
