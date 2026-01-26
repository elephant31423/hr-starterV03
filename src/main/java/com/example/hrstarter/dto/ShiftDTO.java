package com.example.hrstarter.dto;

import com.example.hrstarter.enums.ShiftType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShiftDTO {


        private Long id;
        private Long shiftCode;
        private String shiftName;
        private String startTime;
        private String endTime;
        private String isNight;
        private String color;
        private ShiftType shiftType;

        private Boolean onDuty;
        private LocalDate workDate;



}
