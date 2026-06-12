package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotificationMapper {
    void insert(Notification notification);

    List<Notification> findByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    Long countUnread(@Param("userId") Long userId);

    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    int markAllAsRead(@Param("userId") Long userId);

    Long findUserIdByEmployeeId(@Param("employeeId") Long employeeId);

    List<Long> findUserIdsByRoleKey(@Param("roleKey") String roleKey);
}
