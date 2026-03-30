package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.entity.AuditLog;
import com.heritage.platform.entity.Notification;
import com.heritage.platform.entity.RoleApplication;
import com.heritage.platform.entity.User;
import com.heritage.platform.mapper.AuditLogMapper;
import com.heritage.platform.mapper.NotificationMapper;
import com.heritage.platform.mapper.RoleApplicationMapper;
import com.heritage.platform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin
@RestController
public class UserController {

    @Autowired private UserMapper userMapper;
    @Autowired private RoleApplicationMapper roleAppMapper; // ✨ 新增：角色申请 Mapper
    @Autowired private NotificationMapper notificationMapper; // ✨ 新增：用于发送审批结果通知
    @Autowired private AuditLogMapper auditLogMapper;         // ✨ 新增：用于记录权限变更日志

    // 临时存储重置密码的验证码
    private Map<String, String> verifyCodeMap = new ConcurrentHashMap<>();

    // ==========================================================
    // 🔐 认证基础接口 (Sprint 2 保留并升级)
    // ==========================================================

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Map<String, Object> res = new HashMap<>();
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", loginUser.getUsername()).or().eq("email", loginUser.getUsername());
        User user = userMapper.selectOne(query);

        if (user == null) {
            res.put("success", false); res.put("message", "账号或邮箱不存在！"); return res;
        }

