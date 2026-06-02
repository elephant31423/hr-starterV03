package com.example.hrstarter.dto;

import com.example.hrstarter.entity.Roles;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@ToString(callSuper = true)
public class UserDetailDTO  implements Serializable {
    private Long id;
    private String password;
    private String employeeNo;
    private List<Roles> roles;
    private List<Long> roleIds;
    private String username;
    private String fullName;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long employeeId;
    private Long version;
}
