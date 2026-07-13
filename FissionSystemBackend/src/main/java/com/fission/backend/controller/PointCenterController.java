package com.fission.backend.controller;

import com.fission.backend.common.Result;
import com.fission.backend.dto.point.*;
import com.fission.backend.service.PointCenterService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/point-center")
public class PointCenterController {

    @Autowired
    private PointCenterService pointCenterService;

    // For mock/demo, we pass userId via query params. In real app, get from Token (ThreadLocal).
    @GetMapping("/index")
    public Result<PointCenterIndexVO> getIndex(@RequestParam(defaultValue = "1") Long userId,
                                               @RequestParam(defaultValue = "app_fc131d57") String appId) {
        return Result.success(pointCenterService.getHomeData(userId, appId));
    }

    @PostMapping("/tasks/checkin")
    public Result<CheckInResultVO> checkIn(@RequestParam(defaultValue = "1") Long userId,
                                           @RequestParam(defaultValue = "app_fc131d57") String appId) {
        try {
            return Result.success(pointCenterService.checkIn(userId, appId));
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @PostMapping("/tasks/video")
    public Result<VideoTaskResultVO> watchVideo(@RequestBody VideoTaskRequest request,
                                                @RequestParam(defaultValue = "1") Long userId,
                                                @RequestParam(defaultValue = "app_fc131d57") String appId) {
        try {
            return Result.success(pointCenterService.watchVideo(userId, appId));
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @PostMapping("/tasks/claim")
    public Result<TaskClaimResultVO> claimTask(@RequestBody TaskClaimRequest request,
                                               @RequestParam(defaultValue = "1") Long userId,
                                               @RequestParam(defaultValue = "app_fc131d57") String appId) {
        try {
            return Result.success(pointCenterService.claimTask(userId, appId, request.getTaskId()));
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
