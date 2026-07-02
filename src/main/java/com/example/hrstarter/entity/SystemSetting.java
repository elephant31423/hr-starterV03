package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_settings")
public class SystemSetting {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("setting_group")
    private String settingGroup;

    @TableField("setting_key")
    private String settingKey;

    @TableField("setting_value")
    private String settingValue;

    @TableField("value_type")
    private String valueType;

    private String description;

    @TableField("updated_by")
    private String updatedBy;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
