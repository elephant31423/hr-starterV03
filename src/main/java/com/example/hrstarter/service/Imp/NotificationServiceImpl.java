package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.Notification;
import com.example.hrstarter.mapper.NotificationMapper;
import com.example.hrstarter.service.NotificationService;
import com.example.hrstarter.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    private final NotificationMapper notificationMapper;

    @Override
    public List<Notification> listMine(Integer limit) {
        return notificationMapper.findByUserId(currentUserId(), normalizeLimit(limit));
    }

    @Override
    public Long countUnreadMine() {
        return notificationMapper.countUnread(currentUserId());
    }

    @Override
    public void markAsRead(Long id) {
        notificationMapper.markAsRead(id, currentUserId());
    }

    @Override
    public void markAllAsRead() {
        notificationMapper.markAllAsRead(currentUserId());
    }

    @Override
    public void notifyUser(Long userId, String type, String title, String message) {
        if (userId == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notificationMapper.insert(notification);
    }

    @Override
    public void notifyEmployee(Long employeeId, String type, String title, String message) {
        Long userId = notificationMapper.findUserIdByEmployeeId(employeeId);
        notifyUser(userId, type, title, message);
    }

    @Override
    public void notifyRole(String roleKey, String type, String title, String message) {
        notificationMapper.findUserIdsByRoleKey(roleKey)
                .forEach(userId -> notifyUser(userId, type, title, message));
    }

    private Long currentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new AccessDeniedException("User is not authenticated");
        }
        return userId;
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
