package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.entity.AuditLog;
import com.heritage.platform.entity.HeritageResource;
import com.heritage.platform.entity.Notification;
import com.heritage.platform.mapper.AuditLogMapper;
import com.heritage.platform.mapper.HeritageResourceMapper;
import com.heritage.platform.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class ResourceController {

    @Autowired
    private HeritageResourceMapper resourceMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    // 1. 获取所有资源 (后台管理用)
    @GetMapping("/api/resources")
    public List<HeritageResource> getResources() {
        return resourceMapper.selectList(null);
    }

    // 2. 新增资源
    @PostMapping("/api/resources")
    public String addResource(@RequestBody HeritageResource resource) {
        resource.setStatus(0); // 默认待审核
        resource.setUpdatedAt(LocalDateTime.now());
        resourceMapper.insert(resource);
        logAction("CREATE", resource.getId(), "Created new resource: " + resource.getTitle());
        return "新增成功，请等待审核！";
    }

    // 3. 更新资源状态 (审核/发布/归档/恢复)
    @PutMapping("/api/resources/{id}/status")
    public String updateResourceStatus(@PathVariable Long id, @RequestParam Integer status) {
        HeritageResource resource = resourceMapper.selectById(id);
        if (resource != null) {
            resource.setStatus(status);
            resource.setUpdatedAt(LocalDateTime.now());
            resourceMapper.updateById(resource);

            // 发送系统通知给贡献者 (固定发给 guest01)
            Notification note = new Notification();
            note.setReceiverUsername("guest01");
            String resultText = (status == 1) ? "【已通过】" : (status == 2 ? "【已驳回】" : "【已归档】");
            note.setContent("您的遗产资源《" + resource.getTitle() + "》审核状态更新为：" + resultText);
            note.setCreatedAt(LocalDateTime.now());
            notificationMapper.insert(note);

            // 记录日志
            String action = (status == 3) ? "ARCHIVE" : (status == 1 ? "APPROVE/RESTORE" : "UPDATE");
            logAction(action, id, "Status updated to " + status);
            return "操作成功！";
        }
        return "未找到该资源！";
    }

    // 4. 删除资源
    @DeleteMapping("/api/resources/{id}")
    public String deleteResource(@PathVariable Long id) {
        resourceMapper.deleteById(id);
        logAction("DELETE", id, "Resource deleted permanently.");
        return "删除成功！";
    }

    // 5. [公开大厅] 获取所有已发布的资源 (供游客浏览)
    @GetMapping("/api/public/resources")
    public List<HeritageResource> getPublicResources() {
        QueryWrapper<HeritageResource> query = new QueryWrapper<>();
        query.eq("status", 1);
        return resourceMapper.selectList(query);
    }

    // 6. [统计信息] 为控制台大厅提供汇总数据
    @GetMapping("/api/stats/summary")
    public Map<String, Integer> getSummary() {
        Map<String, Integer> summary = new HashMap<>();

        // 统计各项数据
        summary.put("total", resourceMapper.selectCount(null).intValue());
        summary.put("pending", resourceMapper.selectCount(new QueryWrapper<HeritageResource>().eq("status", 0)).intValue());
        summary.put("published", resourceMapper.selectCount(new QueryWrapper<HeritageResource>().eq("status", 1)).intValue());

        // 统计日志数
        summary.put("logs", auditLogMapper.selectCount(null).intValue());

        return summary;
    }

    // 辅助方法：记录审计日志
    private void logAction(String type, Long resId, String summary) {
        AuditLog log = new AuditLog();
        log.setUserId("Admin_01");
        log.setActionType(type);
        log.setResourceId(resId);
        log.setChangesSummary(summary);
        log.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(log);
    }
}