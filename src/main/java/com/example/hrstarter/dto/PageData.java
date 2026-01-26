package com.example.hrstarter.dto;

import com.example.hrstarter.entity.AuditLogEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 分頁響應數據
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {
    /**
     * 數據項列表
     */
    private List<T> items;

    /**
     * 總記錄數
     */
    private Long total;

    /**
     * 當前頁碼（從 1 開始）
     */
    private Integer pageNumber;

    /**
     * 每頁記錄數
     */
    private Integer pageSize;

    /**
     * 總頁數
     */
    private Integer totalPages;


    /**
     * 是否有下一頁
     */
    public boolean hasNextPage() {
        return pageNumber < totalPages;
    }

    /**
     * 是否有上一頁
     */
    public boolean hasPreviousPage() {
        return pageNumber > 1;
    }

    public PageData(List<T> items, long total, int pageNumber, int pageSize) {
        // 防呆：確保 items 不會是 null，避免前端 items.content 報錯
        this.items = (items != null) ? items : new ArrayList<>();
        this.total = total;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;

        // 自動計算總頁數
        if (pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        } else {
            this.totalPages = 0;
        }
    }
}