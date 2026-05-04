package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.config.RoleCheck;
import com.heritage.platform.config.UserContext;
import com.heritage.platform.entity.AuditLog;
import com.heritage.platform.entity.Notification;
import com.heritage.platform.entity.RoleApplication;
import com.heritage.platform.entity.User;
import com.heritage.platform.mapper.AuditLogMapper;
import com.heritage.platform.mapper.NotificationMapper;
import com.heritage.platform.mapper.RoleApplicationMapper;
import com.heritage.platform.mapper.UserMapper;
import com.heritage.platform.service.MailService;
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
    @Autowired private RoleApplicationMapper roleAppMapper;
    @Autowired private NotificationMapper notificationMapper;
    @Autowired private AuditLogMapper auditLogMapper;
    @Autowired private MailService mailService;   // Mail service

    private Map<String, String> verifyCodeMap = new ConcurrentHashMap<>();

    // ==========================================================
    // 🔐 Authentication
    // ==========================================================

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Map<String, Object> res = new HashMap<>();
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", loginUser.getUsername()).or().eq("email", loginUser.getUsername());
        User user = userMapper.selectOne(query);

        if (user == null) {
            res.put("success", false); res.put("message", "Account or email not found!"); return res;
        }

        String encryptedInput = DigestUtils.md5DigestAsHex(loginUser.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedInput) && !user.getPassword().equals(loginUser.getPassword())) {
            res.put("success", false); res.put("message", "Incorrect password!"); return res;
        }

        res.put("success", true);
        res.put("username", user.getUsername());
        res.put("role", user.getRole());
        return res;
    }

    /**
     * Register: automatically reads the birthday field in request body (if provided by the frontend)
     */
    @PostMapping("/api/users/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", user.getUsername()).or().eq("email", user.getEmail());
        if (userMapper.selectCount(query) > 0) {
            res.put("success", false); res.put("message", "Sorry, this username or email is already registered!"); return res;
        }

        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setRole("VIEWER");
        userMapper.insert(user);   // Birthday will be saved automatically (if present)
        res.put("success", true); res.put("message", "Registered successfully. Please log in with your new account.");
        return res;
    }

    /**
     * Forgot password - send a real email (with a fallback)
     */
    @PostMapping("/api/users/forgot-password")
    public Map<String, Object> forgotPassword(@RequestParam String email) {
        Map<String, Object> res = new HashMap<>();
        if (userMapper.selectCount(new QueryWrapper<User>().eq("email", email)) == 0) {
            res.put("success", false); res.put("message", "This email is not registered!"); return res;
        }
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        verifyCodeMap.put(email, code);

        // Try sending real email first; if it fails, print to console to avoid blocking the flow
        try {
            mailService.sendVerificationCode(email, code);
        } catch (Exception e) {
            System.err.println("Failed to send email. Verification code: " + code);
            // Still return success because the code is stored in the Map; user can retrieve it from console (fallback)
        }

        res.put("success", true);
        res.put("message", "A verification code has been sent to your email. If you don't receive it, contact the administrator.");
        return res;
    }

    @PostMapping("/api/users/reset-password")
    public Map<String, Object> resetPassword(@RequestParam String email, @RequestParam String code, @RequestParam String newPassword) {
        Map<String, Object> res = new HashMap<>();
        if (!code.equals(verifyCodeMap.get(email))) {
            res.put("success", false); res.put("message", "Invalid or expired verification code!"); return res;
        }
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("email", email));
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        userMapper.updateById(user);
        verifyCodeMap.remove(email);
        res.put("success", true); res.put("message", "Password reset successfully. Please log in again.");
        return res;
    }

    // ==========================================================
    // 🧑‍💻 Profile
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
            // Birthday can also be updated in profile editing
            user.setBirthday(updatedUser.getBirthday());
            userMapper.updateById(user);
            res.put("success", true); res.put("message", "Profile updated successfully!");
        }
        return res;
    }

    @PostMapping("/api/users/apply-role")
    public Map<String, Object> applyForContributor(@RequestBody RoleApplication application) {
        Map<String, Object> res = new HashMap<>();
        long pendingCount = roleAppMapper.selectCount(new QueryWrapper<RoleApplication>()
                .eq("username", application.getUsername()).eq("status", 0));
        if (pendingCount > 0) {
            res.put("success", false); res.put("message", "You already have a pending application. Please do not submit again.");
            return res;
        }

        application.setStatus(0);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        roleAppMapper.insert(application);

        res.put("success", true); res.put("message", "Application submitted successfully. Please wait for administrator approval.");
        return res;
    }

    // ==========================================================
    // 🛡️ Admin management (ADMIN only)
    // ==========================================================

    @RoleCheck("ADMIN")
    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    @RoleCheck("ADMIN")
    @PutMapping("/api/users/{id}/role")
    public String updateRole(@PathVariable Long id, @RequestParam String role) {
        User user = userMapper.selectById(id);
        if (user != null) {
            String oldRole = user.getRole();
            user.setRole(role);
            userMapper.updateById(user);
            logAction("ROLE_CHANGE", id, "Admin manually changed role of " + user.getUsername() + " from " + oldRole + " to " + role);
            return "Role updated successfully";
        }
        return "User not found";
    }

    @RoleCheck("ADMIN")
    @DeleteMapping("/api/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userMapper.deleteById(id);
        return "Account deleted";
    }

    @RoleCheck("ADMIN")
    @GetMapping("/api/admin/role-applications")
    public List<RoleApplication> getPendingApplications() {
        return roleAppMapper.selectList(new QueryWrapper<RoleApplication>().eq("status", 0).orderByAsc("created_at"));
    }

    @RoleCheck("ADMIN")
    @PutMapping("/api/admin/role-applications/{id}")
    public Map<String, Object> processApplication(@PathVariable Long id, @RequestParam Integer status) {
        Map<String, Object> res = new HashMap<>();
        RoleApplication app = roleAppMapper.selectById(id);

        if (app == null) {
            res.put("success", false); res.put("message", "Application record does not exist!"); return res;
        }

        app.setStatus(status);
        app.setUpdatedAt(LocalDateTime.now());
        roleAppMapper.updateById(app);

        User targetUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", app.getUsername()));

        if (status == 1 && targetUser != null) {
            targetUser.setRole("CONTRIBUTOR");
            userMapper.updateById(targetUser);
            logAction("ROLE_APPROVE", targetUser.getId(), "Approved contributor application for " + app.getUsername());
        }

        if (targetUser != null) {
            Notification note = new Notification();
            note.setReceiverUsername(app.getUsername());
            String resultText = (status == 1)
                    ? "[APPROVED] Congratulations! You are now a contributor and can publish resources."
                    : "[REJECTED] Sorry, your contributor application was not approved.";
            note.setContent("Role promotion application result: " + resultText);
            note.setCreatedAt(LocalDateTime.now());
            notificationMapper.insert(note);
        }

        res.put("success", true); res.put("message", "Processing completed!");
        return res;
    }

    private void logAction(String type, Long targetId, String summary) {
        AuditLog log = new AuditLog();
        log.setUserId(UserContext.getCurrentUser());
        log.setActionType(type);
        log.setResourceId(targetId);
        log.setChangesSummary(summary);
        log.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(log);
    }
}
