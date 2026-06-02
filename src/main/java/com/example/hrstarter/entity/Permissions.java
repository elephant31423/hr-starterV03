package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data


public class Permissions {

    @TableId(type = IdType.AUTO)
    private Long id;


    private String permissionCode;


    private String permissionName;


    private String description;


    private Long parentId;

    @TableField(value ="created_at",fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value ="updated_at",fill = FieldFill.INSERT)
    private LocalDateTime updatedAt;




}
