package com.fission.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId; // 实际项目中应从Token解析，这里为了演示通过参数传递

    @NotNull(message = "提现消耗积分不能为空")
    @Min(value = 100, message = "最低提现需要100积分")
    private Integer pointsConsumed;
    
    @NotNull(message = "提现金额不能为空")
    private Double amount;
    
    @NotNull(message = "提现方式不能为空 (1-支付宝, 2-微信)")
    private Integer payType;
}
