package com.fission.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fission.backend.common.Result;
import com.fission.backend.entity.FissionAccount;
import com.fission.backend.entity.FissionPointLog;
import com.fission.backend.entity.FissionWithdraw;
import com.fission.backend.entity.SysAdmin;
import com.fission.backend.mapper.FissionAccountMapper;
import com.fission.backend.mapper.FissionPointLogMapper;
import com.fission.backend.mapper.FissionWithdrawMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/withdraw")
public class AdminWithdrawController {

    @Autowired
    private FissionWithdrawMapper withdrawMapper;

    @Autowired
    private FissionAccountMapper accountMapper;

    @Autowired
    private FissionPointLogMapper pointLogMapper;

    /**
     * B端后台：分页查询提现申请列表
     */
    @GetMapping("/list")
    public Result<Page<FissionWithdraw>> getWithdrawList(
            @RequestParam(required = false) String appId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        // 数据隔离逻辑：普通管理员只能看自己的 appId
        SysAdmin currentAdmin = com.fission.backend.common.AdminContext.get();
        if (currentAdmin != null && currentAdmin.getRole() == 2) {
            appId = currentAdmin.getAppId();
        }
        
        QueryWrapper<FissionWithdraw> queryWrapper = new QueryWrapper<>();
        if (appId != null && !appId.isEmpty()) {
            queryWrapper.eq("app_id", appId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        
        Page<FissionWithdraw> resultPage = withdrawMapper.selectPage(new Page<>(page, size), queryWrapper);
        return Result.success(resultPage);
    }

    /**
     * B端后台：审核提现申请
     */
    @PostMapping("/audit")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> auditWithdraw(
            @RequestParam Long withdrawId,
            @RequestParam Integer action, // 1-通过并打款, 3-驳回
            @RequestParam(required = false) String rejectReason,
            @RequestParam Long adminId) { // 实际应从 Admin Token 获取
            
        FissionWithdraw withdraw = withdrawMapper.selectById(withdrawId);
        if (withdraw == null) {
            return Result.error(404, "提现申请不存在");
        }
        if (withdraw.getStatus() != 0) {
            return Result.error(400, "该提现申请已处理，无法重复审核");
        }

        // 乐观锁更新账户相关逻辑
        FissionAccount account = accountMapper.selectById(withdraw.getUserId());
        
        if (action == 1) { // 审核通过
            // 提现通过，扣除冻结的积分，打款状态更新
            withdraw.setStatus(1); // 1-审核通过，可进入实际打款流程
            
            FissionAccount updateAcc = new FissionAccount();
            updateAcc.setUserId(account.getUserId());
            updateAcc.setFrozenPoints(account.getFrozenPoints() - withdraw.getPointsConsumed());
            updateAcc.setVersion(account.getVersion());
            int rows = accountMapper.updateById(updateAcc);
            if (rows == 0) {
                throw new RuntimeException("并发冲突");
            }
            
            // 记录流水：实际提现扣减
            FissionPointLog pointLog = new FissionPointLog();
            pointLog.setAppId(withdraw.getAppId());
            pointLog.setUserId(withdraw.getUserId());
            pointLog.setChangeType(3); // 3-提现扣减
            pointLog.setPoints(-withdraw.getPointsConsumed());
            pointLog.setBalanceAfter(account.getAvailablePoints());
            pointLog.setRelatedId(withdraw.getWithdrawNo());
            pointLog.setRemark("提现审核通过");
            pointLog.setCreateTime(LocalDateTime.now());
            pointLogMapper.insert(pointLog);
            
        } else if (action == 3) { // 驳回
            withdraw.setStatus(3);
            withdraw.setRejectReason(rejectReason);
            
            // 驳回，把冻结的积分退回到可用积分
            FissionAccount updateAcc = new FissionAccount();
            updateAcc.setUserId(account.getUserId());
            updateAcc.setAvailablePoints(account.getAvailablePoints() + withdraw.getPointsConsumed());
            updateAcc.setFrozenPoints(account.getFrozenPoints() - withdraw.getPointsConsumed());
            updateAcc.setVersion(account.getVersion());
            int rows = accountMapper.updateById(updateAcc);
            if (rows == 0) {
                throw new RuntimeException("并发冲突");
            }
            
            // 记录流水：提现驳回退还
            FissionPointLog pointLog = new FissionPointLog();
            pointLog.setAppId(withdraw.getAppId());
            pointLog.setUserId(withdraw.getUserId());
            pointLog.setChangeType(4); // 4-驳回退还
            pointLog.setPoints(withdraw.getPointsConsumed());
            pointLog.setBalanceAfter(account.getAvailablePoints() + withdraw.getPointsConsumed());
            pointLog.setRelatedId(withdraw.getWithdrawNo());
            pointLog.setRemark("提现驳回退还: " + rejectReason);
            pointLog.setCreateTime(LocalDateTime.now());
            pointLogMapper.insert(pointLog);
        } else {
            return Result.error(400, "未知的审核动作");
        }

        withdraw.setAuditAdminId(adminId);
        withdraw.setAuditTime(LocalDateTime.now());
        withdrawMapper.updateById(withdraw);
        
        return Result.success("审核操作成功");
    }
}
