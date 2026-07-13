package com.fission.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fission.backend.dto.point.*;
import com.fission.backend.entity.FissionAccount;
import com.fission.backend.entity.FissionPointLog;
import com.fission.backend.entity.FissionUserCheckin;
import com.fission.backend.entity.FissionUserTaskLog;
import com.fission.backend.mapper.FissionAccountMapper;
import com.fission.backend.mapper.FissionPointLogMapper;
import com.fission.backend.mapper.FissionUserCheckinMapper;
import com.fission.backend.mapper.FissionUserTaskLogMapper;
import com.fission.backend.service.PointCenterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PointCenterServiceImpl implements PointCenterService {

    @Autowired
    private FissionAccountMapper fissionAccountMapper;

    @Autowired
    private FissionPointLogMapper fissionPointLogMapper;

    @Autowired
    private FissionUserCheckinMapper fissionUserCheckinMapper;

    @Autowired
    private FissionUserTaskLogMapper fissionUserTaskLogMapper;

    @Override
    public PointCenterIndexVO getHomeData(Long userId, String appId) {
        PointCenterIndexVO vo = new PointCenterIndexVO();

        // 1. Account Info
        FissionAccount account = fissionAccountMapper.selectById(userId);
        PointCenterIndexVO.AccountInfo accountInfo = new PointCenterIndexVO.AccountInfo();
        if (account != null) {
            accountInfo.setAvailablePoints(account.getAvailablePoints());
            // TODO: query today's earned points from log
            accountInfo.setTodayEarnedPoints(0); 
            // 10000 points = 1 CNY
            accountInfo.setCnyEquivalent(String.format("%.2f", account.getAvailablePoints() / 10000.0));
        } else {
            accountInfo.setAvailablePoints(0);
            accountInfo.setTodayEarnedPoints(0);
            accountInfo.setCnyEquivalent("0.00");
        }
        vo.setAccount(accountInfo);

        // 2. Newcomer Task Info (CheckIn)
        PointCenterIndexVO.NewcomerTaskInfo newcomer = new PointCenterIndexVO.NewcomerTaskInfo();
        FissionUserCheckin checkin = fissionUserCheckinMapper.selectOne(new QueryWrapper<FissionUserCheckin>().eq("user_id", userId));
        if (checkin != null) {
            newcomer.setCheckInProgress(checkin.getContinuousDays());
            newcomer.setIsTodayCheckedIn(LocalDate.now().equals(checkin.getLastCheckinDate()));
        } else {
            newcomer.setCheckInProgress(0);
            newcomer.setIsTodayCheckedIn(false);
        }
        newcomer.setWithdrawProgress(0); // Mock
        newcomer.setIsTodayWithdrawn(false); // Mock
        vo.setNewcomerTask(newcomer);

        // 3. Video Task Info
        PointCenterIndexVO.VideoTaskInfo videoTask = new PointCenterIndexVO.VideoTaskInfo();
        FissionUserTaskLog videoLog = getTaskLog(userId, appId, "task_video", LocalDate.now());
        videoTask.setWatchedCount(videoLog.getProgressCount());
        videoTask.setMaxLimit(5);
        videoTask.setRewardPerVideo(1144);
        vo.setVideoTask(videoTask);

        // 4. Daily Tasks
        List<PointCenterIndexVO.DailyTaskInfo> dailyTasks = new ArrayList<>();
        
        dailyTasks.add(buildDailyTask("task_daily_login", "天天领金币", "每天领取一次，金币天天送不停", 400, "Coins", getTaskLog(userId, appId, "task_daily_login", LocalDate.now())));
        dailyTasks.add(buildDailyTask("task_lottery_ticket", "签到天天抽奖券", "签到天天得奖券", 1000, "Ticket", getTaskLog(userId, appId, "task_lottery_ticket", LocalDate.now())));
        dailyTasks.add(buildDailyTask("task_lucky_wheel", "金币大转盘", "最高可得10000金币", 10000, "Trophy", getTaskLog(userId, appId, "task_lucky_wheel", LocalDate.now())));
        
        vo.setDailyTasks(dailyTasks);

        return vo;
    }

    private PointCenterIndexVO.DailyTaskInfo buildDailyTask(String id, String title, String sub, Integer reward, String icon, FissionUserTaskLog log) {
        PointCenterIndexVO.DailyTaskInfo t = new PointCenterIndexVO.DailyTaskInfo();
        t.setTaskId(id);
        t.setTitle(title);
        t.setSubTitle(sub);
        t.setRewardPoints(reward);
        t.setIconType(icon);
        t.setStatus(log.getStatus());
        return t;
    }

    private FissionUserTaskLog getTaskLog(Long userId, String appId, String taskId, LocalDate date) {
        FissionUserTaskLog log = fissionUserTaskLogMapper.selectOne(new QueryWrapper<FissionUserTaskLog>()
                .eq("user_id", userId).eq("task_id", taskId).eq("record_date", date));
        if (log == null) {
            log = new FissionUserTaskLog();
            log.setAppId(appId);
            log.setUserId(userId);
            log.setTaskId(taskId);
            log.setRecordDate(date);
            log.setProgressCount(0);
            log.setStatus(0);
            fissionUserTaskLogMapper.insert(log);
        }
        return log;
    }

    @Override
    @Transactional
    public CheckInResultVO checkIn(Long userId, String appId) {
        LocalDate today = LocalDate.now();
        FissionUserCheckin checkin = fissionUserCheckinMapper.selectOne(new QueryWrapper<FissionUserCheckin>().eq("user_id", userId));
        
        if (checkin == null) {
            checkin = new FissionUserCheckin();
            checkin.setAppId(appId);
            checkin.setUserId(userId);
            checkin.setContinuousDays(0);
            fissionUserCheckinMapper.insert(checkin);
        }
        
        if (today.equals(checkin.getLastCheckinDate())) {
            throw new RuntimeException("今日已签到");
        }

        if (today.minusDays(1).equals(checkin.getLastCheckinDate())) {
            checkin.setContinuousDays(checkin.getContinuousDays() + 1);
        } else {
            checkin.setContinuousDays(1);
        }
        
        checkin.setLastCheckinDate(today);
        fissionUserCheckinMapper.updateById(checkin);

        int reward = 1000;
        if (checkin.getContinuousDays() >= 4) {
            reward = 10000;
        }

        addPoints(userId, appId, reward, 1, "每日签到");

        CheckInResultVO result = new CheckInResultVO();
        result.setRewardPoints(reward);
        result.setContinuousDays(checkin.getContinuousDays());
        return result;
    }

    @Override
    @Transactional
    public VideoTaskResultVO watchVideo(Long userId, String appId) {
        FissionUserTaskLog videoLog = getTaskLog(userId, appId, "task_video", LocalDate.now());
        if (videoLog.getProgressCount() >= 5) {
            throw new RuntimeException("今日看视频次数已达上限");
        }

        videoLog.setProgressCount(videoLog.getProgressCount() + 1);
        if (videoLog.getProgressCount() >= 5) {
            videoLog.setStatus(2); // directly claimed for video
        }
        fissionUserTaskLogMapper.updateById(videoLog);

        int reward = 1144;
        addPoints(userId, appId, reward, 5, "看视频领红包");

        VideoTaskResultVO result = new VideoTaskResultVO();
        result.setRewardPoints(reward);
        result.setCurrentWatchedCount(videoLog.getProgressCount());
        return result;
    }

    @Override
    @Transactional
    public TaskClaimResultVO claimTask(Long userId, String appId, String taskId) {
        FissionUserTaskLog log = getTaskLog(userId, appId, taskId, LocalDate.now());
        if (log.getStatus() == 2) {
            throw new RuntimeException("任务已领取");
        }
        
        // For demonstration, directly mark as completed and claim
        log.setStatus(2);
        fissionUserTaskLogMapper.updateById(log);

        int reward = 1000; // Mock reward logic
        addPoints(userId, appId, reward, 5, "领取日常任务");

        FissionAccount account = fissionAccountMapper.selectById(userId);

        TaskClaimResultVO result = new TaskClaimResultVO();
        result.setRewardPoints(reward);
        result.setNewBalance(account.getAvailablePoints());
        return result;
    }

    private void addPoints(Long userId, String appId, int points, int type, String remark) {
        FissionAccount account = fissionAccountMapper.selectById(userId);
        if (account != null) {
            fissionAccountMapper.update(null, new UpdateWrapper<FissionAccount>()
                .eq("user_id", userId)
                .setSql("available_points = available_points + " + points)
                .setSql("total_points = total_points + " + points));
        } else {
            account = new FissionAccount();
            account.setUserId(userId);
            account.setAppId(appId);
            account.setTotalPoints(points);
            account.setAvailablePoints(points);
            account.setFrozenPoints(0);
            account.setVersion(0);
            fissionAccountMapper.insert(account);
        }
        
        // Record log
        account = fissionAccountMapper.selectById(userId);
        FissionPointLog log = new FissionPointLog();
        log.setAppId(appId);
        log.setUserId(userId);
        log.setChangeType(type);
        log.setPoints(points);
        log.setBalanceAfter(account.getAvailablePoints());
        log.setRemark(remark);
        fissionPointLogMapper.insert(log);
    }
}
