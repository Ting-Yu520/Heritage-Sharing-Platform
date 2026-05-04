package com.heritage.platform.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.entity.AuditLog;
import com.heritage.platform.entity.HeritageResource;
import com.heritage.platform.mapper.AuditLogMapper;
import com.heritage.platform.mapper.HeritageResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AutoArchiveTask {

    @Autowired
    private HeritageResourceMapper resourceMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    /**
     * Scheduled task that runs daily to archive resources
     * that have been published but not updated for 30 days.
     */
    @Scheduled(fixedRate = 86400000)
    public void runAutoArchive() {
        System.out.println("🤖 [System Bot] Starting daily patrol...");

        // Check published resources not updated for 30 days
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);

        QueryWrapper<HeritageResource> query = new QueryWrapper<>();
        query.eq("status", 1)
                .le("updated_at", threshold);

        List<HeritageResource> outdatedResources = resourceMapper.selectList(query);

        if (outdatedResources.isEmpty()) {
            System.out.println("🤖 [System Bot] Patrol complete. No outdated resources found.");
            return;
        }

        for (HeritageResource resource : outdatedResources) {
            // Archive the resource
            resource.setStatus(3);
            resourceMapper.updateById(resource);

            // Record audit log
            AuditLog log = new AuditLog();
            log.setUserId("System (Auto-Archive Policy)");
            log.setActionType("AUTO_ARCHIVE");
            log.setResourceId(resource.getId());
            log.setChangesSummary("System rule: Archived resource inactive for > 30 days.");
            log.setCreatedAt(LocalDateTime.now());
            auditLogMapper.insert(log);

            System.out.println("🤖 [System Bot] Auto-archived outdated resource ID: " + resource.getId());
        }
    }
}