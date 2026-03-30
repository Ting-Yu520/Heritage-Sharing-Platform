package com.heritage.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String role;
    private String email;
    private String nickname;
    private String avatar;

    // ✨ PBI 5 新增：通知偏好设置
    private Integer notifyReview;
    private Integer notifyComment;
    private Integer notifySystem;

    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getNotifyReview() { return notifyReview; }
    public void setNotifyReview(Integer notifyReview) { this.notifyReview = notifyReview; }
    public Integer getNotifyComment() { return notifyComment; }
    public void setNotifyComment(Integer notifyComment) { this.notifyComment = notifyComment; }
    public Integer getNotifySystem() { return notifySystem; }
    public void setNotifySystem(Integer notifySystem) { this.notifySystem = notifySystem; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}