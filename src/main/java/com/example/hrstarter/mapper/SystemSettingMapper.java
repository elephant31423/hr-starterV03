package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.SystemSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemSettingMapper {
    List<SystemSetting> findByGroup(@Param("settingGroup") String settingGroup);

    int upsert(SystemSetting setting);
}
