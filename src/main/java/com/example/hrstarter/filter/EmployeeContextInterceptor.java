package com.example.hrstarter.filter;

import com.example.hrstarter.util.EmployeeContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class EmployeeContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 從路徑或 session 取得目前選取的員工
        String empId = request.getHeader("X-Employee-Context");

        if (empId != null) {
            EmployeeContext.set(Long.valueOf(empId));
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        EmployeeContext.clear();
    }
}
