package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.system.BasicSettingsDTO;
import com.example.hrstarter.dto.system.BrandSettingsDTO;
import com.example.hrstarter.dto.system.SecuritySettingsDTO;
import com.example.hrstarter.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system-settings")
public class SystemSettingController {
    private final SystemSettingService systemSettingService;

    @GetMapping("/branding")
    public ApiResponse<BrandSettingsDTO> getBrandSettings() {
        return ApiResponse.success(systemSettingService.getBrandSettings());
    }

    @GetMapping("/basic")
    @PreAuthorize("hasAnyAuthority('setting:view', 'setting:update', 'permission:view')")
    public ApiResponse<BasicSettingsDTO> getBasicSettings() {
        return ApiResponse.success(systemSettingService.getBasicSettings());
    }

    @PutMapping("/basic")
    @PreAuthorize("hasAnyAuthority('setting:update', 'permission:view')")
    public ApiResponse<BasicSettingsDTO> updateBasicSettings(@RequestBody BasicSettingsDTO settings) {
        return ApiResponse.success("Settings updated", systemSettingService.updateBasicSettings(settings));
    }

    @PostMapping("/branding/logo")
    @PreAuthorize("hasAnyAuthority('setting:update', 'permission:view')")
    public ApiResponse<BrandSettingsDTO> uploadLogo(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success("Logo uploaded", systemSettingService.uploadLogo(file));
    }

    @PostMapping("/branding/favicon")
    @PreAuthorize("hasAnyAuthority('setting:update', 'permission:view')")
    public ApiResponse<BrandSettingsDTO> uploadFavicon(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success("Favicon uploaded", systemSettingService.uploadFavicon(file));
    }

    @GetMapping("/security")
    @PreAuthorize("hasAnyAuthority('setting:view', 'setting:update', 'permission:view')")
    public ApiResponse<SecuritySettingsDTO> getSecuritySettings() {
        return ApiResponse.success(systemSettingService.getSecuritySettings());
    }

    @PutMapping("/security")
    @PreAuthorize("hasAnyAuthority('setting:update', 'permission:view')")
    public ApiResponse<SecuritySettingsDTO> updateSecuritySettings(@RequestBody SecuritySettingsDTO settings) {
        return ApiResponse.success("Settings updated", systemSettingService.updateSecuritySettings(settings));
    }
}
