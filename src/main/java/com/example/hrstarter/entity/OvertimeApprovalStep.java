package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("overtime_approval_steps")
public class OvertimeApprovalStep {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long overtimeRequestId;

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
    private OvertimeRequest overtimeRequest;

    @TableField(exist = false)
    private String employeeName;
}
