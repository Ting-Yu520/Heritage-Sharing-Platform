package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.heritage.platform.entity.Notification;
import com.heritage.platform.entity.User;
import com.heritage.platform.mapper.NotificationMapper;
import com.heritage.platform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class NotificationController {

    @Autowired private NotificationMapper notificationMapper;
    @Autowired private UserMapper userMapper;

    /**
     * PBI 3: 获取聚合通知列表
     */
    @GetMapping("/api/notifications")
    public List<Notification> getNotifications(@RequestParam String username) {
        return notificationMapper.selectList(new QueryWrapper<Notification>()
                .eq("receiver_username", username)
                .orderByDesc("created_at"));
    }

    /**
     * PBI 4: 单条标记已读
     */
    @PutMapping("/api/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        Notification n = notificationMapper.selectById(id);
        if (n != null) {
            n.setIsRead(1);
            notificationMapper.updateById(n);
        }
        return "success";
    }

    /**
     * ✨ PBI 4: 一键全部标为已读
     */
    @PutMapping("/api/notifications/mark-all-read")
    public String markAllAsRead(@RequestParam String username) {
        UpdateWrapper<Notification> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("receiver_username", username).eq("is_read", 0).set("is_read", 1);
        notificationMapper.update(null, updateWrapper);
        return "success";
    }

    /**
     * ✨ PBI 5: 更新通知偏好设置
     */
    @PutMapping("/api/users/preferences")
    public Map<String, Object> updatePreferences(@RequestParam String username, @RequestBody User prefs) {
        Map<String, Object> res = new HashMap<>();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user != null) {
            user.setNotifyReview(prefs.getNotifyReview());
            user.setNotifyComment(prefs.getNotifyComment());
            user.setNotifySystem(prefs.getNotifySystem());
            userMapper.updateById(user);

            res.put("success", true);
            res.put("message", "通知偏好设置已保存！");
        } else {
            res.put("success", false);
        }
        return res;
    }
}