package com.fission.backend.service.impl;

import com.fission.backend.entity.FissionAccount;
import com.fission.backend.entity.FissionInviteLog;
import com.fission.backend.entity.FissionPointLog;
import com.fission.backend.mapper.FissionAccountMapper;
import com.fission.backend.mapper.FissionInviteLogMapper;
import com.fission.backend.mapper.FissionPointLogMapper;
import com.fission.backend.service.InviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InviteServiceImpl implements InviteService {

    @Autowired
    private FissionInviteLogMapper inviteLogMapper;

    @Autowired
    private FissionAccountMapper accountMapper;

    @Autowired
    private FissionPointLogMapper pointLogMapper;

    private static final int INVITE_REWARD_POINTS = 50; // 假设每邀请一人奖励50积分

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processInviteReward(String appId, Long newUserId, Long inviterId, String sourceChannel) {
        if (inviterId == null) {
            // 没有邀请人，直接返回，或者可以记录一条无邀请人的注册流水用于后续渠道分析
            return;
        }

        // 1. 给邀请人加积分 (重试机制或依赖上游)
        FissionAccount account = accountMapper.selectById(inviterId);
        if (account != null) {
            FissionAccount updateAccount = new FissionAccount();
            updateAccount.setUserId(inviterId);
            updateAccount.setTotalPoints(account.getTotalPoints() + INVITE_REWARD_POINTS);
            updateAccount.setAvailablePoints(account.getAvailablePoints() + INVITE_REWARD_POINTS);
            updateAccount.setVersion(account.getVersion());
            
            int rows = accountMapper.updateById(updateAccount);
            if (rows == 0) {
                throw new RuntimeException("并发冲突，发放邀请奖励失败");
            }

            // 2. 记录积分明细流水
            FissionPointLog pointLog = new FissionPointLog();
            pointLog.setAppId(appId);
            pointLog.setUserId(inviterId);
            pointLog.setChangeType(2); // 2-邀请奖励
            pointLog.setPoints(INVITE_REWARD_POINTS);
            pointLog.setBalanceAfter(account.getAvailablePoints() + INVITE_REWARD_POINTS);
            pointLog.setRelatedId(String.valueOf(newUserId));
            pointLog.setRemark("邀请新用户奖励");
            pointLog.setCreateTime(LocalDateTime.now());
            pointLogMapper.insert(pointLog);
        }

        // 3. 记录邀请关系台账
        FissionInviteLog inviteLog = new FissionInviteLog();
        inviteLog.setAppId(appId);
        inviteLog.setInviterId(inviterId);
        inviteLog.setInviteeId(newUserId);
        inviteLog.setSourceChannel(sourceChannel);
        inviteLog.setRewardPoints(INVITE_REWARD_POINTS);
        inviteLog.setStatus(1); // 1-已发放
        inviteLog.setCreateTime(LocalDateTime.now());
        inviteLogMapper.insert(inviteLog);
    }
}
