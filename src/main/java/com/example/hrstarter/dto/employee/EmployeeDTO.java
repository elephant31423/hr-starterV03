package com.example.hrstarter.dto.employee;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private String empNo;
    private Long id;
    private Long userId;
    private String name;
    private Long departmentId;
    private String departmentName;
    private String title;
    private LocalDateTime hireDate;
    private Integer status;
    private String address;
    private String phone;
    private String email;
    private String birthday;
    private LocalDateTime resignDate;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
