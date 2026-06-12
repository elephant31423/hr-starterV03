package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.entity.Notification;
import com.example.hrstarter.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<Notification>> listMine(@RequestParam(defaultValue = "20") Integer limit) {
        return ApiResponse.success(notificationService.listMine(limit));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> countUnreadMine() {
        return ApiResponse.success(notificationService.countUnreadMine());
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("通知已讀");
    }

    @PatchMapping("/read-all")
    public ApiResponse<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.success("通知已全部標記為已讀");
    }
}
