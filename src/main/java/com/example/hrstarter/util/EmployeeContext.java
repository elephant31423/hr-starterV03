package com.example.hrstarter.util;

public class EmployeeContext {
    private static final ThreadLocal<Long> CURRENT_EMPLOYEE = new ThreadLocal<>();

    public static void set(Long employeeId) {
        CURRENT_EMPLOYEE.set(employeeId);
    }

    public static Long get() {
        return CURRENT_EMPLOYEE.get();
    }

    public static void clear() {
        CURRENT_EMPLOYEE.remove();
    }
}
