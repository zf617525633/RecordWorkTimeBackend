package com.fission.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fission_invite_log")
public class FissionInviteLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appId;
    private Long inviterId;
    private Long inviteeId;
    private String sourceChannel;
    private Integer rewardPoints;
    
    /**
     * 状态: 1-已发放奖励, 0-风控拦截/待发放
     */
    private Integer status;
    private LocalDateTime createTime;
}
