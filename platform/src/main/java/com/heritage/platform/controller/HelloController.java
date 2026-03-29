package com.heritage.platform.controller;

import org.springframework.web.bind.annotation.CrossOrigin; // 注意这里会自动多出一行导入
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin // 👈 新增这一行：允许前端跨域访问
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "太帅了！我的社区遗产策展平台后端启动成功啦！";
    }
}