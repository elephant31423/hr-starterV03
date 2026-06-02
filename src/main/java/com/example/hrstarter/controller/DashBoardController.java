package com.example.hrstarter.controller;

import com.example.hrstarter.dto.ApiResponse;
import com.example.hrstarter.dto.DashboardStatsDTO;
import com.example.hrstarter.dto.PermissionTreeDTO;
import com.example.hrstarter.entity.Employee;
import com.example.hrstarter.service.DashBoardService;
import com.example.hrstarter.service.Imp.DashBoardServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/api/dashboard")
public class DashBoardController {


    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    DashBoardService dashBoardService;

    @RequestMapping("/status")
    @GetMapping
    public ResponseEntity<?> list() {

        DashboardStatsDTO stats = dashBoardService.getDashboardStats();
        log.info("從資料庫獲取的 Dashboard 統計數據: {}", stats);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(stats));
    }

    @GetMapping("/test-msg")
    public String testWebsocket(@RequestParam String targetId) {

        Map<String, String> msg = new HashMap<>();
        msg.put("title", "絕對路徑測試");
        msg.put("message", "測試中...");

        // 💡 暫時手動拼湊路徑，跳過 UserDestination 轉發邏輯
        messagingTemplate.convertAndSend("/user/" + targetId + "/topic/notifications", msg);

        return "已發送絕對路徑至 " + targetId;
    }
    @GetMapping("/test-broadcast")
    public String testBroadcast() {
        try {
            Map<String, String> msg = new HashMap<>();
            msg.put("title", "📢 系統廣播");
            msg.put("message", "這是一條發給所有人的測試訊息");

            log.info("正在發送全域廣播訊息...");

            messagingTemplate.convertAndSend("/topic/notifications", msg);

            return "全域廣播已發送";
        } catch (Exception e) {
            log.error("發送失敗: ", e);
            return "錯誤: " + e.getMessage();
        }
    }
}
