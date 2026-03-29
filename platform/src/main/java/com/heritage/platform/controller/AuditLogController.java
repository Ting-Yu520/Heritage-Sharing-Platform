package com.heritage.platform.controller;

import com.heritage.platform.entity.AuditLog;
import com.heritage.platform.mapper.AuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@CrossOrigin
@RestController
public class AuditLogController {

    @Autowired
    private AuditLogMapper auditLogMapper;

    // 获取所有审计日志
    @GetMapping("/api/audit-logs")
    public List<AuditLog> getAllLogs() {
        return auditLogMapper.selectList(null);
    }
}