        String encryptedInput = DigestUtils.md5DigestAsHex(loginUser.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedInput) && !user.getPassword().equals(loginUser.getPassword())) {
            res.put("success", false); res.put("message", "密码错误！"); return res;
        }

        res.put("success", true);
        res.put("username", user.getUsername());
        res.put("role", user.getRole());
        return res;
    }

    @PostMapping("/api/users/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", user.getUsername()).or().eq("email", user.getEmail());
        if (userMapper.selectCount(query) > 0) {
            res.put("success", false); res.put("message", "对不起，该用户名或邮箱已被注册！"); return res;
        }

        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        // ✨ PBI 1 核心修复：新用户注册默认赋予 "VIEWER" (浏览者) 角色，而不是 CONTRIBUTOR
        user.setRole("VIEWER");

        userMapper.insert(user);
        res.put("success", true); res.put("message", "注册成功，请使用新账号登录！");
        return res;
    }

    @PostMapping("/api/users/forgot-password")
    public Map<String, Object> forgotPassword(@RequestParam String email) {
        Map<String, Object> res = new HashMap<>();
        if (userMapper.selectCount(new QueryWrapper<User>().eq("email", email)) == 0) {
            res.put("success", false); res.put("message", "该邮箱未注册！"); return res;
        }
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        verifyCodeMap.put(email, code);
        System.out.println("\n💌 【系统模拟邮件发送】 收件人：" + email + " 验证码：【 " + code + " 】\n");
        res.put("success", true); res.put("message", "验证码已发送到您的邮箱，请查收！");
        return res;
    }

    @PostMapping("/api/users/reset-password")
    public Map<String, Object> resetPassword(@RequestParam String email, @RequestParam String code, @RequestParam String newPassword) {
        Map<String, Object> res = new HashMap<>();
        if (!code.equals(verifyCodeMap.get(email))) {
            res.put("success", false); res.put("message", "验证码错误或已过期！"); return res;
        }
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("email", email));
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        userMapper.updateById(user);
        verifyCodeMap.remove(email);
        res.put("success", true); res.put("message", "密码重置成功，请重新登录！");
        return res;
    }

    // ==========================================================
    // 🧑‍💻 个人中心与角色申请 (Sprint 4)
    // ==========================================================

    @GetMapping("/api/users/profile")
    public User getProfile(@RequestParam String username) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if(user != null) user.setPassword(null);
        return user;
    }

    @PutMapping("/api/users/profile")
    public Map<String, Object> updateProfile(@RequestBody User updatedUser) {
        Map<String, Object> res = new HashMap<>();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", updatedUser.getUsername()));
        if (user != null) {
            user.setNickname(updatedUser.getNickname());
            user.setAvatar(updatedUser.getAvatar());
            userMapper.updateById(user);
            res.put("success", true); res.put("message", "个人资料更新成功！");
        }
        return res;
    }

    /**
     * ✨ PBI 3: 浏览者 (VIEWER) 提交成为贡献者 (CONTRIBUTOR) 的申请
     */
    @PostMapping("/api/users/apply-role")
    public Map<String, Object> applyForContributor(@RequestBody RoleApplication application) {
        Map<String, Object> res = new HashMap<>();
        // 防重复提交：检查是否已有待审核的申请
        long pendingCount = roleAppMapper.selectCount(new QueryWrapper<RoleApplication>()
                .eq("username", application.getUsername()).eq("status", 0));
        if (pendingCount > 0) {
            res.put("success", false); res.put("message", "您已有一份待审核的申请，请勿重复提交！");
            return res;
        }

        application.setStatus(0); // 0 表示待审核 (Pending)
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        roleAppMapper.insert(application);

        res.put("success", true); res.put("message", "申请提交成功，请等待管理员审批！");
        return res;
    }

    // ==========================================================
    // 🛡️ 后台用户与权限管理大盘 (Sprint 4)
    // ==========================================================

    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    /**
     * ✨ PBI 5: 管理员手动修改用户角色
     */
    @PutMapping("/api/users/{id}/role")
    public String updateRole(@PathVariable Long id, @RequestParam String role) {
        User user = userMapper.selectById(id);
        if (user != null) {
            String oldRole = user.getRole();
            user.setRole(role);
            userMapper.updateById(user);

            // 记录审计日志
            logAction("ROLE_CHANGE", id, "Admin manually changed role of " + user.getUsername() + " from " + oldRole + " to " + role);
            return "权限更新成功";
        }
        return "未找到用户";
    }

    @DeleteMapping("/api/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userMapper.deleteById(id);
        return "账号已注销";
    }

    /**
     * ✨ PBI 4: 管理员获取待审批的晋升申请列表
     */
    @GetMapping("/api/admin/role-applications")
    public List<RoleApplication> getPendingApplications() {
        // 只查询状态为 0 (待审核) 的申请
        return roleAppMapper.selectList(new QueryWrapper<RoleApplication>().eq("status", 0).orderByAsc("created_at"));
    }

    /**
     * ✨ PBI 4: 管理员审批申请 (通过/驳回)
     */
    @PutMapping("/api/admin/role-applications/{id}")
    public Map<String, Object> processApplication(@PathVariable Long id, @RequestParam Integer status) {
        Map<String, Object> res = new HashMap<>();
        RoleApplication app = roleAppMapper.selectById(id);

        if (app == null) {
            res.put("success", false); res.put("message", "申请记录不存在！"); return res;
        }

        app.setStatus(status);
        app.setUpdatedAt(LocalDateTime.now());
        roleAppMapper.updateById(app);

        User targetUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", app.getUsername()));

        // 如果审核通过 (status == 1)，则自动升级角色为 CONTRIBUTOR
        if (status == 1 && targetUser != null) {
            targetUser.setRole("CONTRIBUTOR");
            userMapper.updateById(targetUser);
            logAction("ROLE_APPROVE", targetUser.getId(), "Approved contributor application for " + app.getUsername());
        }

        // 无论通过还是驳回，发送系统内通知给该用户
        if (targetUser != null) {
            Notification note = new Notification();
            note.setReceiverUsername(app.getUsername());
            String resultText = (status == 1) ? "【已通过】恭喜您已成为贡献者，现在可以发布资源了！" : "【被驳回】抱歉，您的贡献者申请未通过。";
            note.setContent("您的角色晋升申请审批结果：" + resultText);
            note.setCreatedAt(LocalDateTime.now());
            notificationMapper.insert(note);
        }

        res.put("success", true); res.put("message", "审批处理完成！");
        return res;
    }

    // 辅助方法：记录审计日志
    private void logAction(String type, Long targetId, String summary) {
        AuditLog log = new AuditLog();
        log.setUserId("Admin_01"); // 真实场景应获取当前登录的管理员
        log.setActionType(type);
        log.setResourceId(targetId);
        log.setChangesSummary(summary);
        log.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(log);
    }
}