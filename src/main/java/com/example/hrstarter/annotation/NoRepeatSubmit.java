package com.example.hrstarter.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoRepeatSubmit {
    /**
     * 鎖定時間（單位：秒），預設 3 秒內不允許重複提交
     */
    int lockTime() default 3;

    /**
     * 提示訊息
     */
    String message() default "請勿重複提交請求";

}
