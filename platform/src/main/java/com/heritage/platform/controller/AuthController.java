package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.entity.User;
import com.heritage.platform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Map<String, Object> result = new HashMap<>();

        // 去数据库里找：账号和密码都完全匹配的那个人
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", loginUser.getUsername())
                .eq("password", loginUser.getPassword());

        User user = userMapper.selectOne(query);

        if (user != null) {
            // 找到了，准许通行！
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("username", user.getUsername());
            result.put("role", user.getRole()); // 顺便把身份(ADMIN/CONTRIBUTOR)也发给前端
        } else {
            // 没找到，拦截！
            result.put("success", false);
            result.put("message", "账号或密码错误");
        }
        return result;
    }
}