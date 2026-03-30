package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.entity.Comment;
import com.heritage.platform.entity.CommentReport;
import com.heritage.platform.entity.Notification;
import com.heritage.platform.mapper.CommentMapper;
import com.heritage.platform.mapper.CommentReportMapper;
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
    @Autowired private NotificationMapper notificationMapper;

    // ==========================================================
    // 💬 评论核心功能 (PBI 1, 2, 4)
    // ==========================================================

    /**
     * ✨ 获取资源的评论树 (支持无限极嵌套，自动处理软删除占位符)
     */
    @GetMapping("/api/public/resources/{resourceId}/comments")
    public List<Comment> getResourceComments(@PathVariable Long resourceId) {
        // 1. 获取该资源下的所有评论 (按时间正序)
        List<Comment> allComments = commentMapper.selectList(
                new QueryWrapper<Comment>().eq("resource_id", resourceId).orderByAsc("created_at")
        );

        Map<Long, Comment> commentMap = new HashMap<>();
        List<Comment> rootComments = new ArrayList<>();

        // 2. 数据清洗与组装
        for (Comment c : allComments) {
            c.setChildren(new ArrayList<>());

            // ✨ PBI 4: 处理软删除 (保留楼层，只把内容和用户名变成 [Deleted])
            if (c.getIsDeleted() == 1) {
                c.setUsername("[Deleted]");
                c.setContent("[Deleted]");
                c.setLikes(0);
                c.setDislikes(0);
            }
            commentMap.put(c.getId(), c);
        }

        // 3. 构建嵌套树形结构
        for (Comment c : allComments) {
            if (c.getParentId() == 0) {
                rootComments.add(c); // 一级评论
            } else {
                Comment parent = commentMap.get(c.getParentId());
                if (parent != null) {
                    parent.getChildren().add(c); // 挂载到父评论下
                }
            }
        }

        // 翻转一级评论，让最新的在最上面
        Collections.reverse(rootComments);
        return rootComments;
    }

    /**
     * ✨ 发布新评论 / 回复评论
     */
    @PostMapping("/api/comments")
    public Map<String, Object> addComment(@RequestBody Comment comment) {
        Map<String, Object> res = new HashMap<>();

        // PBI 1: 字数限制防御
        if (comment.getContent().length() > 1000) {
            res.put("success", false); res.put("message", "评论内容不能超过1000字！"); return res;
        }

        comment.setIsDeleted(0);
        comment.setIsEdited(0);
        comment.setLikes(0);
        comment.setDislikes(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        // ✨ PBI 5: 如果是回复别人，立刻给对方发一条系统通知
        if (comment.getParentId() != 0 && comment.getReplyTo() != null) {
            Notification note = new Notification();
            note.setReceiverUsername(comment.getReplyTo());
            note.setContent("🔔 用户 " + comment.getUsername() + " 回复了您的评论！内容：" +
                    (comment.getContent().length() > 20 ? comment.getContent().substring(0, 20) + "..." : comment.getContent()));
            note.setCreatedAt(LocalDateTime.now());
            notificationMapper.insert(note);
        }

        res.put("success", true); res.put("message", "评论发表成功！");
        return res;
    }

    /**
     * ✨ PBI 4: 修改自己的评论 (限时 30 分钟内)
     */
    @PutMapping("/api/comments/{id}")
    public Map<String, Object> editComment(@PathVariable Long id, @RequestBody Comment updateData) {
        Map<String, Object> res = new HashMap<>();
        Comment c = commentMapper.selectById(id);

        if (c == null || c.getIsDeleted() == 1) {
            res.put("success", false); res.put("message", "评论不存在或已删除！"); return res;
        }

        // ✨ 高级业务校验：检查是否超过 30 分钟
        long minutesPassed = Duration.between(c.getCreatedAt(), LocalDateTime.now()).toMinutes();
        if (minutesPassed > 30) {
            res.put("success", false); res.put("message", "抱歉，评论发布超过30分钟后不可再修改！"); return res;
        }

        c.setContent(updateData.getContent());
        c.setIsEdited(1); // 标记为已编辑
        c.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(c);

        res.put("success", true); res.put("message", "评论修改成功！");
        return res;
    }

    /**
     * ✨ PBI 4: 软删除自己的评论
     */
    @DeleteMapping("/api/comments/{id}")
    public Map<String, Object> deleteComment(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        Comment c = commentMapper.selectById(id);
        if (c != null) {
            c.setIsDeleted(1); // 仅仅是软删除打标，不执行物理删除 (保留占位符)
            commentMapper.updateById(c);
        }
        res.put("success", true); res.put("message", "评论已删除。");
        return res;
    }

    /**
     * ✨ PBI 2: 评论点赞与踩 (极简逻辑：发请求就累加)
     */
    @PostMapping("/api/comments/{id}/action")
    public String commentAction(@PathVariable Long id, @RequestParam String type) {
        Comment c = commentMapper.selectById(id);
        if (c != null && c.getIsDeleted() == 0) {
            if ("like".equals(type)) c.setLikes(c.getLikes() + 1);
            if ("dislike".equals(type)) c.setDislikes(c.getDislikes() + 1);
            commentMapper.updateById(c);
        }
        return "success";
    }

    // ==========================================================
    // 🛡️ 风控与举报中心 (PBI 3, 5)
    // ==========================================================

    /**
     * ✨ PBI 3: 提交举报
     */
    @PostMapping("/api/comments/report")
    public Map<String, Object> reportComment(@RequestBody CommentReport report) {
        Map<String, Object> res = new HashMap<>();
        report.setStatus(0); // 待处理
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportMapper.insert(report);
        res.put("success", true); res.put("message", "举报已提交，感谢您协助维护社区环境！");
        return res;
    }

    /**
     * ✨ PBI 5: 管理员获取待处理的举报列表
     */
    @GetMapping("/api/admin/comment-reports")
    public List<CommentReport> getPendingReports() {
        return reportMapper.selectList(new QueryWrapper<CommentReport>().eq("status", 0).orderByAsc("created_at"));
    }

    /**
     * ✨ PBI 5: 管理员处理举报 (并联动发送通知)
     */
    @PutMapping("/api/admin/comment-reports/{id}")
    public Map<String, Object> processReport(@PathVariable Long id, @RequestParam Integer status) {
        Map<String, Object> res = new HashMap<>();
        CommentReport report = reportMapper.selectById(id);
        if (report == null) {
            res.put("success", false); res.put("message", "举报记录不存在！"); return res;
        }

        report.setStatus(status);
        report.setUpdatedAt(LocalDateTime.now());
        reportMapper.updateById(report);

        // 发送通知给举报人：告知其举报的处理结果
        Notification reporterNote = new Notification();
        reporterNote.setReceiverUsername(report.getReporterUsername());
        String resultTxt = (status == 1) ? "已被系统核实并清理。" : "经系统核查未违规，已忽略。";
        reporterNote.setContent("🛡️ 【风控中心】您举报的评论 " + resultTxt + " 感谢您的反馈！");
        reporterNote.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(reporterNote);

        // 如果举报成立 (status == 1)，强制软删除该评论！
        if (status == 1) {
            Comment targetComment = commentMapper.selectById(report.getCommentId());
            if (targetComment != null) {
                targetComment.setIsDeleted(1);
                commentMapper.updateById(targetComment);

                // 发警告通知给违规评论的作者
                Notification authorNote = new Notification();
                authorNote.setReceiverUsername(targetComment.getUsername());
                authorNote.setContent("⚠️ 【系统警告】您的一条评论因违反社区规定被多名用户举报，现已被系统强制清理。请规范发言！");
                authorNote.setCreatedAt(LocalDateTime.now());
                notificationMapper.insert(authorNote);
            }
        }

        res.put("success", true); res.put("message", "举报处理完毕！");
        return res;
    }
}