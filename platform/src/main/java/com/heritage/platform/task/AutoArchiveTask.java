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

    // 每天执行一次巡逻 [cite: 23]
    @Scheduled(fixedRate = 86400000)
    public void runAutoArchive() {
        System.out.println("🤖 [系统机器人] 开始每日例行巡逻...");

        // PBI 5 逻辑：检查 1 天前未更新的已发布资源 [cite: 23]
        LocalDateTime ruleTime = LocalDateTime.now().minusDays(1);

        QueryWrapper<HeritageResource> query = new QueryWrapper<>();
        query.eq("status", 1)
                .le("updated_at", ruleTime);

        List<HeritageResource> outdatedResources = resourceMapper.selectList(query);

        if (outdatedResources.isEmpty()) {
            System.out.println("🤖 [系统机器人] 巡逻完毕，没有发现过期资源。");
            return;
        }

        for (HeritageResource resource : outdatedResources) {
            // 将状态改为归档 [cite: 23]
            resource.setStatus(3);
            resourceMapper.updateById(resource);

            // 记录由系统自动执行的审计日志 [cite: 23]
            AuditLog log = new AuditLog();
            log.setUserId("System (Auto-Archive Policy)");
            log.setActionType("AUTO_ARCHIVE");
            log.setResourceId(resource.getId());
            log.setChangesSummary("System rule: Archived resource inactive for > 1 day.");
            log.setCreatedAt(LocalDateTime.now());
            auditLogMapper.insert(log);

            System.out.println("🤖 [系统机器人] 已自动归档超期资源 ID: " + resource.getId());
        }
    } // 👈 确保方法在这里结束
} // 👈 确保类在这里结束