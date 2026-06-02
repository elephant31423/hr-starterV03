package com.example.hrstarter.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class CalendarDTO implements Serializable {

    private String month;               // 2026-01
    private List<CalendarDayDTO> days;  // 1~31

}
