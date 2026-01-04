package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.DashboardStatsDTO;
import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.service.DashBoardService;
import com.example.hrstarter.service.Imp.DashBoardServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/dashboard")
public class DashBoardController {


    @Autowired
    DashBoardService dashBoardService;
    @RequestMapping("/status")
    @GetMapping
    public ResponseEntity<?> list() {
        DashboardStatsDTO dashboardStats = dashBoardService.getDashboardStats();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(dashboardStats));
    }



}
