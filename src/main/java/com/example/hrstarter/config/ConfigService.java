package com.example.hrstarter.config;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigService {
    private final Map<String, String> configMap = new HashMap<>();

    public ConfigService() {
        // 預設配置
        configMap.put("LEAVE_SYSTEM_TYPE", "ANNIVERSARY"); // 或 CALENDAR
        configMap.put("HOURS_PER_DAY", "8.0");
    }

    public String getValueByKey(String key) {
        return configMap.getOrDefault(key, "");
    }
    public BigDecimal getHoursPerDay() {
        return new BigDecimal(getValueByKey("HOURS_PER_DAY"));
    }
}
