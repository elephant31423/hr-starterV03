package com.example.hrstarter.service.Imp;

import com.example.hrstarter.dto.*;
import com.example.hrstarter.dto.employee.EmployeeCalendarDTO;
import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.enums.ShiftType;
import com.example.hrstarter.mapper.*;
import com.example.hrstarter.service.CalendarService;
import constants.ColorConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final ShiftMapper shiftMapper;
    private final LeaveRecordMapper leaveRecordMapper;
    private final HolidayMapper holidayMapper;
    private final EmployeeMapper employeeMapper;
    private final EmployeeAnnualLeaveMapper employeeAnnualLeaveMapper;
    private final EmployeeShiftsMapper employeeShiftsMapper;

//    @Override
//    public List<CalendarDayDTO> getEmployeeMonthlyCalendar(
//            Long employeeId,
//            YearMonth yearMonth
//    ) {
//
//        LocalDate start = yearMonth.atDay(1);
//        LocalDate end = yearMonth.atEndOfMonth();
//
//        // ===== ① 初始化整個月份 =====
//        Map<LocalDate, CalendarDayDTO> map = new LinkedHashMap<>();
//
//        LocalDate date = start;
//        while (!date.isAfter(end)) {
//            CalendarDayDTO dto = new CalendarDayDTO();
//            dto.setDate(date);
//            dto.setShiftType(ShiftType.OFF);
//            dto.setOnDuty(false);
//            dto.setHoliday(false);
//            dto.setFullDayLeave(false);
//            dto.setDay(date.getDayOfMonth());
//            map.put(date, dto);
//            date = date.plusDays(1);
//        }
//
//        // ===== ② 國定假日 =====
//        holidayMapper.findByDateRange(start, end)
//                .forEach(h -> {
//                    CalendarDayDTO dto = map.get(h.getDate());
//                    if (dto != null) {
//                        dto.setHoliday(true);
//                        dto.setHolidayName(h.getName());
//                    }
//                });
//
//        // ===== ③ 班表 =====
//        employeeShiftsMapper.findByEmployeeAndDateRange(employeeId, start, end)
//                .forEach(s -> {
//                    CalendarDayDTO dto = map.get(s.getWorkDate());
//                    if (dto == null) {
//                        return; // ⛔ 超出區間直接忽略
//                    }
//
//                    dto.setShiftType(s.getShiftType());
//                    dto.setOnDuty(s.getOnDuty());
//                });
//
//        // ===== ④ 請假（覆蓋班表）=====
//        leaveRecordMapper.findByEmployeeAndDateRange(employeeId, start, end)
//                .forEach(l -> {
//                    LocalDate leaveDate = l.getLeaveDate();
//                    CalendarDayDTO dto = map.get(leaveDate);
//                    if (dto == null) return;
//
//                    dto.setLeaveType(l.getLeaveType());
//                    dto.setLeaveHours(l.getHours());
//                    dto.setFullDayLeave(l.getHours().compareTo(BigDecimal.valueOf(8)) >= 0);
//
//                    // 🔥 請假一定覆蓋班表
//                    dto.setShiftType(ShiftType.OFF);
//                    dto.setOnDuty(false);
//                });
//
//        return new ArrayList<>(map.values());
//    }

    @Override
    public CalendarDTO getCalendar(Long employeeId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();
        log.info("獲取員工 {} 的日曆，月份：{}，區間：{} ~ {}", employeeId, month, startDate, endDate);
        // ① 初始化每天
        Map<LocalDate, CalendarDayDTO> map = new LinkedHashMap<>();
        for (int i = 1; i <= month.lengthOfMonth(); i++) {
            LocalDate date = month.atDay(i);

            CalendarDayDTO dto = new CalendarDayDTO();
            dto.setDate(date);
            dto.setShiftType(ShiftType.OFF);
            dto.setOnDuty(false);
            dto.setHoliday(false);
            dto.setBirthday(false);

            map.put(date, dto);
        }
        log.info("初始化月份完成，共 {} 天", map.size());

        // ② 班表
        List<ShiftDTO> byEmployeeAndDateRange = employeeShiftsMapper.findByEmployeeAndDateRange(employeeId, startDate, endDate);
        log.info("查詢到班表記錄 {} 筆", byEmployeeAndDateRange.size());

        byEmployeeAndDateRange.forEach(s -> {
            CalendarDayDTO dto = map.get(s.getWorkDate());
            if (dto == null) {
                return; // ⛔ 超出區間直接忽略
            }

            dto.setShiftType(s.getShiftType());
            dto.setColor(s.getShiftType().getColor());
            dto.setDisplayLabel(s.getShiftType().getLabel());
            dto.setOnDuty(s.getOnDuty());
            log.info("套用班表：{}，班別：{}，上班：{}", s.getWorkDate(), s.getShiftType(), s.getOnDuty());
        });

        log.info("班表套用完成");
        // ③ 請假（覆蓋班表）
        leaveRecordMapper.findByEmployeeAndDateRange(employeeId, startDate, endDate)
                .forEach(l -> {
                    CalendarDayDTO dto = map.get(l.getLeaveDate());
                    dto.setLeaveType(l.getLeaveType());
                    dto.setLeaveHours(l.getLeaveHours());
                    dto.setShiftType(ShiftType.OFF);
                    dto.setOnDuty(false);
                    dto.setColor(ColorConstants.LEAVE);
                    dto.setDisplayLabel("請假");

                });
        log.info("請假套用完成");
        // ④ 國定假日
        holidayMapper.findByDateRange(startDate, endDate)
                .forEach(h -> {
                    CalendarDayDTO dto = map.get(h.getDate());
                    if (dto != null) {
                        dto.setHoliday(true);
                        dto.setHolidayName(h.getName());
                        dto.setColor(ColorConstants.HOLIDAY);
                        dto.setDisplayLabel(h.getName());
                    }
                });
        log.info("國定假日套用完成");
        // ⑤ 生日
        LocalDate birthday = employeeMapper.findBirthday(employeeId);
        if (birthday != null) {
            map.values().forEach(dto -> {
                if (dto.getDate().getMonth() == birthday.getMonth()
                        && dto.getDate().getDayOfMonth() == birthday.getDayOfMonth()) {
                    dto.setBirthday(true);
                }
            });
        }
        log.info("生日套用完成");
        CalendarDTO result = new CalendarDTO();
        result.setMonth(month.toString());
        result.setDays(new ArrayList<>(map.values()));
        log.info("日曆組裝完成");
//        log.info("日曆內容：{}", result);

        return result;
    }

    @Override
    public List<EmployeeCalendarDTO> getDepartmentCalendar(YearMonth ym, Long departmentId) {
        log.info("獲取部門日曆總覽，月份：{}，部門ID：{}", ym, departmentId);
        List<Employee> employees;
        if (departmentId == null) {
            log.info("未指定部門，獲取所有在職員工");
            employees = employeeMapper.findAllActive();
            log.info("查詢到在職員工 {} 人", employees.size());
        } else {
            employees = employeeMapper.findByDepartmentId(departmentId);
        }

        List<EmployeeCalendarDTO> result = new ArrayList<>();

        for (Employee emp : employees) {
            CalendarDTO calendar = getCalendar(emp.getId(), ym);
            EmployeeCalendarDTO empCalDto = new EmployeeCalendarDTO();
            empCalDto.setEmployeeId(emp.getId());
            empCalDto.setEmployeeName(emp.getName());
            empCalDto.setDepartmentId(emp.getDepartmentId());
            empCalDto.setDays(calendar.getDays());
            log.info("組裝員工 {} 的日曆完成", emp.getName());
            result.add(empCalDto);

        }

        return result;
    }

