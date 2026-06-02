package com.example.hrstarter.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class testhrdb {


    private static final String DB_URL = "jdbc:mysql://localhost:3306/hrdb?serverTimezone=UTC&useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "root"; // 請根據您的實際密碼修改

    public static void main(String[] args) {
        System.out.println("Testing connection to MySQL...");

        try {
            // 載入驅動
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! 請確認您的 classpath 中包含了 mysql-connector-j.jar");
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            if (conn != null) {
                System.out.println("Connection successful! 資料庫連線正常。");
            } else {
                System.out.println("Connection failed: Connection object is null.");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed! 請檢查您的資料庫狀態、URL、用戶名和密碼。");
            System.err.println("錯誤訊息: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }

}

