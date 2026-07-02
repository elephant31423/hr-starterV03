package com.example.hrstarter.dto.system;

import lombok.Data;

@Data
public class SecuritySettingsDTO {
    private Integer passwordMinLength;
    private Boolean passwordRequireLetter;
    private Boolean passwordRequireNumber;
    private Integer maxFailedLoginAttempts;
    private Integer loginLockMinutes;
    private Integer tokenExpireMinutes;
}
