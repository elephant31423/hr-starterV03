package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LeaveRequestMapper {

    void insert(LeaveRequest request);

    List<LeaveRequest> findByEmployee(
            @Param("employeeId") Long employeeId
    );
}
