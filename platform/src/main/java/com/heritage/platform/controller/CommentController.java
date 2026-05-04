package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.config.RoleCheck;
import com.heritage.platform.entity.Comment;
import com.heritage.platform.entity.CommentLike;
import com.heritage.platform.entity.CommentReport;
import com.heritage.platform.entity.Notification;
import com.heritage.platform.mapper.CommentMapper;
import com.heritage.platform.mapper.CommentReportMapper;
import com.heritage.platform.mapper.CommentLikeMapper;
import com.heritage.platform.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin
@RestController
public class CommentController {

    @Autowired private CommentMapper commentMapper;
    @Autowired private CommentReportMapper reportMapper;
    @Autowired private CommentLikeMapper commentLikeMapper;
    @Autowired private NotificationMapper notificationMapper;

    // ==========================================================
    // 💬 Comment core features (public + logged-in users)
    // ==========================================================

    /**
     * Get the comment tree for a resource (supports like/dislike state)
     */
    @GetMapping("/api/public/resources/{resourceId}/comments")
    public List<Comment> getResourceComments(
            @PathVariable Long resourceId,
            @RequestParam(required = false) String username) {

        List<Comment> allComments = commentMapper.selectList(
                new QueryWrapper<Comment>().eq("resource_id", resourceId).orderByAsc("created_at")
        );

        Map<Long, Comment> commentMap = new HashMap<>();
        List<Comment> rootComments = new ArrayList<>();

        for (Comment c : allComments) {
            c.setChildren(new ArrayList<>());
            if (c.getIsDeleted() == 1) {
                c.setUsername("[Deleted]");
                c.setContent("[Deleted]");
                c.setLikes(0);
                c.setDislikes(0);
            }
            // Query current user's like/dislike state for this comment
            if (username != null) {
                boolean isLiked = commentLikeMapper.selectCount(
                        new QueryWrapper<CommentLike>().eq("comment_id", c.getId())
                                .eq("username", username).eq("type", "like")) > 0;
                boolean isDisliked = commentLikeMapper.selectCount(
                        new QueryWrapper<CommentLike>().eq("comment_id", c.getId())
                                .eq("username", username).eq("type", "dislike")) > 0;
                c.setIsLiked(isLiked);
                c.setIsDisliked(isDisliked);
            }
            commentMap.put(c.getId(), c);
        }

        for (Comment c : allComments) {
            if (c.getParentId() == 0) {
                rootComments.add(c);
            } else {
                Comment parent = commentMap.get(c.getParentId());
                if (parent != null) {
                    parent.getChildren().add(c);
                }
            }
        }

        Collections.reverse(rootComments);
        return rootComments;
    }

