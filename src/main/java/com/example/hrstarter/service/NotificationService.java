package com.example.hrstarter.service;

import com.example.hrstarter.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> listMine(Integer limit);

    Long countUnreadMine();

    void markAsRead(Long id);

    void markAllAsRead();

    void notifyUser(Long userId, String type, String title, String message);

    void notifyEmployee(Long employeeId, String type, String title, String message);

    void notifyRole(String roleKey, String type, String title, String message);
}
