package com.example.hrstarter.mapper;

import com.example.hrstarter.entity.AnnualLeaveRule;
import org.apache.ibatis.annotations.Param;

public interface AnnualLeaveRuleMapper {


    AnnualLeaveRule findRule( @Param("years")int years);

    void updateAnnualLeaveRule(AnnualLeaveRule annualLeaveRule);

}
