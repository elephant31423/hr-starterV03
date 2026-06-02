package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("leave_type")
public class LeaveTypeEntity {
    private Long id;
    private String code;
    private String name;
    private Boolean paid;
    private Boolean needApprove;
}