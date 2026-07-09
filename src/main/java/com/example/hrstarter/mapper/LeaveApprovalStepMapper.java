package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.LeaveApprovalStep;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LeaveApprovalStepMapper {
    void insert(LeaveApprovalStep step);

    LeaveApprovalStep findById(@Param("id") Long id);

    LeaveApprovalStep findPendingByLeaveRequestId(@Param("leaveRequestId") Long leaveRequestId);

    LeaveApprovalStep findNextWaitingStep(@Param("leaveRequestId") Long leaveRequestId, @Param("stepOrder") Integer stepOrder);

    List<LeaveApprovalStep> findByLeaveRequestId(@Param("leaveRequestId") Long leaveRequestId);

    List<LeaveApprovalStep> findPendingSteps();

    int approve(@Param("id") Long id, @Param("userId") Long userId, @Param("comment") String comment);

    int reject(@Param("id") Long id, @Param("userId") Long userId, @Param("comment") String comment);

    int markPending(@Param("id") Long id);

    int cancelByLeaveRequestId(@Param("leaveRequestId") Long leaveRequestId);
}
