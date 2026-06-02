package com.example.hrstarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class EmployeeAnnualLeaves implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主鍵 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 員工 ID
     */
    private Long employeeId;

    /**
     * 年份
     */
    private Integer year;


    private BigDecimal totalHours;
    private BigDecimal usedHours;
    private BigDecimal remainHours;
    /**
     * 員工名稱（關聯查詢用）
     */
    @TableField(exist = false)
    private String employeeName;

    /**
     * 使用百分比（關聯查詢用）
     */

    private BigDecimal usagePercentage;

    /**
     * 計算使用百分比
     */
    public BigDecimal calculateUsagePercentage() {

        if (totalHours == null || totalHours .compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return   usedHours.divide(usedHours, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
    }
}
