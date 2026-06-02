package com.example.hrstarter.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;

@Data

@AllArgsConstructor
public class Roles {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleKey;

    private String roleName;

    private String status = "ACTIVE";

    private Long createdBy;

    private String description;

    // --- 自動填充欄位 ---
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // --- 非資料庫欄位 (使用 @TableField(exist = false)) ---

    @TableField(exist = false)
    private List<Long> permissionIds;

    /**
     * 注意：MyBatis-Plus 不支援 JPA 的 @ManyToMany 自動映射。
     * 通常我們會透過手寫 XML 的 JOIN 查詢，或在 Service 層二次查詢來填充這個 Set。
     */
    @TableField(exist = false)
    private Set<Permissions> permissions;
    public Roles() {

    }



}
