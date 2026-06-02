package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class Employee {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("emp_no")
    private String empNo;
    @TableField("user_id")
    private Long userId;
    @TableField("name")
    private String name;
    @TableField("department_id")
    private Long departmentId;
    @TableField(exist = false)
    private String departmentName;
    @TableField("title")
    private String title;
    @TableField("hire_date")
    private LocalDate hireDate;
    @TableField("status")
    private Integer status;
    @TableField("address")
    private String address;
    @TableField("phone")
    private String phone;
    @TableField("email")
    private String email;
    @TableField("birthday")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String birthday;
    @TableField("resign_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime resignDate;
    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;
    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;


}
