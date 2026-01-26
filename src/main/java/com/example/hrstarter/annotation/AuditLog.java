package com.example.hrstarter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuditLog {

    String action();      // CREATE, UPDATE, DELETE
    String entityType();  // ROLE, USER, EMPLOYEE
    String idParam() default "id"; // 從方法參數讀取實體 ID


    String performedBy() default "system"; // 執行操作的用戶

}
