package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("overtime_records")
public class OvertimeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long overtimeRequestId;

    private Long employeeId;

    private LocalDate overtimeDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal hours;

    private String reason;

    private Long approvedBy;

    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String employeeName;

    @TableField(exist = false)
    private String approvedByName;
}
