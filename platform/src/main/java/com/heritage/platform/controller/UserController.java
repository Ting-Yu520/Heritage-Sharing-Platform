package com.heritage.platform.controller;

import com.heritage.platform.entity.User;
import com.heritage.platform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // 1. 获取所有用户列表 (管理员审计用)
    @GetMapping
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    // 2. 注册新用户 (公开接口)
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // 默认注册为贡献者
        user.setRole("CONTRIBUTOR");
        userMapper.insert(user);
        return "注册成功！";
    }

    // 3. 删除/注销用户
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userMapper.deleteById(id);
        return "账号已注销";
    }

    // 4. 修改角色 (提拔管理员)
    @PutMapping("/{id}/role")
    public String updateRole(@PathVariable Long id, @RequestParam String role) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setRole(role);
            userMapper.updateById(user);
            return "权限更新成功";
        }
        return "未找到用户";
    }
}