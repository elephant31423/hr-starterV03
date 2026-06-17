package com.example.hrstarter.service.Imp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hrstarter.LeaveCalculation.LeaveCalculationStrategy;
import com.example.hrstarter.dto.employee.EmployeeDTO;
import com.example.hrstarter.dto.employee.EmployeeQueryDTO;
import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.entity.EmployeeAnnualLeaves;
import com.example.hrstarter.entity.User;
import com.example.hrstarter.exception.BusinessException;
import com.example.hrstarter.mapper.EmployeeAnnualLeaveMapper;
import com.example.hrstarter.mapper.EmployeeMapper;
import com.example.hrstarter.mapper.UserMapper;
import com.example.hrstarter.mapper.UserRoleMapper;
import com.example.hrstarter.config.ConfigService;
import com.example.hrstarter.service.EmployeeService;
import com.example.hrstarter.service.LeaveCalculationService;
import com.example.hrstarter.util.PageUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final EmployeeAnnualLeaveMapper annualLeaveMapper;
    private final LeaveCalculationService leaveCalculationService;
    private final Map<String, LeaveCalculationStrategy> strategyMap;
    private final ConfigService configService;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;


    @PreAuthorize("hasAnyAuthority('employee:view')")
    @Override
    public List<EmployeeDTO> findAll() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current Authorities = {}", auth.getAuthorities());
        return employeeMapper.findAll();
    }

    @PreAuthorize("hasAuthority('employee:view')")
    @Override
    public List<EmployeeDTO> findUnbound() {
        return employeeMapper.findUnbound();
    }

    @PreAuthorize("hasAuthority('employee:view')")
    @Override
    public Employee findById(Long id) {
        return employeeMapper.findById(id);
    }

    @PreAuthorize("hasAuthority('employee:create')")
    @Override
    @Transactional
    public void insert(Employee employee) {
        if (employee.getHireDate() == null) {
            throw BusinessException.badRequest("Hire date is required");
        }

        if (Boolean.TRUE.equals(employee.getCreateAccount())) {
            validateAccountRequest(employee);
            if (userMapper.findByUsername(employee.getUsername()) != null) {
                throw BusinessException.conflict("登入帳號已存在");
            }
        }

        employeeMapper.insert(employee);

        if (Boolean.TRUE.equals(employee.getCreateAccount())) {
            User user = new User();
            user.setUsername(employee.getUsername());
            user.setPassword(passwordEncoder.encode(employee.getPassword()));
            user.setFullName(employee.getName());
            user.setEnabled(employee.getEnabled() == null ? true : employee.getEnabled());
            user.setEmployeeId(employee.getId());
            userMapper.insert(user);

            if (!CollectionUtils.isEmpty(employee.getRoleIds())) {
                userRoleMapper.batchInsertUserRoles(user.getId(), employee.getRoleIds());
            }

            int bound = employeeMapper.bindUser(employee.getId(), user.getId());
            if (bound == 0) {
                throw BusinessException.conflict("員工已綁定其他帳號");
            }
            employee.setUserId(user.getId());
        }

        int currentYear = LocalDate.now().getYear();
        String systemType = configService.getValueByKey("LEAVE_SYSTEM_TYPE");
        LeaveCalculationStrategy strategy = strategyMap.get(systemType);
        if (strategy != null) {
            // 使用剛才寫好的策略計算應得天數
            log.info("使用特休計算策略：{}", strategy.getClass().getSimpleName());
            if (employee.getHireDate() == null) {
                throw new IllegalArgumentException("入職日期不能為空");
            }
            BigDecimal totalDays = strategy.calculateDays(LocalDate.from(employee.getHireDate()), currentYear);

            EmployeeAnnualLeaves annualLeave = new EmployeeAnnualLeaves();
            annualLeave.setEmployeeId(employee.getId());
            annualLeave.setYear(currentYear);
            annualLeave.setTotalHours(BigDecimal.valueOf(totalDays.intValue())); // 轉為整數存入
            annualLeave.setUsedHours(BigDecimal.ZERO);
            annualLeave.setRemainHours(BigDecimal.valueOf(totalDays.intValue()));

            // 同步寫入特休表
            annualLeaveMapper.insert(annualLeave);

            log.info("員工 {} 入職成功，已根據 {} 初始化 {} 年度特休：{} 天",
                    employee.getName(), systemType, currentYear, totalDays);
        }
    }

    @PreAuthorize("hasAuthority('employee:update')")
    @Override
    public void update(Employee e) {
        employeeMapper.update(e);
    }

    @PreAuthorize("hasAuthority('employee:delete')")
    @Override
    public void delete(Long id) {
        employeeMapper.delete(id);
    }

    @PreAuthorize("hasAuthority('employee:view')")
    @Override
    public List<Employee> findByDepartmentId(Long departmentId) {
        return employeeMapper.findByDepartmentId(departmentId);
    }

    @PreAuthorize("hasAuthority('employee:view')")
    @Override
    public IPage<EmployeeDTO> getEmployeePage(EmployeeQueryDTO queryDTO) {

        // 1. 設定分頁（這行必須在 Mapper 查詢之前，PageHelper 會攔截 SQL 加上 Limit）

        IPage<Employee> employeePage = employeeMapper.selectPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), null);

        // 2. 執行查詢
        // 此時 MP 會執行兩條 SQL：1. SELECT COUNT 算總數； 2. SELECT ... LIMIT 算分頁
        // 查詢結果會自動封裝進 page.getRecords() 中
        employeeMapper.findByParams(employeePage, queryDTO);
        // 4. 使用 PageInfo 包裝結果並返回
        return PageUtils.convertPage(employeePage, EmployeeDTO.class);

    }

    @Override
    public void softDelete(Long id) {

    }

    @Override
    public void restore(Long id) {

    }

    @Transactional
    public void initializeEmployeeLeave(Employee employee) {
        // 1. 計算年資 (新入職可能是 0)
        BigDecimal seniority = calculateSeniority(employee.getHireDate().atStartOfDay());

        // 2. 從規則表查出天數
        BigDecimal initialDays = leaveCalculationService.calculateDaysBySeniority(seniority);

        // 3. 寫入 employee_annual_leaves
        EmployeeAnnualLeaves eal = new EmployeeAnnualLeaves();
        eal.setEmployeeId(employee.getId());
        eal.setYear(LocalDate.now().getYear());
        eal.setTotalHours(initialDays);
        eal.setRemainHours(initialDays); // 初始時剩餘天數 = 總天數
        eal.setUsedHours(BigDecimal.ZERO);

        annualLeaveMapper.insert(eal);
    }

    private BigDecimal calculateSeniority(LocalDateTime hireDate) {
        return null;
    }

    private void validateAccountRequest(Employee employee) {
        if (!StringUtils.hasText(employee.getUsername())) {
            throw BusinessException.badRequest("請輸入登入帳號");
        }
        if (!StringUtils.hasText(employee.getPassword())) {
            throw BusinessException.badRequest("請輸入初始密碼");
        }
    }


}
