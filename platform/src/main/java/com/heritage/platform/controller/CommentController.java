package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.entity.Comment;
import com.heritage.platform.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin
@RestController
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;

    // 1. 获取某个资源的所有评论
    @GetMapping("/api/public/resources/{resourceId}/comments")
    public List<Comment> getComments(@PathVariable Long resourceId) {
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.eq("resource_id", resourceId).orderByDesc("created_at"); // 按时间倒序，最新的在上面
        return commentMapper.selectList(query);
    }

    // 2. 提交新评论
    @PostMapping("/api/public/comments")
    public String addComment(@RequestBody Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        // 如果没传用户名，就默认是匿名游客
        if (comment.getUsername() == null || comment.getUsername().isEmpty()) {
            comment.setUsername("匿名文化爱好者");
        }
        commentMapper.insert(comment);
        return "评论成功！";
    }
}