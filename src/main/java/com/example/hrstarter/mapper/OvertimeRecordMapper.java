package com.example.hrstarter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hrstarter.entity.OvertimeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OvertimeRecordMapper extends BaseMapper<OvertimeRecord> {
    List<OvertimeRecord> findByEmployeeId(@Param("employeeId") Long employeeId);

    List<OvertimeRecord> findAll();
}
