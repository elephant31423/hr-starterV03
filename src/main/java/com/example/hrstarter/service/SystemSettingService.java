package com.example.hrstarter.service;

import com.example.hrstarter.dto.system.BasicSettingsDTO;
import com.example.hrstarter.dto.system.BrandSettingsDTO;
import com.example.hrstarter.dto.system.SecuritySettingsDTO;
import org.springframework.web.multipart.MultipartFile;

public interface SystemSettingService {
    BasicSettingsDTO getBasicSettings();

    BrandSettingsDTO getBrandSettings();

    BasicSettingsDTO updateBasicSettings(BasicSettingsDTO settings);

    BrandSettingsDTO uploadLogo(MultipartFile file);

    BrandSettingsDTO uploadFavicon(MultipartFile file);

    SecuritySettingsDTO getSecuritySettings();

    SecuritySettingsDTO updateSecuritySettings(SecuritySettingsDTO settings);
}