    /**
     * Logged-in users post comments (notification errors are isolated)
     */
    @PostMapping("/api/comments")
    public Map<String, Object> addComment(@RequestBody Comment comment) {
        Map<String, Object> res = new HashMap<>();

        if (comment.getContent().length() > 1000) {
            res.put("success", false);
            res.put("message", "Comment content cannot exceed 1000 characters!");
            return res;
        }

        comment.setIsDeleted(0);
        comment.setIsEdited(0);
        comment.setLikes(0);
        comment.setDislikes(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        if (comment.getParentId() != 0 && comment.getReplyTo() != null) {
            try {
                Notification note = new Notification();
                note.setReceiverUsername(comment.getReplyTo());
                note.setContent("🔔 User " + comment.getUsername() + " replied to your comment. Content: " +
                        (comment.getContent().length() > 20 ? comment.getContent().substring(0, 20) + "..." : comment.getContent()));
                note.setCreatedAt(LocalDateTime.now());
                notificationMapper.insert(note);
            } catch (Exception e) {
                System.err.println("Failed to send reply notification: " + e.getMessage());
            }
        }

        res.put("success", true);
        res.put("message", "Comment posted successfully!");
        return res;
    }

    /**
     * Public comment API (anonymous supported)
     */
    @PostMapping("/api/public/comments")
    public Map<String, Object> addPublicComment(@RequestBody Comment comment) {
        if (comment.getUsername() == null || comment.getUsername().trim().isEmpty()) {
            comment.setUsername("Anonymous Cultural Enthusiast");
        }
        comment.setParentId(0L);
        comment.setReplyTo(null);
        return addComment(comment);
    }

    /**
     * Edit your own comment (within 30 minutes)
     */
    @PutMapping("/api/comments/{id}")
    public Map<String, Object> editComment(@PathVariable Long id, @RequestBody Comment updateData) {
        Map<String, Object> res = new HashMap<>();
        Comment c = commentMapper.selectById(id);
        if (c == null || c.getIsDeleted() == 1) {
            res.put("success", false);
            res.put("message", "Comment does not exist or has been deleted!");
            return res;
        }
        long minutesPassed = Duration.between(c.getCreatedAt(), LocalDateTime.now()).toMinutes();
        if (minutesPassed > 30) {
            res.put("success", false);
            res.put("message", "Sorry, comments cannot be edited after 30 minutes!");
            return res;
        }
        c.setContent(updateData.getContent());
        c.setIsEdited(1);
        c.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(c);
        res.put("success", true);
        res.put("message", "Comment updated successfully!");
        return res;
    }

    /**
     * Soft delete a comment
     */
    @DeleteMapping("/api/comments/{id}")
    public Map<String, Object> deleteComment(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        Comment c = commentMapper.selectById(id);
        if (c != null) {
            c.setIsDeleted(1);
            commentMapper.updateById(c);
        }
        res.put("success", true);
        res.put("message", "Comment deleted.");
        return res;
    }

    /**
     * Like/dislike (toggle; one per user)
     */
    @PostMapping("/api/comments/{id}/action")
    public Map<String, Object> commentAction(
            @PathVariable Long id,
            @RequestParam String type,
            @RequestParam String username) {

        Map<String, Object> res = new HashMap<>();
        Comment c = commentMapper.selectById(id);
        if (c == null || c.getIsDeleted() == 1) {
            res.put("success", false);
            return res;
        }

        QueryWrapper<CommentLike> query = new QueryWrapper<CommentLike>()
                .eq("comment_id", id).eq("username", username).eq("type", type);
        CommentLike exist = commentLikeMapper.selectOne(query);

        if (exist != null) {
            commentLikeMapper.deleteById(exist.getId());
            if ("like".equals(type)) c.setLikes(Math.max(0, c.getLikes() - 1));
            if ("dislike".equals(type)) c.setDislikes(Math.max(0, c.getDislikes() - 1));
            res.put("action", "un" + type);
        } else {
            CommentLike cl = new CommentLike();
            cl.setCommentId(id);
            cl.setUsername(username);
            cl.setType(type);
            cl.setCreatedAt(LocalDateTime.now());
            commentLikeMapper.insert(cl);
            if ("like".equals(type)) c.setLikes(c.getLikes() + 1);
            if ("dislike".equals(type)) c.setDislikes(c.getDislikes() + 1);
            res.put("action", type);
        }

        commentMapper.updateById(c);
        res.put("success", true);
        return res;
    }

    // ==========================================================
    // 🛡️ Risk control & reporting (public submit, admin processes)
    // ==========================================================

    /**
     * Submit a report (any logged-in user)
     */
    @PostMapping("/api/comments/report")
    public Map<String, Object> reportComment(@RequestBody CommentReport report) {
        Map<String, Object> res = new HashMap<>();
        report.setStatus(0);
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportMapper.insert(report);
        res.put("success", true);
        res.put("message", "Report submitted. Thank you for helping maintain the community!");
        return res;
    }

    /**
     * Admin: get pending reports
     */
    @RoleCheck("ADMIN")
    @GetMapping("/api/admin/comment-reports")
    public List<CommentReport> getPendingReports() {
        return reportMapper.selectList(new QueryWrapper<CommentReport>().eq("status", 0).orderByAsc("created_at"));
    }

    /**
     * Admin: process a report (also sends notifications)
     */
    @RoleCheck("ADMIN")
    @PutMapping("/api/admin/comment-reports/{id}")
    public Map<String, Object> processReport(@PathVariable Long id, @RequestParam Integer status) {
        Map<String, Object> res = new HashMap<>();
        CommentReport report = reportMapper.selectById(id);
        if (report == null) {
            res.put("success", false);
            res.put("message", "Report record does not exist!");
            return res;
        }
        report.setStatus(status);
        report.setUpdatedAt(LocalDateTime.now());
        reportMapper.updateById(report);

        try {
            Notification reporterNote = new Notification();
            reporterNote.setReceiverUsername(report.getReporterUsername());
            String resultTxt = (status == 1) ? "has been verified and removed." : "was found not violating and has been ignored.";
            reporterNote.setContent("🛡️ [Risk Control] The comment you reported " + resultTxt + " Thank you for your feedback!");
            reporterNote.setCreatedAt(LocalDateTime.now());
            notificationMapper.insert(reporterNote);
        } catch (Exception e) {
            System.err.println("Failed to send report notification: " + e.getMessage());
        }

        if (status == 1) {
            Comment targetComment = commentMapper.selectById(report.getCommentId());
            if (targetComment != null) {
                targetComment.setIsDeleted(1);
                commentMapper.updateById(targetComment);
                try {
                    Notification authorNote = new Notification();
                    authorNote.setReceiverUsername(targetComment.getUsername());
                    authorNote.setContent("⚠️ [System Warning] One of your comments was reported by multiple users for violating community rules and has been removed. Please follow the guidelines.");
                    authorNote.setCreatedAt(LocalDateTime.now());
                    notificationMapper.insert(authorNote);
                } catch (Exception e) {
                    System.err.println("Failed to send warning notification: " + e.getMessage());
                }
            }
        }
        res.put("success", true);
        res.put("message", "Report processed!");
        return res;
    }
}
