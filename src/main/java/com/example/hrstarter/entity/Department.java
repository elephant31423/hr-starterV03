package com.example.hrstarter.entity;

import lombok.Data;

@Data
public class Department {
    private Long id;
    private String name;
    private Long parentId;
    private String code;



}
