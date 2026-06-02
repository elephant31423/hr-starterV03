package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;

@Data

@TableName("audit_logs")
public class AuditLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("username")
    private String username;

    @TableField("action")
    private String action; // CREATE, UPDATE, DELETE

    @TableField("entity_type")
    private String entityType; // ROLE, USER, EMPLOYEE

    @TableField("entity_id")
    private Long entityId;

    @TableField(value = "old_value", typeHandler = JacksonTypeHandler.class)
    private String oldValue;

    @TableField(value = "new_value", typeHandler = JacksonTypeHandler.class)
    private String newValue;

    @TableField("status")
    private String status; // SUCCESS, FAILURE

    @TableField("error_message")
    private String errorMessage;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("user_agent")
    private String userAgent;

    @TableField(value ="created_at",fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


}