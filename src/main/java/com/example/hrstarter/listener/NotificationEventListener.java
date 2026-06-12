package com.example.hrstarter.listener;

import com.example.hrstarter.service.Imp.EmployeeShiftServiceImpl;
import com.example.hrstarter.service.NotificationService;
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
    private final NotificationService notificationService;

    @EventListener
    public void handleShiftChanged(EmployeeShiftServiceImpl.ShiftChangedEvent event) {
        String title = "班表已更新";
        String message = "您的 " + event.date() + " 班表已更新，請查看最新排班。";

        notificationService.notifyEmployee(event.employeeId(), "SHIFT_UPDATED", title, message);

        Map<String, String> msg = new HashMap<>();
        msg.put("title", title);
        msg.put("message", message);
        messagingTemplate.convertAndSendToUser(
                event.employeeId().toString(),
                "/topic/notifications",
                msg
        );
    }
}
