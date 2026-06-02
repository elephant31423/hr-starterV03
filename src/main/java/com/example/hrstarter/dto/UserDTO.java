package com.example.hrstarter.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String fullName;
    private Boolean enabled;
    private String createdAt;
    private String updatedAt;
}
