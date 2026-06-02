package com.example.hrstarter.service;


import java.math.BigDecimal;

public interface LeaveCalculationService {
    BigDecimal calculateDaysBySeniority(BigDecimal seniorityYears);
}
