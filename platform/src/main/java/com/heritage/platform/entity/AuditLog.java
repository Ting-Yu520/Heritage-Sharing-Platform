package com.heritage.platform.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId = "Admin_01"; // 模拟当前登录的管理员
    private String actionType;
    private Long resourceId;
    private String changesSummary;
    private LocalDateTime createdAt;

    // Getters and Setters (如果你没装Lombok，请手动生成或复制下面的)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public String getChangesSummary() { return changesSummary; }
    public void setChangesSummary(String changesSummary) { this.changesSummary = changesSummary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}