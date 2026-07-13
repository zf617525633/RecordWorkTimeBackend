package com.fission.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fission_withdraw")
public class FissionWithdraw {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String withdrawNo;
    private String appId;
    private Long userId;
    private Integer pointsConsumed;
    private Double amount;
    private Integer payType;
    private String accountName;
    private String accountNo;
    
    /**
     * 状态: 0-待审核, 1-审核通过(打款中), 2-打款成功, 3-已驳回
     */
    private Integer status;
    private Long auditAdminId;
    private LocalDateTime auditTime;
    private String rejectReason;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
