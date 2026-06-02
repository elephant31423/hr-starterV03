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

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("leave_records")
public class LeaveRecord implements Serializable {


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
     * 假別類型：ANNUAL（特休）、SICK（病假）、PERSONAL（事假）、BIRTHDAY（生日假）
     */
//    @TableField("leave_type")
    private String leaveType;

    /**
     * 請假開始日期
     */
    private LocalDate startDate;

    /**
     * 請假結束日期
     */
    private LocalDate endDate;

    /**
     * 實際請假日期（展開用，每一天一條記錄）
     */
    private LocalDate leaveDate;



    /**
     * 請假時數（默認 8 小時）
     */
    private BigDecimal leaveHours;

    /**
     * 請假原因
     */
    private String reason;

    /**
     * 狀態：PENDING（待審核）、APPROVED（已批准）、REJECTED（已拒絕）
     */
    private String status;

    /**
     * 申請人 ID
     */
    private Long createdBy;

    /**
     * 審核人 ID
     */
    private Long approvedBy;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    /**
     * 員工名稱（關聯查詢用）
     */

    private String employeeName;

    /**
     * 申請人名稱（關聯查詢用）
     */

    private String createdByName;

    /**
     * 審核人名稱（關聯查詢用）
     */

    private String approvedByName;
}