package com.fission.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fission.backend.entity.FissionAccount;
import com.fission.backend.entity.FissionPointLog;
import com.fission.backend.mapper.FissionAccountMapper;
import com.fission.backend.mapper.FissionPointLogMapper;
import com.fission.backend.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private FissionAccountMapper accountMapper;

    @Autowired
    private FissionPointLogMapper pointLogMapper;

    private static final int DAILY_CHECKIN_POINTS = 10; // 假设每次签到固定给10积分，实际应从sys_app配置中读取

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer checkIn(String appId, Long userId) {
        String today = LocalDate.now().toString();
        // Redis 防刷/防并发签到 Key: fission:checkin:{appId}:{userId}:{yyyy-MM-dd}
        String redisKey = String.format("fission:checkin:%s:%s:%s", appId, userId, today);
        
        // 1. 利用 Redis 的 setIfAbsent (SETNX) 防止同一天重复签到及高并发下的并发请求
        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", Duration.ofDays(1));
        if (Boolean.FALSE.equals(success)) {
            throw new RuntimeException("今日已签到，请勿重复操作");
        }

        try {
            // 2. 更新账户积分 (乐观锁保证安全性)
            FissionAccount account = accountMapper.selectById(userId);
            if (account == null) {
                // 如果是第一次获得积分，可能需要初始化账户，这里简化处理直接抛异常或初始化
                throw new RuntimeException("账户不存在");
            }
            
            FissionAccount updateAccount = new FissionAccount();
            updateAccount.setUserId(userId);
            updateAccount.setTotalPoints(account.getTotalPoints() + DAILY_CHECKIN_POINTS);
            updateAccount.setAvailablePoints(account.getAvailablePoints() + DAILY_CHECKIN_POINTS);
            updateAccount.setVersion(account.getVersion());
            
            int rows = accountMapper.updateById(updateAccount);
            if (rows == 0) {
                throw new RuntimeException("系统繁忙，请重试 (并发冲突)");
            }

            // 3. 记录积分流水
            FissionPointLog log = new FissionPointLog();
            log.setAppId(appId);
            log.setUserId(userId);
            log.setChangeType(1); // 1-签到
            log.setPoints(DAILY_CHECKIN_POINTS);
            log.setBalanceAfter(account.getAvailablePoints() + DAILY_CHECKIN_POINTS);
            log.setRemark("每日签到奖励");
            log.setCreateTime(LocalDateTime.now());
            pointLogMapper.insert(log);

            return DAILY_CHECKIN_POINTS;

        } catch (Exception e) {
            // 如果数据库操作失败，需要删除 Redis 防重 Key，以便用户可以重试
            redisTemplate.delete(redisKey);
            throw e;
        }
    }
}
