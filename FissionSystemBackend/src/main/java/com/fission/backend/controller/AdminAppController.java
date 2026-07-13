package com.fission.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fission.backend.common.Result;
import com.fission.backend.entity.SysApp;
import com.fission.backend.mapper.SysAppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/app")
public class AdminAppController {

    @Autowired
    private SysAppMapper sysAppMapper;

    @GetMapping("/list")
    public Result<List<SysApp>> getAppList() {
        QueryWrapper<SysApp> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");
        return Result.success(sysAppMapper.selectList(qw));
    }

    @PostMapping("/add")
    public Result<String> addApp(@RequestBody Map<String, String> body) {
        String appName = body.get("appName");
        if (appName == null || appName.trim().isEmpty()) {
            return Result.error(400, "应用名称不能为空");
        }

        SysApp sysApp = new SysApp();
        sysApp.setAppName(appName);
        
        // 自动生成 appId 和 appSecret
        String appId = "app_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String appSecret = "sk_" + UUID.randomUUID().toString().replace("-", "");
        
        sysApp.setAppId(appId);
        sysApp.setAppSecret(appSecret);
        sysApp.setStatus(1);
        sysApp.setCreateTime(LocalDateTime.now());
        sysApp.setUpdateTime(LocalDateTime.now());

        sysAppMapper.insert(sysApp);
        return Result.success("创建成功");
    }
}