//    @Override
//    public CalendarMonthDTO getEmployeeCalendar(Long employeeId, int year, int month) {
//        // ===== ① 計算月份區間 =====
//        LocalDate start = LocalDate.of(year, month, 1);
//        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
//
//        // ===== ② 初始化月曆 Map（每天一格）=====
//        Map<LocalDate, CalendarDayDTO> map = new LinkedHashMap<>();
//
//        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
//
//            CalendarDayDTO dto = new CalendarDayDTO();
//            dto.setDate(date);
//            dto.setYear(date.getYear());
//            dto.setMonth(date.getMonthValue());
//            dto.setDay(date.getDayOfMonth());
//            dto.setDayOfWeek(date.getDayOfWeek().getValue());
//
//            // 預設
//            dto.setShiftType(ShiftType.OFF);
//            dto.setOnDuty(false);
//            dto.setHoliday(false);
//            dto.setFullDayLeave(false);
//
//            map.put(date, dto);
//        }
//
//        // ===== ③ 套用國定假日 =====
//        holidayMapper.findByDateRange(start, end)
//                .forEach(h -> {
//                    CalendarDayDTO dto = map.get(h.getDate());
//                    if (dto == null) return;
//
//                    dto.setHoliday(true);
//                    dto.setHolidayName(h.getName());
//                });
//
//        // ===== ④ 套用班表 =====
//        employeeShiftsMapper.findByEmployeeAndDateRange(employeeId, start, end)
//                .forEach(s -> {
//                    CalendarDayDTO dto = map.get(s.getWorkDate());
//                    if (dto == null) return;
//
//                    dto.setShiftType(s.getShiftType());
//                    dto.setOnDuty(s.getOnDuty());
//                });
//
//        // ===== ⑤ 套用請假（覆蓋班表）=====
//        leaveRecordMapper.findByEmployeeAndDateRange(employeeId, start, end)
//                .forEach(l -> {
//
//                    LocalDate d = l.getStartDate();
//                    while (!d.isAfter(l.getEndDate())) {
//
//                        CalendarDayDTO dto = map.get(d);
//                        if (dto == null) {
//                            d = d.plusDays(1);
//                            continue;
//                        }
//
//                        dto.setLeaveType(l.getLeaveType());
//                        dto.setLeaveHours(l.getHours());
//                        dto.setFullDayLeave(l.getHours().compareTo(BigDecimal.valueOf(8)) >= 0);
//
//                        // 🔥 請假覆蓋一切
//                        dto.setShiftType(ShiftType.OFF);
//                        dto.setOnDuty(false);
//
//                        d = d.plusDays(1);
//                    }
//                });
//
//
//        // ===== ⑥ 計算顯示用欄位（Label + Color）=====
//        map.values().forEach(this::applyDisplayStyle);
//
//        // ===== ⑦ 組裝回傳 DTO =====
//        CalendarMonthDTO result = new CalendarMonthDTO();
//        result.setEmployeeId(employeeId);
//        result.setYear(year);
//        result.setMonth(month);
//        result.setDays(new ArrayList<>(map.values()));
//
//        return result;
//    }

    /**
     * 顯示邏輯集中在後端
     */
    private void applyDisplayStyle(CalendarDayDTO dto) {

        if (dto.getLeaveType() != null) {
            dto.setDisplayLabel(dto.getLeaveType() + "假");
            dto.setColor("#F44336");
            return;
        }

        if (dto.isHoliday()) {
            dto.setDisplayLabel(dto.getHolidayName());
            dto.setColor("#FF9800");
            return;
        }

        if (dto.getShiftType() != null) {
            switch (dto.getShiftType()) {
                case MORNING -> {
                    dto.setDisplayLabel("早班");
                    dto.setColor("#4CAF50");
                }
                case MIDDLE -> {
                    dto.setDisplayLabel("中班");
                    dto.setColor("#2196F3");
                }
                case NIGHT -> {
                    dto.setDisplayLabel("夜班");
                    dto.setColor("#673AB7");
                }
                case OFF -> {
                    dto.setDisplayLabel("休息");
                    dto.setColor("#9E9E9E");
                }
            }
        }
    }
}
