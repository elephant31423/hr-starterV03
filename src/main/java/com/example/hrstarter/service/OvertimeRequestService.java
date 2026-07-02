package com.example.hrstarter.service;

import com.example.hrstarter.entity.OvertimeRecord;
import com.example.hrstarter.entity.OvertimeRequest;

import java.util.List;

public interface OvertimeRequestService {
    OvertimeRequest apply(OvertimeRequest request);

    List<OvertimeRequest> listMine();

    List<OvertimeRequest> listAll();

    List<OvertimeRecord> listMyRecords();

    List<OvertimeRecord> listAllRecords();

    void updateApprovalStatus(Long overtimeRequestId, String status);

    void finalizeApprovedOvertime(Long overtimeRequestId, Long approvedBy);
}
