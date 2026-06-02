package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.hrstarter.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("leave_requests")
public class LeaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主鍵 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 員工 ID
     */
    private Long employeeId;

    /**
     * 假別類型 ID
     */
    private Long leaveTypeId;

    /**
     * 請假開始日期
     */
    private LocalDate startDate;

    /**
     * 請假結束日期
     */
    private LocalDate endDate;

    /**
     * 請假天數
     */
    private BigDecimal days;

    /**
     * 申請狀態：PENDING（待審核）、APPROVED（已批准）、REJECTED（已拒絕）
     */
    private String status;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 請假原因
     */

    private String reason;

    /**
     * 員工名稱（關聯查詢用）
     */
    @TableField(exist = false)
    private String employeeName;

    /**
     * 假別名稱（關聯查詢用）
     */
    @TableField(exist = false)
    private String leaveTypeName;
    @TableField(exist = false)
    private LeaveType leaveType;
    @TableField(exist = false)
    private LocalDateTime startTime;
    @TableField(exist = false)
    private LocalDateTime endTime;
    
    private BigDecimal leaveHours;
}