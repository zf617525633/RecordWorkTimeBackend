package com.fission.backend.controller;

import com.fission.backend.common.Result;
import com.fission.backend.dto.WithdrawRequest;
import com.fission.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * H5端用户申请提现接口
     */
    @PostMapping("/withdraw")
    public Result<String> applyWithdraw(@Validated @RequestBody WithdrawRequest request) {
        try {
            // 在实际项目中，此处还应该插入 fission_withdraw 表的记录，为了聚焦并发锁暂略
            boolean success = accountService.applyWithdraw(request.getUserId(), request.getPointsConsumed());
            if (success) {
                return Result.success("提现申请已提交，等待审核");
            }
            return Result.error(500, "提现失败，请稍后重试");
        } catch (RuntimeException e) {
            // 捕获余额不足或并发锁异常返回给前端
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "系统内部错误");
        }
    }
}
