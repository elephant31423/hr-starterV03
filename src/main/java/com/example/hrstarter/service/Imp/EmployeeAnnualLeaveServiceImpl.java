package com.example.hrstarter.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.hrstarter.LeaveCalculation.LeaveCalculationStrategy;
import com.example.hrstarter.dto.EmployeeAnnualLeaveDTO;
import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.entity.EmployeeAnnualLeaves;
import com.example.hrstarter.mapper.EmployeeAnnualLeaveMapper;
import com.example.hrstarter.config.ConfigService;
import com.example.hrstarter.service.EmployeeAnnualLeaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 員工年度特休業務邏輯實現類
 *
 * @author HR System
 * @date 2024-01-30
 */
@Slf4j
@Service
@PreAuthorize("hasAuthority('annualLeave:edit')")
public class EmployeeAnnualLeaveServiceImpl extends ServiceImpl<EmployeeAnnualLeaveMapper, EmployeeAnnualLeaves>
        implements EmployeeAnnualLeaveService {

    @Autowired
    private Map<String, LeaveCalculationStrategy> strategyMap;

    @Autowired
    private ConfigService configService;

    @Override
    public void refreshEmployeeLeave(Employee employee, int year, String systemType) {
        // systemType 傳入 "ANNIVERSARY" 或 "CALENDAR"
        LeaveCalculationStrategy strategy = strategyMap.get(systemType);

        BigDecimal totalHors = strategy.calculateDays(LocalDate.from(employee.getHireDate()), year);
        BigDecimal finalHours = totalHors.multiply(new BigDecimal("2"))
                .setScale(0, RoundingMode.HALF_UP)
                .divide(new BigDecimal("2"), 1, RoundingMode.HALF_UP);

        // 更新到數據庫
        EmployeeAnnualLeaves eal = new EmployeeAnnualLeaves();
        eal.setEmployeeId(employee.getId());
        eal.setYear(year);
        eal.setTotalHours(totalHors);
        eal.setRemainHours(finalHours);

        this.updateAnnualLeave(eal);
    }

    @Override
    public void updateById(EmployeeAnnualLeaveDTO annualLeave) {
        EmployeeAnnualLeaves entity = new EmployeeAnnualLeaves();
        entity.setId(annualLeave.getId());
        entity.setEmployeeId(annualLeave.getEmployeeId());
        entity.setYear(annualLeave.getYear());
        entity.setTotalHours(annualLeave.getTotalHours());
        entity.setUsedHours(annualLeave.getUsedHours());
        entity.setRemainHours(annualLeave.getRemainHours());

        this.updateById(entity);
    }

    @Override

    public EmployeeAnnualLeaveDTO getAnnualLeaveByEmployeeIdAndYear(Long employeeId, Integer year) {
        log.info("查詢員工年度特休配額：員工ID={}, 年份={}", employeeId, year);
        EmployeeAnnualLeaveDTO employeeAnnualLeaveDTO = convertToDTO(this.baseMapper.selectByEmployeeIdAndYear(employeeId, year));
        log.info("查詢員工年度特休配額： getUsagePercentage{}, 年份={}", employeeAnnualLeaveDTO.getUsagePercentage(), year);
        return employeeAnnualLeaveDTO;
    }

    @Override
    public Page<EmployeeAnnualLeaveDTO> getEmployeeAnnualLeaves(Integer pageNum, Integer pageSize, Long employeeId) {
        log.info("查詢員工所有年度特休配額：員工ID={}, 頁碼={}, 每頁數量={}", employeeId, pageNum, pageSize);

        Page<EmployeeAnnualLeaves> page = new Page<>(pageNum, pageSize);
        Page<EmployeeAnnualLeaves> result = this.baseMapper.selectByEmployeeId(page, employeeId);

        return convertToDTO(result);
    }

    @Override
    public Page<EmployeeAnnualLeaveDTO> getAnnualLeavesByYear(Integer pageNum, Integer pageSize, Integer year) {
        log.info("查詢指定年份所有員工的特休配額：年份={}, 頁碼={}, 每頁數量={}", year, pageNum, pageSize);

        Page<EmployeeAnnualLeaves> page = new Page<>(pageNum, pageSize);
        Page<EmployeeAnnualLeaves> result = this.baseMapper.selectByYear(page, year);

        return convertToDTO(result);
    }

    @Override
    public List<EmployeeAnnualLeaves> getAllAnnualLeavesByYear(Integer year) {
        log.info("查詢指定年份所有員工的特休配額：年份={}", year);

        return this.baseMapper.selectAllByYear(year);
    }

    @Override
    public EmployeeAnnualLeaves getLatestAnnualLeaveByEmployeeId(Long employeeId) {
        log.info("查詢員工最新年份的特休配額：員工ID={}", employeeId);

        return this.baseMapper.selectLatestByEmployeeId(employeeId);
    }

    @Override
    public List<EmployeeAnnualLeaves> getAnnualLeavesByEmployeeIdsAndYear(List<Long> employeeIds, Integer year) {
        log.info("批量查詢員工特休配額：員工ID列表={}, 年份={}", employeeIds, year);

        return this.baseMapper.selectByEmployeeIdsAndYear(employeeIds, year);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAnnualLeave(EmployeeAnnualLeaves annualLeave) {
        log.info("新增年度特休配額：員工ID={}, 年份={}, 總天數={}",
                annualLeave.getEmployeeId(), annualLeave.getYear(), annualLeave.getTotalHours());

        // 驗證是否已存在
        if (hasAnnualLeaveByEmployeeIdAndYear(annualLeave.getEmployeeId(), annualLeave.getYear())) {
            log.warn("員工年度特休配額已存在：員工ID={}, 年份={}",
                    annualLeave.getEmployeeId(), annualLeave.getYear());
            throw new RuntimeException("該員工該年份的特休配額已存在");
        }

        // 初始化已使用天數和剩餘天數
        annualLeave.setUsedHours(BigDecimal.ZERO);
        annualLeave.setRemainHours(annualLeave.getTotalHours());

        boolean saved = this.save(annualLeave);
        if (saved) {
            log.info("年度特休配額新增成功：員工ID={}, 年份={}", annualLeave.getEmployeeId(), annualLeave.getYear());
        } else {
            log.error("年度特休配額新增失敗：員工ID={}, 年份={}", annualLeave.getEmployeeId(), annualLeave.getYear());
        }

        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAnnualLeaveBatch(List<EmployeeAnnualLeaves> annualLeaves) {
        for (EmployeeAnnualLeaves item : annualLeaves) {
            // 根據 employee_id 和 year 尋找是否已存在記錄
            EmployeeAnnualLeaves existing = this.getOne(new LambdaQueryWrapper<EmployeeAnnualLeaves>()
                    .eq(EmployeeAnnualLeaves::getEmployeeId, item.getEmployeeId())
                    .eq(EmployeeAnnualLeaves::getYear, item.getYear()));

            if (existing != null) {
                item.setId(existing.getId()); // 把資料庫的 ID 塞回去，觸發 UPDATE
                // 這裡可以手動同步更新 remain_hours (總小時 - 已用小時)
                item.setRemainHours(item.getTotalHours().subtract(existing.getUsedHours()));
                this.updateById(item);
            } else {
                // 不存在則新增，初始剩餘小時 = 總小時
                item.setUsedHours(BigDecimal.ZERO);
                item.setRemainHours(item.getTotalHours());
                this.save(item);
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAnnualLeave(EmployeeAnnualLeaves annualLeave) {
        log.info("更新年度特休配額：員工ID={}, 年份={}, 已使用={}, 剩餘={}",
                annualLeave.getEmployeeId(), annualLeave.getYear(), annualLeave.getUsedHours(), annualLeave.getRemainHours());

        boolean updated = this.updateById(annualLeave);
        if (updated) {
            log.info("年度特休配額更新成功：員工ID={}, 年份={}", annualLeave.getEmployeeId(), annualLeave.getYear());
        } else {
            log.error("年度特休配額更新失敗：員工ID={}, 年份={}", annualLeave.getEmployeeId(), annualLeave.getYear());
        }

        return updated;
    }

    @Override
    public Integer countLowRemainDays(Integer year) {
        log.debug("查詢特休配額不足的員工數量：年份={}", year);

        return this.baseMapper.countLowRemainDays(year);
    }

    @Override
    public Integer countNoRemainDays(Integer year) {
        log.debug("查詢特休已用完的員工數量：年份={}", year);

        return this.baseMapper.countNoRemainDays(year);
    }

    @Override
    public boolean hasAnnualLeaveByEmployeeIdAndYear(Long employeeId, Integer year) {
        Integer count = this.baseMapper.existsByEmployeeIdAndYear(employeeId, year);
        return count != null && count > 0;
    }

    /**
     * 轉換為 DTO
     */
    private Page<EmployeeAnnualLeaveDTO> convertToDTO(Page<EmployeeAnnualLeaves> page) {
        Page<EmployeeAnnualLeaveDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<EmployeeAnnualLeaveDTO> dtoList = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    /**
     * 轉換單個對象為 DTO
     */
    private EmployeeAnnualLeaveDTO convertToDTO(EmployeeAnnualLeaves annualLeave) {
        EmployeeAnnualLeaveDTO dto = new EmployeeAnnualLeaveDTO();
        BigDecimal hoursPerDay = configService.getHoursPerDay();
        if (hoursPerDay == null) hoursPerDay = new BigDecimal("8");

        dto.setId(annualLeave.getId());
        dto.setEmployeeId(annualLeave.getEmployeeId());
        dto.setEmployeeName(annualLeave.getEmployeeName());
        dto.setYear(annualLeave.getYear());

        // 假設實體類中的欄位已改為 BigDecimal (推薦)
        dto.setTotalHours(annualLeave.getTotalHours());
        dto.setUsedHours(annualLeave.getUsedHours());
        dto.setRemainHours(annualLeave.getRemainHours());

        // 1. 計算使用百分比 (避免除以 0)
        if (annualLeave.getTotalHours() != null && annualLeave.getTotalHours().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = annualLeave.getUsedHours()
                    .multiply(new BigDecimal("100"))
                    .divide(annualLeave.getTotalHours(), 2, RoundingMode.HALF_UP);
            dto.setUsagePercentage(percentage);
        } else {
            dto.setUsagePercentage(BigDecimal.ZERO);
        }

        // 2. 核心：換算成「天 + 小時」顯示文字
        dto.setRemainDisplay(formatLeaveDisplay(annualLeave.getRemainHours(), hoursPerDay));

        return dto;
    }

    /**
     * 格式化邏輯：將總小時轉換為 "X 天 Y 小時"
     */
    private String formatLeaveDisplay(BigDecimal totalHours, BigDecimal hoursPerDay) {
        if (totalHours == null || totalHours.compareTo(BigDecimal.ZERO) <= 0) {
            return "0 小時";
        }

        // 計算整數天數：totalHours / hoursPerDay (取下限)
        BigDecimal days = totalHours.divide(hoursPerDay, 0, RoundingMode.FLOOR);

        // 計算剩餘小時：totalHours % hoursPerDay
        BigDecimal hours = totalHours.remainder(hoursPerDay).stripTrailingZeros();

        StringBuilder sb = new StringBuilder();
        if (days.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(days.toPlainString()).append(" 天 ");
        }
        // 如果有剩餘小時，或者總天數為 0，則顯示小時
        if (hours.compareTo(BigDecimal.ZERO) > 0 || days.compareTo(BigDecimal.ZERO) == 0) {
            sb.append(hours.toPlainString()).append(" 小時");
        }

        return sb.toString().trim();
    }
}