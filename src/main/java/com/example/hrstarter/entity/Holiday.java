package com.example.hrstarter.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Holiday {
    private String date;
    private String name;
    private Boolean isNational;


}
