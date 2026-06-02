package com.example.hrstarter.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;


@Data
public class PageResult<T> {
    private List<T> list;      // 改回 list，前端最愛
    private long total;         // 總筆數
    private long pageNum;       // 當前頁碼
    private long pageSize;      // 每頁筆數
    private long totalPage;     // 總頁數

    /**
     * 將 MyBatis-Plus 的 IPage 轉換為通用的 PageResult
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setList(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotalPage(page.getPages());
        return result;
    }
}

