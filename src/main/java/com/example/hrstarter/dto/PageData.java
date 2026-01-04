package com.example.hrstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
}