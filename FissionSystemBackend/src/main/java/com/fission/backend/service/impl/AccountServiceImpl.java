package com.fission.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fission.backend.entity.FissionAccount;
import com.fission.backend.mapper.FissionAccountMapper;
import com.fission.backend.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl extends ServiceImpl<FissionAccountMapper, FissionAccount> implements AccountService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyWithdraw(Long userId, Integer pointsConsumed) {
        // 1. 查询当前账户信息
        FissionAccount account = this.getById(userId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }

        // 2. 检查余额是否充足
        if (account.getAvailablePoints() < pointsConsumed) {
            throw new RuntimeException("可用积分余额不足");
        }

        // 3. 构造更新对象，MyBatis-Plus 的乐观锁插件会根据传入的 version 自动在 SQL 加上 WHERE version = ? 并将 version + 1
        FissionAccount updateAccount = new FissionAccount();
        updateAccount.setUserId(account.getUserId());
        updateAccount.setAvailablePoints(account.getAvailablePoints() - pointsConsumed);
        updateAccount.setFrozenPoints(account.getFrozenPoints() + pointsConsumed); // 将提现的积分先放入冻结区
        updateAccount.setVersion(account.getVersion()); // 传入旧版本号触发乐观锁

        // 4. 执行更新
        boolean success = this.updateById(updateAccount);
        if (!success) {
            // 如果 success 为 false，说明在并发情况下 version 被其他线程修改了
            throw new RuntimeException("系统繁忙，请稍后重试 (并发冲突)");
        }

        return true;
    }
}
