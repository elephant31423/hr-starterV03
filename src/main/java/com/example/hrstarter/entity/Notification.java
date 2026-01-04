package com.example.hrstarter.entity;

import lombok.Data;

@Data
public class Notification {

     private Integer id;
     private Integer userId;
     private String type;
     private String content;
     private boolean read;
     private String createdAt;
}
