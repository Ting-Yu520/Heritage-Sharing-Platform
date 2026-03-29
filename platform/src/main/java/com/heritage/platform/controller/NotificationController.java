package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.entity.Notification;
import com.heritage.platform.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;

    // 获取当前登录用户的所有通知
    @GetMapping("/api/notifications")
    public List<Notification> getMyNotifications(@RequestParam String username) {
        QueryWrapper<Notification> query = new QueryWrapper<>();
        query.eq("receiver_username", username).orderByDesc("created_at");
        return notificationMapper.selectList(query);
    }

    // 标记通知为已读
    @PutMapping("/api/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        Notification n = notificationMapper.selectById(id);
        if (n != null) {
            n.setIsRead(1);
            notificationMapper.updateById(n);
        }
        return "success";
    }
}