package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.platform.config.RoleCheck;
import com.heritage.platform.config.UserContext;
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
    // 🎨 Creator Center (CONTRIBUTOR / ADMIN)
    // ==========================================================

    @RoleCheck({"ADMIN", "CONTRIBUTOR"})
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

    @RoleCheck({"ADMIN", "CONTRIBUTOR"})
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
        res.put("message", status == -1 ? "Draft saved!" : "Submitted for review, waiting for admin approval.");
        return res;
    }

    @RoleCheck({"ADMIN", "CONTRIBUTOR"})
    @PutMapping("/api/my-resources/{id}")
    public Map<String, Object> updateMyResource(@PathVariable Long id, @RequestBody HeritageResource resource, @RequestParam(defaultValue = "0") Integer status) {
        Map<String, Object> res = new HashMap<>();
        HeritageResource old = resourceMapper.selectById(id);
        if (old == null) {
            res.put("success", false); res.put("message", "Resource not found!"); return res;
        }

        old.setTitle(resource.getTitle());
        old.setDescription(resource.getDescription());
        old.setCategory(resource.getCategory());
        old.setThumbnail(resource.getThumbnail());
        old.setMediaUrl(resource.getMediaUrl());
        old.setTags(resource.getTags());
        old.setLocation(resource.getLocation());
        old.setStatus(status);
        old.setUpdatedAt(LocalDateTime.now());
        resourceMapper.updateById(old);

        logAction("CONTRIBUTOR_EDIT", id, "Resource updated and status set to " + status);
        res.put("success", true);
        res.put("message", status == -1 ? "Draft updated!" : "Resubmitted for review successfully!");
        return res;
    }

    @RoleCheck({"ADMIN", "CONTRIBUTOR"})
    @PutMapping("/api/my-resources/{id}/withdraw")
    public Map<String, Object> withdrawMyResource(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        HeritageResource r = resourceMapper.selectById(id);
        if (r != null && r.getStatus() == 0) {
            r.setStatus(4);
            r.setUpdatedAt(LocalDateTime.now());
            resourceMapper.updateById(r);
            logAction("CONTRIBUTOR_WITHDRAW", id, "User withdrew the pending submission");
            res.put("success", true); res.put("message", "Withdrawn successfully!");
        } else {
            res.put("success", false); res.put("message", "Only pending submissions can be withdrawn!");
        }
        return res;
    }

    @RoleCheck({"ADMIN", "CONTRIBUTOR"})
    @DeleteMapping("/api/my-resources/{id}")
    public Map<String, Object> deleteMyResource(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        HeritageResource r = resourceMapper.selectById(id);
        if (r == null) {
            res.put("success", false);
            res.put("message", "Resource not found!");
            return res;
        }

        resourceMapper.deleteById(id);
        logAction("CONTRIBUTOR_DELETE", id, "Resource titled '" + r.getTitle() + "' deleted by contributor");
        res.put("success", true);
        res.put("message", "Deleted successfully!");
        return res;
    }

    // ==========================================================
    // 🏛️ Public Hall, Likes, Favorites (public)
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

    // ==========================================================
    // 🛡️ Admin only
    // ==========================================================

    @RoleCheck("ADMIN")
    @GetMapping("/api/resources")
    public List<HeritageResource> getResources() {
        return resourceMapper.selectList(null);
    }

    @RoleCheck("ADMIN")
    @PostMapping("/api/resources")
    public String addResource(@RequestBody HeritageResource resource) {
        resource.setStatus(0);
        resource.setUpdatedAt(LocalDateTime.now());
        resourceMapper.insert(resource);
        logAction("CREATE", resource.getId(), "Created new resource: " + resource.getTitle());
        return "New resource added, waiting for review.";
    }

    @RoleCheck("ADMIN")
    @DeleteMapping("/api/resources/{id}")
    public String deleteResource(@PathVariable Long id) {
        resourceMapper.deleteById(id);
        return "Deleted successfully!";
    }

    // ✨ Modified: Allow ADMIN and CONTRIBUTOR to view dashboard stats
    @RoleCheck({"ADMIN", "CONTRIBUTOR"})
    @GetMapping("/api/stats/summary")
    public Map<String, Integer> getSummary() {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("total", resourceMapper.selectCount(null).intValue());
        summary.put("pending", resourceMapper.selectCount(new QueryWrapper<HeritageResource>().eq("status", 0)).intValue());
        summary.put("published", resourceMapper.selectCount(new QueryWrapper<HeritageResource>().eq("status", 1)).intValue());
        summary.put("logs", auditLogMapper.selectCount(null).intValue());
        return summary;
    }

    @RoleCheck("ADMIN")
    @GetMapping("/api/resources/pending")
    public Page<HeritageResource> getPendingResources(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "20") Integer size, @RequestParam(required = false) String category, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
        Page<HeritageResource> page = new Page<>(current, size);
        QueryWrapper<HeritageResource> query = new QueryWrapper<HeritageResource>().eq("status", 0);
        if (StringUtils.hasText(category)) query.eq("category", category);
        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) query.between("created_at", startDate + " 00:00:00", endDate + " 23:59:59");
        query.orderByAsc("created_at");
        return resourceMapper.selectPage(page, query);
    }

    @RoleCheck("ADMIN")
    @PutMapping("/api/resources/{id}/status")
    public String updateResourceStatus(@PathVariable Long id, @RequestParam Integer status, @RequestParam(required = false) String feedback) {
        HeritageResource resource = resourceMapper.selectById(id);
        if (resource == null) return "Resource not found!";
        resource.setStatus(status);
        resource.setUpdatedAt(LocalDateTime.now());
        resourceMapper.updateById(resource);

        Notification note = new Notification();
        note.setReceiverUsername(resource.getContributorUsername());
        String resultText = (status == 1) ? "Approved! Your resource is now published." : (status == 2 ? "Rejected. Reason: " + feedback + ". Please revise and resubmit." : "Archived. The resource has been taken down.");
        note.setContent("Audit result for " + resource.getTitle() + ": " + resultText);
        note.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(note);

        logAction((status == 3) ? "ARCHIVE" : (status == 1 ? "APPROVE" : "REJECT"), id, "Status updated to " + status);
        return "Operation successful!";
    }

    private void logAction(String type, Long resId, String summary) {
        AuditLog log = new AuditLog();
        log.setUserId(UserContext.getCurrentUser());
        log.setActionType(type);
        log.setResourceId(resId);
        log.setChangesSummary(summary);
        log.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(log);
    }
}