package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String fullName;

    private Boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    /** 🔗 對應員工 */
    private Long employeeId;
    private Long version;
//    public User() {
//
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public Boolean getEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(Boolean enabled) {
//        this.enabled = enabled;
//    }
//
//    public User(String username, String password, String fullName, Boolean enabled) {
//        this.username = username;
//        this.password = password;
//        this.fullName = fullName;
//        this.enabled = enabled;
//    }
//
//    public Long getId() {
//        return id;
//    }
}
