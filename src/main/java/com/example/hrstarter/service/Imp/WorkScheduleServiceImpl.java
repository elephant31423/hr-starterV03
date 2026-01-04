package com.example.hrstarter.service.Imp;

import com.example.hrstarter.entity.WorkSchedule;
import com.example.hrstarter.mapper.WorkScheduleMapper;
import com.example.hrstarter.service.WorkScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
@Slf4j
public class WorkScheduleServiceImpl implements WorkScheduleService {

    WorkScheduleMapper workScheduleMapper;

    public WorkScheduleServiceImpl(WorkScheduleMapper workScheduleMapper) {
        this.workScheduleMapper = workScheduleMapper;

    }

    @Override
    public List<WorkSchedule> getMonthlySchedule(Long employeeId, YearMonth month) {
        return workScheduleMapper.findByEmployeeAndMonth(
                employeeId,
                month.atDay(1),
                month.atEndOfMonth()
        );


    }

    @Transactional
    @Override
    public void assignShift(WorkSchedule schedule) {
        // 先刪再加（調班安全）
        workScheduleMapper.deleteByEmployeeAndDate(
                schedule.getEmployeeId(),
                schedule.getWorkDate()
        );
        workScheduleMapper.insert(schedule);
    }
}
