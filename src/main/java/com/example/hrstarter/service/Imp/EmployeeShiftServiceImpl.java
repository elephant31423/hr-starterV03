package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.employee.EmployeeShiftDTO;
import com.example.hrstarter.dto.ShiftDTO;
import com.example.hrstarter.entity.EmployeeShifts;
import com.example.hrstarter.enums.ShiftType;
import com.example.hrstarter.mapper.EmployeeShiftsMapper;
import com.example.hrstarter.service.DashBoardService;
import com.example.hrstarter.service.EmployeeShiftService;
import com.example.hrstarter.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeShiftServiceImpl implements EmployeeShiftService {

    @Autowired
    EmployeeShiftsMapper employeeShiftsMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Value("${shift.edit-buffer-days:0}")
    private int editBufferDays;

    @Override
    @Transactional
    public void assign(EmployeeShiftDTO dto) {
        Long operatorEmployeeId = SecurityUtils.getEmployeeId();
        boolean isHr = SecurityUtils.hasRole("HR");
        log.info("指派班表請求 - 操作人: {}, 目標員工: {}, 日期: {}",
                operatorEmployeeId, dto.getEmployeeId(), dto.getShiftDate());

        if (dto.getShiftDate() == null) {
            throw new RuntimeException("日期不能為空！請檢查前端傳參格式。");
        }
        // 先檢查傳進來的 DTO 是否有值
        if (dto.getEmployeeId() == null) {
            throw new RuntimeException("員工ID不能為空！請檢查前端傳參格式。");
        }

        if (!isHr) {
            LocalDate today = LocalDate.now();
            // 計算最早可修改日期 (例如 buffer=1，則今天=1/14, 最早可改=1/13)
            LocalDate earliestEditableDate = today.minusDays(editBufferDays);

            if (dto.getShiftDate().isBefore(earliestEditableDate)) {
                log.warn("非管理員嘗試修改過期班表: {}", dto.getShiftDate());
                throw new RuntimeException("超過修改時限，過去的班表不可更動！");
            }
        }
        // 3. 操作對象權限檢查 (管理員可以修改任何人，一般員工只能改自己)
        if (!isHr) {
            assert operatorEmployeeId != null;
            if (!operatorEmployeeId.equals(dto.getEmployeeId())) {
                log.warn("操作員 {} 嘗試指派他人 ({}) 班表被拒", operatorEmployeeId, dto.getEmployeeId());
                throw new AccessDeniedException("您沒有權限指派其他員工的班表");
            }
        }
        log.info("開始指派班表：{}", dto);

        assert operatorEmployeeId != null;
        if (!operatorEmployeeId.equals(dto.getEmployeeId())) {
            // 這裡可以加入更多的權限檢查邏輯
            log.warn("操作員 {} 嘗試指派其他員工 {} 的班表，拒絕操作", operatorEmployeeId, dto.getEmployeeId());
            throw new AccessDeniedException("您沒有權限指派其他員工的班表");
        }

        Long employeeId = dto.getEmployeeId();
        LocalDate shiftDate = dto.getShiftDate();
        ShiftType shiftType = dto.getShiftType();
        boolean onDuty = dto.getOnDuty();

        // 1. 檢查該員工當天是否已有紀錄
        // 這裡建議 Mapper 提供一個精簡的方法 findByDate(empId, date)
        List<ShiftDTO> existingShifts = employeeShiftsMapper.findByEmployeeAndDateRange(employeeId, shiftDate, shiftDate);

        EmployeeShifts record = new EmployeeShifts();
        record.setEmployeeId(employeeId);
        record.setShiftDate(shiftDate);
        record.setShiftType(shiftType);
        record.setOnDuty(onDuty); // 預設值，或根據 DTO 傳入
        record.setCreatedBy(operatorEmployeeId); // 記錄是誰操作的

        if (existingShifts.isEmpty()) {
            log.info("當日無紀錄，執行新增：{} {}", shiftDate, shiftType);
            employeeShiftsMapper.upsert(record);
        } else {
            log.info("當日已有紀錄，執行更新：{} {}", shiftDate, shiftType);
            employeeShiftsMapper.update(record);
        }

        eventPublisher.publishEvent(new ShiftChangedEvent(dto.getShiftDate(),employeeId));
        log.info("班表指派成功");
    }

    public record ShiftChangedEvent(LocalDate date,Long userId) {
    }
}
