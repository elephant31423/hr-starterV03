package com.example.hrstarter.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hrstarter.entity.AnnualLeaveRule;
import com.example.hrstarter.mapper.AnnualLeaveRuleMapper;
import com.example.hrstarter.service.LeaveCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class LeaveCalculationServiceImpl implements LeaveCalculationService {
    @Autowired
    private AnnualLeaveRuleMapper ruleMapper;

    public BigDecimal calculateDaysBySeniority(BigDecimal seniorityYears) {

        LambdaQueryWrapper<AnnualLeaveRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(AnnualLeaveRule::getMinYears, seniorityYears)
                .gt(AnnualLeaveRule::getMaxYears, seniorityYears)
                .last("LIMIT 1"); // 確保只取一筆

        AnnualLeaveRule rule = ruleMapper.selectOne(queryWrapper);
        return rule != null ? rule.getHours() : BigDecimal.ZERO;
    }


}
