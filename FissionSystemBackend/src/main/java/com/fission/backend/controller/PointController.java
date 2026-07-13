package com.fission.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fission.backend.common.Result;
import com.fission.backend.entity.FissionPointLog;
import com.fission.backend.mapper.FissionPointLogMapper;
import com.fission.backend.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/point")
public class PointController {

    @Autowired
    private PointService pointService;

    @Autowired
    private FissionPointLogMapper pointLogMapper;

    /**
     * 每日签到接口
     */
    @PostMapping("/checkIn")
    public Result<Integer> checkIn(@RequestParam String appId, @RequestParam Long userId) {
        try {
            Integer points = pointService.checkIn(appId, userId);
            return Result.success(points);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "签到失败，系统异常");
        }
    }

    /**
     * 获取积分流水记录 (分页)
     */
    @GetMapping("/logs")
    public Result<Page<FissionPointLog>> getPointLogs(
            @RequestParam String appId, 
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
            
        QueryWrapper<FissionPointLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                    .eq("user_id", userId)
                    .orderByDesc("create_time");
                    
        Page<FissionPointLog> logPage = pointLogMapper.selectPage(new Page<>(page, size), queryWrapper);
        return Result.success(logPage);
    }
}
