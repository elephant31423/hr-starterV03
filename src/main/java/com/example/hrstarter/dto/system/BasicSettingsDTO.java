package com.example.hrstarter.dto.system;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BasicSettingsDTO {
    private String companyName;
    private String logoUrl;
    private String faviconUrl;
    private String standardStartTime;
    private String standardEndTime;
    private BigDecimal dailyHours;
    private BigDecimal minimumLeaveUnitHours;
    private Boolean allowHalfDayLeave;
    private Boolean allowLeaveOverdraft;
}
