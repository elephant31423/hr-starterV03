package com.example.hrstarter.listener;

import com.example.hrstarter.service.Imp.EmployeeShiftServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleShiftChanged(EmployeeShiftServiceImpl.ShiftChangedEvent event) {
        // 封裝通知對象
        Map<String, String> msg = new HashMap<>();
        msg.put("title", "班表變動通知");
        msg.put("message", "日期 " + event.date() + " 的班表已被管理員更新。");
        String targetId = event.userId().toString();
        System.out.println("目標用戶ID: " + targetId);
        // 推送給所有訂閱了 /topic/notifications 的用戶
//        messagingTemplate.convertAndSend("/topic/notifications", msg);
        messagingTemplate.convertAndSendToUser(targetId , "/topic/notifications", msg);        messagingTemplate.convertAndSendToUser("HR"
                , "/topic/notifications", msg);

    }
}