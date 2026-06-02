package com.example.hrstarter.dto.employee;

import lombok.Data;

@Data
public class EmployeeQueryDTO {
    private String name;
    private Long departmentId;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
