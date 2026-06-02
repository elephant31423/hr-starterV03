package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;
@Data
public class WorkSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private LocalDate workDate;
    private Long shiftId;
    private Boolean isOnCall;
    private String remark;
}
