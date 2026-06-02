package com.example.hrstarter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hrstarter.entity.AnnualLeaveRule;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface AnnualLeaveRuleMapper {


    AnnualLeaveRule findRule( @Param("years")int years);

    void updateAnnualLeaveRule(AnnualLeaveRule annualLeaveRule);

    AnnualLeaveRule selectOne(LambdaQueryWrapper<AnnualLeaveRule> queryWrapper);

    int getDaysBySeniority(BigDecimal seniority);
}
