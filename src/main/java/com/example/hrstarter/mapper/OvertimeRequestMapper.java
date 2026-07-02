package com.example.hrstarter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hrstarter.entity.OvertimeRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OvertimeRequestMapper extends BaseMapper<OvertimeRequest> {
    OvertimeRequest findDetailById(@Param("id") Long id);

    List<OvertimeRequest> findByEmployeeId(@Param("employeeId") Long employeeId);

    List<OvertimeRequest> findAll();

    void updateStatus(@Param("id") Long id, @Param("status") String status);
}
