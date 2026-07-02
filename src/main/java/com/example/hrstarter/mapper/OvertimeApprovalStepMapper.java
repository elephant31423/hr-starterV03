package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.OvertimeApprovalStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OvertimeApprovalStepMapper {
    void insert(OvertimeApprovalStep step);

    OvertimeApprovalStep findById(@Param("id") Long id);

    OvertimeApprovalStep findNextWaitingStep(@Param("overtimeRequestId") Long overtimeRequestId, @Param("stepOrder") Integer stepOrder);

    List<OvertimeApprovalStep> findByOvertimeRequestId(@Param("overtimeRequestId") Long overtimeRequestId);

    List<OvertimeApprovalStep> findPendingSteps();

    void approve(@Param("id") Long id, @Param("userId") Long userId, @Param("comment") String comment);

    void reject(@Param("id") Long id, @Param("userId") Long userId, @Param("comment") String comment);

    void markPending(@Param("id") Long id);
}
