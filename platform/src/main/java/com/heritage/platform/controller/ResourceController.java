package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.platform.entity.*;
import com.heritage.platform.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class ResourceController {

    @Autowired private HeritageResourceMapper resourceMapper;
    @Autowired private AuditLogMapper auditLogMapper;
    @Autowired private NotificationMapper notificationMapper;
    @Autowired private UserLikeMapper userLikeMapper;
    @Autowired private UserFavoriteMapper userFavoriteMapper;

    // ==========================================================
    // 🎨 创作者投稿中心 (Sprint 6: PBI 1, 3, 4, 5)
    // ==========================================================

    /**
     * 获取当前用户的所有稿件 (包含草稿、审核中、已发布、被驳回、已撤回)
     */
    @GetMapping("/api/my-resources")
    public Page<HeritageResource> getMyResources(
            @RequestParam String username,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<HeritageResource> page = new Page<>(current, size);
        QueryWrapper<HeritageResource> query = new QueryWrapper<HeritageResource>()
                .eq("contributor_username", username)
                .orderByDesc("updated_at");
        return resourceMapper.selectPage(page, query);
    }

    /**
     * PBI 1 & 5: 提交新稿件 (支持直接提交审核 status=0, 或存草稿 status=-1)
     */
    @PostMapping("/api/my-resources/submit")
    public Map<String, Object> submitMyResource(@RequestBody HeritageResource resource, @RequestParam(defaultValue = "0") Integer status) {
        Map<String, Object> res = new HashMap<>();
        resource.setStatus(status);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resourceMapper.insert(resource);

        String actionStr = status == -1 ? "Saved as draft" : "Submitted for review";
        logAction("CONTRIBUTOR_SUBMIT", resource.getId(), "User " + resource.getContributorUsername() + " " + actionStr);

        res.put("success", true);
        res.put("message", status == -1 ? "草稿保存成功！" : "提交成功，请等待管理员审核！");
        return res;
    }

    /**
     * PBI 3: 修改自己的稿件 (重新提交或更新草稿)
     */
    @PutMapping("/api/my-resources/{id}")
    public Map<String, Object> updateMyResource(@PathVariable Long id, @RequestBody HeritageResource resource, @RequestParam(defaultValue = "0") Integer status) {
        Map<String, Object> res = new HashMap<>();
        HeritageResource old = resourceMapper.selectById(id);
        if (old == null) {
            res.put("success", false); res.put("message", "资源不存在！"); return res;
        }

        old.setTitle(resource.getTitle());
        old.setDescription(resource.getDescription());
        old.setCategory(resource.getCategory());
        old.setThumbnail(resource.getThumbnail());
        old.setMediaUrl(resource.getMediaUrl());
        old.setTags(resource.getTags());
        old.setLocation(resource.getLocation());
        old.setStatus(status); // 更新状态 (-1 草稿 或 0 待审)
        old.setUpdatedAt(LocalDateTime.now());
        resourceMapper.updateById(old);

        logAction("CONTRIBUTOR_EDIT", id, "Resource updated and status set to " + status);
        res.put("success", true);
        res.put("message", status == -1 ? "草稿更新成功！" : "重新提交审核成功！");
        return res;
    }

    /**
     * PBI 4: 撤回正在审核中的稿件 (状态变为 4)
     */
    @PutMapping("/api/my-resources/{id}/withdraw")
    public Map<String, Object> withdrawMyResource(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        HeritageResource r = resourceMapper.selectById(id);
        // 只有 0(待审核) 的状态才能撤回
        if (r != null && r.getStatus() == 0) {
            r.setStatus(4); // 4 = 已撤回
            r.setUpdatedAt(LocalDateTime.now());
            resourceMapper.updateById(r);
            logAction("CONTRIBUTOR_WITHDRAW", id, "User withdrew the pending submission");
            res.put("success", true); res.put("message", "撤回成功！您的稿件已移出审核队列。");
        } else {
            res.put("success", false); res.put("message", "只能撤回正在审核中的稿件！");
        }
        return res;
    }

    // ==========================================================
    // 🏛️ 前台大厅、互动点赞、后台管理等旧接口 (保持不变，全部兼容)
    // ==========================================================

    @GetMapping("/api/public/resources")
    public Page<HeritageResource> getPublicResources(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "12") Integer size, @RequestParam(required = false) String keyword, @RequestParam(required = false) String category) {
        Page<HeritageResource> page = new Page<>(current, size);
        QueryWrapper<HeritageResource> query = new QueryWrapper<>();
        query.eq("status", 1);
        if (StringUtils.hasText(keyword)) query.and(q -> q.like("title", keyword).or().like("description", keyword).or().like("category", keyword));
        if (StringUtils.hasText(category)) query.eq("category", category);
        query.orderByDesc("created_at");
        return resourceMapper.selectPage(page, query);
    }

    @GetMapping("/api/public/categories/count")
    public Map<String, Long> getCategoryCounts() {
        List<HeritageResource> allPublic = resourceMapper.selectList(new QueryWrapper<HeritageResource>().eq("status", 1));
        return allPublic.stream().collect(Collectors.groupingBy(HeritageResource::getCategory, Collectors.counting()));
    }

    @GetMapping("/api/public/resources/{id}")
    public Map<String, Object> getResourceDetail(@PathVariable Long id, @RequestParam(required = false) String username) {
        Map<String, Object> res = new HashMap<>();
        HeritageResource resource = resourceMapper.selectById(id);
        if (resource == null || resource.getStatus() != 1) {
            res.put("success", false); res.put("message", "Resource does not exist or has been removed"); return res;
        }
        res.put("success", true); res.put("data", resource);
        res.put("likeCount", userLikeMapper.selectCount(new QueryWrapper<UserLike>().eq("resource_id", id)));
        res.put("favCount", userFavoriteMapper.selectCount(new QueryWrapper<UserFavorite>().eq("resource_id", id)));
        boolean isLiked = false, isFavorited = false;
        if (StringUtils.hasText(username)) {
            isLiked = userLikeMapper.selectCount(new QueryWrapper<UserLike>().eq("resource_id", id).eq("username", username)) > 0;
            isFavorited = userFavoriteMapper.selectCount(new QueryWrapper<UserFavorite>().eq("resource_id", id).eq("username", username)) > 0;
        }
        res.put("isLiked", isLiked); res.put("isFavorited", isFavorited);
        return res;
    }

    @PostMapping("/api/resources/{id}/like")
    public String toggleLike(@PathVariable Long id, @RequestParam String username) {
        QueryWrapper<UserLike> query = new QueryWrapper<UserLike>().eq("resource_id", id).eq("username", username);
        if (userLikeMapper.selectCount(query) > 0) { userLikeMapper.delete(query); return "unliked"; }
        else { UserLike like = new UserLike(); like.setResourceId(id); like.setUsername(username); like.setCreatedAt(LocalDateTime.now()); userLikeMapper.insert(like); return "liked"; }
    }

    @PostMapping("/api/resources/{id}/favorite")
    public String toggleFavorite(@PathVariable Long id, @RequestParam String username) {
        QueryWrapper<UserFavorite> query = new QueryWrapper<UserFavorite>().eq("resource_id", id).eq("username", username);
        if (userFavoriteMapper.selectCount(query) > 0) { userFavoriteMapper.delete(query); return "unfavorited"; }
        else { UserFavorite fav = new UserFavorite(); fav.setResourceId(id); fav.setUsername(username); fav.setCreatedAt(LocalDateTime.now()); userFavoriteMapper.insert(fav); return "favorited"; }
    }

    @GetMapping("/api/resources/favorites")
    public Page<HeritageResource> getMyFavorites(@RequestParam String username, @RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "12") Integer size) {
        List<UserFavorite> favs = userFavoriteMapper.selectList(new QueryWrapper<UserFavorite>().eq("username", username).orderByDesc("created_at"));
        if (favs.isEmpty()) return new Page<>(current, size);
        List<Long> resourceIds = favs.stream().map(UserFavorite::getResourceId).collect(Collectors.toList());
        Page<HeritageResource> page = new Page<>(current, size);
        return resourceMapper.selectPage(page, new QueryWrapper<HeritageResource>().in("id", resourceIds));
    }

    @GetMapping("/api/resources")
    public List<HeritageResource> getResources() { return resourceMapper.selectList(null); }

    @PostMapping("/api/resources")
    public String addResource(@RequestBody HeritageResource resource) {
        resource.setStatus(0);
        resource.setUpdatedAt(LocalDateTime.now());
        resourceMapper.insert(resource);
        logAction("CREATE", resource.getId(), "Created new resource: " + resource.getTitle());
        return "新增成功，请等待审核！";
    }

    @DeleteMapping("/api/resources/{id}")
    public String deleteResource(@PathVariable Long id) {
        resourceMapper.deleteById(id);
        return "删除成功！";
    }

    @GetMapping("/api/stats/summary")
    public Map<String, Integer> getSummary() {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("total", resourceMapper.selectCount(null).intValue());
        summary.put("pending", resourceMapper.selectCount(new QueryWrapper<HeritageResource>().eq("status", 0)).intValue());
        summary.put("published", resourceMapper.selectCount(new QueryWrapper<HeritageResource>().eq("status", 1)).intValue());
        summary.put("logs", auditLogMapper.selectCount(null).intValue());
        return summary;
    }

    @GetMapping("/api/resources/pending")
    public Page<HeritageResource> getPendingResources(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "20") Integer size, @RequestParam(required = false) String category, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
        Page<HeritageResource> page = new Page<>(current, size);
        QueryWrapper<HeritageResource> query = new QueryWrapper<HeritageResource>().eq("status", 0);
        if (StringUtils.hasText(category)) query.eq("category", category);
        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) query.between("created_at", startDate + " 00:00:00", endDate + " 23:59:59");
        query.orderByAsc("created_at");
        return resourceMapper.selectPage(page, query);
    }

    @PutMapping("/api/resources/{id}/status")
    public String updateResourceStatus(@PathVariable Long id, @RequestParam Integer status, @RequestParam(required = false) String feedback) {
        HeritageResource resource = resourceMapper.selectById(id);
        if (resource == null) return "未找到该资源！";
        resource.setStatus(status);
        resource.setUpdatedAt(LocalDateTime.now());
        resourceMapper.updateById(resource);

        Notification note = new Notification();
        note.setReceiverUsername(resource.getContributorUsername()); // 修复：通知发给真实的贡献者
        String resultText = (status == 1) ? "【已通过】您的资源已公开发布！" : (status == 2 ? "【被驳回】原因：" + feedback + "。请修改后重新提交。" : "【已归档】资源已被下架。");
        note.setContent("关于《" + resource.getTitle() + "》的审核结果：" + resultText);
        note.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(note);

        logAction((status == 3) ? "ARCHIVE" : (status == 1 ? "APPROVE" : "REJECT"), id, "Status updated to " + status);
        return "操作成功！";
    }

    private void logAction(String type, Long resId, String summary) {
        AuditLog log = new AuditLog();
        log.setUserId("System");
        log.setActionType(type);
        log.setResourceId(resId);
        log.setChangesSummary(summary);
        log.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(log);
    }
}