package com.fission.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fission.backend.entity.FissionAccount;

public interface AccountService extends IService<FissionAccount> {

    /**
     * 申请提现，安全扣减积分（防超扣）
     *
     * @param userId         用户ID
     * @param pointsConsumed 消耗的积分
     * @return true=扣减成功, false=扣减失败(如余额不足或并发冲突)
     */
    boolean applyWithdraw(Long userId, Integer pointsConsumed);
}
