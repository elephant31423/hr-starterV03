package com.example.hrstarter.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分頁轉換工具類 - MyBatis-Plus 版
 */
public class PageUtils {

    /**
     * 將 IPage (Entity) 轉換為 IPage (DTO)
     *
     * @param sourcePage  原始分頁物件 (Mapper 查出來的 Page<Entity>)
     * @param targetClazz 目標 DTO 的 Class
     * @return 轉換後的分頁物件 Page<DTO>
     */
    public static <S, T> IPage<T> convertPage(IPage<S> sourcePage, Class<T> targetClazz) {
        // 1. 轉換內部的 List 資料
        List<T> targetList = sourcePage.getRecords().stream().map(source -> {
            try {
                T target = targetClazz.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, target);
                return target;
            } catch (Exception e) {
                throw new RuntimeException("物件轉換失敗: " + targetClazz.getName(), e);
            }
        }).collect(Collectors.toList());

        // 2. 封裝並回填分頁元數據
        return copyMetadata(sourcePage, targetList);
    }

    /**
     * 進階版：支援自定義轉換邏輯
     */
    public static <S, T> IPage<T> convertPage(IPage<S> sourcePage, Function<S, T> mapper) {
        List<T> targetList = sourcePage.getRecords().stream().map(mapper).collect(Collectors.toList());
        return copyMetadata(sourcePage, targetList);
    }

    /**
     * 私有輔助方法：複製分頁屬性（如 total, current, size 等）
     */
    private static <S, T> IPage<T> copyMetadata(IPage<S> source, List<T> targetList) {
        Page<T> targetPage = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        targetPage.setRecords(targetList);
        targetPage.setPages(source.getPages());
        return targetPage;
    }
}