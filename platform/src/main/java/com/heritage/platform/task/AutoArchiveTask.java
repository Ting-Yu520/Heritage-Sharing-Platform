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

    // Run daily patrol [cite: 23]
    @Scheduled(fixedRate = 86400000)
    public void runAutoArchive() {
        System.out.println("🤖 [System Bot] Starting daily patrol...");

        // PBI 5 logic: check published resources not updated for 1 day [cite: 23]
        LocalDateTime ruleTime = LocalDateTime.now().minusDays(1);

        QueryWrapper<HeritageResource> query = new QueryWrapper<>();
        query.eq("status", 1)
                .le("updated_at", ruleTime);

        List<HeritageResource> outdatedResources = resourceMapper.selectList(query);

        if (outdatedResources.isEmpty()) {
            System.out.println("🤖 [System Bot] Patrol complete. No outdated resources found.");
            return;
        }

        for (HeritageResource resource : outdatedResources) {
            // Set status to archived [cite: 23]
            resource.setStatus(3);
            resourceMapper.updateById(resource);

            // Record audit log executed by the system automatically [cite: 23]
            AuditLog log = new AuditLog();
            log.setUserId("System (Auto-Archive Policy)");
            log.setActionType("AUTO_ARCHIVE");
            log.setResourceId(resource.getId());
            log.setChangesSummary("System rule: Archived resource inactive for > 1 day.");
            log.setCreatedAt(LocalDateTime.now());
            auditLogMapper.insert(log);

            System.out.println("🤖 [System Bot] Auto-archived outdated resource ID: " + resource.getId());
        }
    } // 👈 Ensure the method ends here
} // 👈 Ensure the class ends here
