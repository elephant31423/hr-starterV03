package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("leave_approval_steps")
public class LeaveApprovalStep {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long leaveRequestId;

    private Integer stepOrder;

    private String stepCode;

    private String approverRole;

    private Long approverUserId;

    private String status;

    private String comment;

    private LocalDateTime approvedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private LeaveRequest leaveRequest;

    @TableField(exist = false)
    private String employeeName;

    @TableField(exist = false)
    private String leaveTypeName;
}
