package com.example.hrstarter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int count() default 10;     // 允許的請求次數
    int time() default 60;      // 單位時間 (秒)
    String message() default "伺服器繁忙，請稍後再試"; // 拒絕時的提示

}
