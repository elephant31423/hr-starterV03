package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Notification {
     @TableId(type = IdType.AUTO)
     private Integer id;
     private Integer userId;
     private String type;
     private String content;
     private boolean read;
     private String createdAt;
}
