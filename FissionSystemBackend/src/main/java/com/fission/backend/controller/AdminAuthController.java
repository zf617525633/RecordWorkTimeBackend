package com.fission.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fission.backend.common.AdminContext;
import com.fission.backend.common.Result;
import com.fission.backend.entity.SysAdmin;
import com.fission.backend.mapper.SysAdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private SysAdminMapper sysAdminMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return Result.error(400, "账号或密码不能为空");
        }

        QueryWrapper<SysAdmin> qw = new QueryWrapper<>();
        qw.eq("username", username);
        SysAdmin admin = sysAdminMapper.selectOne(qw);

        if (admin == null) {
            return Result.error(401, "账号不存在");
        }
        
        if (admin.getStatus() == 0) {
            return Result.error(401, "账号已被禁用");
        }

        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!admin.getPassword().equals(md5Password)) {
            return Result.error(401, "密码错误");
        }

        // 登录成功，生成Token并存入Redis
        String token = UUID.randomUUID().toString().replace("-", "");
        String redisKey = "admin_token:" + token;
        redisTemplate.opsForValue().set(redisKey, admin.getId().toString(), Duration.ofHours(24));

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("role", admin.getRole());
        data.put("username", admin.getUsername());

        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String actualToken = token.substring(7);
            redisTemplate.delete("admin_token:" + actualToken);
        }
        return Result.success("退出成功");
    }

    @GetMapping("/info")
    public Result<SysAdmin> getInfo() {
        SysAdmin currentAdmin = AdminContext.get();
        if (currentAdmin != null) {
            currentAdmin.setPassword(null); // 安全脱敏
        }
        return Result.success(currentAdmin);
    }
}
