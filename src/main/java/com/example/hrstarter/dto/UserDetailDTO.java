package com.example.hrstarter.dto;

import com.example.hrstarter.entity.Role;
import com.example.hrstarter.entity.User;
import lombok.*;

import java.util.List;
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserDetailDTO extends User {
    private String employeeNo;
    private List<Role> roles;
    private List<Long> roleIds;

}
