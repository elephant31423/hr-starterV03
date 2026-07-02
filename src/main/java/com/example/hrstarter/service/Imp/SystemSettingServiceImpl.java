package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.PermissionTreeDTO;
import com.example.hrstarter.dto.system.BasicSettingsDTO;
import com.example.hrstarter.dto.system.BrandSettingsDTO;
import com.example.hrstarter.dto.system.SecuritySettingsDTO;
import com.example.hrstarter.entity.SystemSetting;
import com.example.hrstarter.mapper.SystemSettingMapper;
import com.example.hrstarter.service.SystemSettingService;
import com.example.hrstarter.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {
    private static final String BASIC = "BASIC";
    private static final String SECURITY = "SECURITY";
    private static final Path BRANDING_UPLOAD_DIR = Paths.get("uploads", "branding").toAbsolutePath().normalize();
    private static final long MAX_BRANDING_FILE_SIZE = 10L * 1024 * 1024;

    private final SystemSettingMapper systemSettingMapper;

    @Override
    public BasicSettingsDTO getBasicSettings() {
        Map<String, String> values = valuesByKey(BASIC);
        BasicSettingsDTO dto = new BasicSettingsDTO();
        dto.setCompanyName(value(values, "company.name", "Enterprise HR System"));
        dto.setLogoUrl(value(values, "company.logoUrl", ""));
        dto.setFaviconUrl(value(values, "company.faviconUrl", ""));
        dto.setStandardStartTime(value(values, "work.standardStartTime", "09:00"));
        dto.setStandardEndTime(value(values, "work.standardEndTime", "18:00"));
        dto.setDailyHours(decimalValue(values, "work.dailyHours", "8"));
        dto.setMinimumLeaveUnitHours(decimalValue(values, "leave.minimumUnitHours", "1"));
        dto.setAllowHalfDayLeave(booleanValue(values, "leave.allowHalfDay", true));
        dto.setAllowLeaveOverdraft(booleanValue(values, "leave.allowOverdraft", false));
        return dto;
    }

    @Override
    public BrandSettingsDTO getBrandSettings() {
        Map<String, String> values = valuesByKey(BASIC);
        BrandSettingsDTO dto = new BrandSettingsDTO();
        dto.setCompanyName(value(values, "company.name", "Enterprise HR System"));
        dto.setLogoUrl(value(values, "company.logoUrl", ""));
        dto.setFaviconUrl(value(values, "company.faviconUrl", ""));
        return dto;
    }

    @Override
    @Transactional
    public BasicSettingsDTO updateBasicSettings(BasicSettingsDTO settings) {
        if (settings == null) {
            settings = getBasicSettings();
        }

        upsert(BASIC, "company.name", nonBlank(settings.getCompanyName(), "Enterprise HR System"), "STRING", "Company or system display name");
        upsert(BASIC, "company.logoUrl", valueOrEmpty(settings.getLogoUrl()), "STRING", "Company logo URL");
        upsert(BASIC, "company.faviconUrl", valueOrEmpty(settings.getFaviconUrl()), "STRING", "Browser favicon URL");
        upsert(BASIC, "work.standardStartTime", nonBlank(settings.getStandardStartTime(), "09:00"), "TIME", "Standard start time");
        upsert(BASIC, "work.standardEndTime", nonBlank(settings.getStandardEndTime(), "18:00"), "TIME", "Standard end time");
        upsert(BASIC, "work.dailyHours", positiveDecimal(settings.getDailyHours(), "8"), "DECIMAL", "Daily standard work hours");
        upsert(BASIC, "leave.minimumUnitHours", positiveDecimal(settings.getMinimumLeaveUnitHours(), "1"), "DECIMAL", "Minimum leave unit in hours");
        upsert(BASIC, "leave.allowHalfDay", stringValue(settings.getAllowHalfDayLeave(), true), "BOOLEAN", "Allow half-day leave");
        upsert(BASIC, "leave.allowOverdraft", stringValue(settings.getAllowLeaveOverdraft(), false), "BOOLEAN", "Allow leave overdraft");
        return getBasicSettings();
    }

    @Override
    @Transactional
    public BrandSettingsDTO uploadLogo(MultipartFile file) {
        String url = saveImage(file, "logo");
        upsert(BASIC, "company.logoUrl", url, "STRING", "Company logo URL");
        return getBrandSettings();
    }

    @Override
    @Transactional
    public BrandSettingsDTO uploadFavicon(MultipartFile file) {
        String url = saveImage(file, "favicon");
        upsert(BASIC, "company.faviconUrl", url, "STRING", "Browser favicon URL");
        return getBrandSettings();
    }

    @Override
    public SecuritySettingsDTO getSecuritySettings() {
        Map<String, String> values = valuesByKey(SECURITY);
        SecuritySettingsDTO dto = new SecuritySettingsDTO();
        dto.setPasswordMinLength(intValue(values, "password.minLength", 8));
        dto.setPasswordRequireLetter(booleanValue(values, "password.requireLetter", true));
        dto.setPasswordRequireNumber(booleanValue(values, "password.requireNumber", true));
        dto.setMaxFailedLoginAttempts(intValue(values, "login.maxFailedAttempts", 5));
        dto.setLoginLockMinutes(intValue(values, "login.lockMinutes", 15));
        dto.setTokenExpireMinutes(intValue(values, "session.tokenExpireMinutes", 480));
        return dto;
    }

    @Override
    @Transactional
    public SecuritySettingsDTO updateSecuritySettings(SecuritySettingsDTO settings) {
        if (settings == null) {
            settings = getSecuritySettings();
        }

        upsert(SECURITY, "password.minLength", positiveInt(settings.getPasswordMinLength(), 8), "INTEGER", "Password minimum length");
        upsert(SECURITY, "password.requireLetter", stringValue(settings.getPasswordRequireLetter(), true), "BOOLEAN", "Password requires letter");
        upsert(SECURITY, "password.requireNumber", stringValue(settings.getPasswordRequireNumber(), true), "BOOLEAN", "Password requires number");
        upsert(SECURITY, "login.maxFailedAttempts", positiveInt(settings.getMaxFailedLoginAttempts(), 5), "INTEGER", "Maximum failed login attempts");
        upsert(SECURITY, "login.lockMinutes", positiveInt(settings.getLoginLockMinutes(), 15), "INTEGER", "Login lock minutes");
        upsert(SECURITY, "session.tokenExpireMinutes", positiveInt(settings.getTokenExpireMinutes(), 480), "INTEGER", "Token expiration minutes");
        return getSecuritySettings();
    }

    private Map<String, String> valuesByKey(String group) {
        List<SystemSetting> settings = systemSettingMapper.findByGroup(group);
        return settings.stream().collect(Collectors.toMap(SystemSetting::getSettingKey, SystemSetting::getSettingValue, (a, b) -> a));
    }

    private void upsert(String group, String key, String value, String type, String description) {
        SystemSetting setting = new SystemSetting();
        setting.setSettingGroup(group);
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        setting.setValueType(type);
        setting.setDescription(description);
        setting.setUpdatedBy(currentUsername());
        systemSettingMapper.upsert(setting);
    }

    private String currentUsername() {
        PermissionTreeDTO.UserPrincipal user = SecurityUtils.getLoginUser();
        return user == null ? null : user.getUsername();
    }

    private String value(Map<String, String> values, String key, String defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    private BigDecimal decimalValue(Map<String, String> values, String key, String defaultValue) {
        return new BigDecimal(value(values, key, defaultValue));
    }

    private Boolean booleanValue(Map<String, String> values, String key, boolean defaultValue) {
        return Boolean.parseBoolean(value(values, key, String.valueOf(defaultValue)));
    }

    private Integer intValue(Map<String, String> values, String key, int defaultValue) {
        return Integer.parseInt(value(values, key, String.valueOf(defaultValue)));
    }

    private String nonBlank(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value.trim() : defaultValue;
    }

    private String valueOrEmpty(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    private String positiveDecimal(BigDecimal value, String defaultValue) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            return defaultValue;
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private String positiveInt(Integer value, int defaultValue) {
        if (value == null || value <= 0) {
            return String.valueOf(defaultValue);
        }
        return String.valueOf(value);
    }

    private String stringValue(Boolean value, boolean defaultValue) {
        return String.valueOf(value == null ? defaultValue : value);
    }

    private String saveImage(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Upload file is required");
        }
        if (file.getSize() > MAX_BRANDING_FILE_SIZE) {
            throw new IllegalArgumentException("Image file must be 10MB or smaller");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        String filename = prefix + "-" + UUID.randomUUID() + extensionFrom(file.getOriginalFilename(), contentType);
        try {
            Files.createDirectories(BRANDING_UPLOAD_DIR);
            Path target = BRANDING_UPLOAD_DIR.resolve(filename).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/branding/" + filename;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save uploaded file", e);
        }
    }

    private String extensionFrom(String originalFilename, String contentType) {
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase(Locale.ROOT);
            if (ext.matches("\\.(png|jpg|jpeg|gif|webp|svg|ico)")) {
                return ext;
            }
        }
        if ("image/svg+xml".equals(contentType)) return ".svg";
        if ("image/x-icon".equals(contentType) || "image/vnd.microsoft.icon".equals(contentType)) return ".ico";
        if ("image/jpeg".equals(contentType)) return ".jpg";
        if ("image/webp".equals(contentType)) return ".webp";
        return ".png";
    }
}
