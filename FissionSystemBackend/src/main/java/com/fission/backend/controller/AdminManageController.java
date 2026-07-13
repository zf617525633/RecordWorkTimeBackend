package com.fission.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fission.backend.common.AdminContext;
import com.fission.backend.common.Result;
import com.fission.backend.entity.SysAdmin;
import com.fission.backend.mapper.SysAdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/manage")
public class AdminManageController {

    @Autowired
    private SysAdminMapper sysAdminMapper;

    /**
     * 新增普通管理员 (仅限超级管理员)
     */
    @PostMapping("/add")
    public Result<String> addAdmin(@RequestBody Map<String, String> body) {
        SysAdmin currentAdmin = AdminContext.get();
        if (currentAdmin == null || currentAdmin.getRole() != 1) {
            return Result.error(403, "无权限操作，仅限超级管理员");
        }

        String username = body.get("username");
        String password = body.get("password");
        String appId = body.get("appId");

        if (username == null || password == null || appId == null) {
            return Result.error(400, "参数不完整");
        }

        QueryWrapper<SysAdmin> qw = new QueryWrapper<>();
        qw.eq("username", username);
        if (sysAdminMapper.selectCount(qw) > 0) {
            return Result.error(400, "该账号已存在");
        }

        SysAdmin newAdmin = new SysAdmin();
        newAdmin.setUsername(username);
        newAdmin.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        newAdmin.setRole(2); // 普通管理员
        newAdmin.setAppId(appId);
        newAdmin.setStatus(1);
        newAdmin.setCreateTime(LocalDateTime.now());
        newAdmin.setUpdateTime(LocalDateTime.now());

        sysAdminMapper.insert(newAdmin);
        return Result.success("创建成功");
    }

    /**
     * 分页查询管理员列表 (仅限超级管理员)
     */
    @GetMapping("/list")
    public Result<Page<SysAdmin>> getAdminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        SysAdmin currentAdmin = AdminContext.get();
        if (currentAdmin == null || currentAdmin.getRole() != 1) {
            return Result.error(403, "无权限操作，仅限超级管理员");
        }

        QueryWrapper<SysAdmin> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");
        Page<SysAdmin> resultPage = sysAdminMapper.selectPage(new Page<>(page, size), qw);
        
        // 抹除密码下发
        for (SysAdmin admin : resultPage.getRecords()) {
            admin.setPassword(null);
        }

        return Result.success(resultPage);
    }
}
