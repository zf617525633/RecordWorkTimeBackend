package com.fission.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fission_point_log")
public class FissionPointLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appId;
    private Long userId;
    
    /**
     * 变动类型: 1-签到, 2-邀请奖励, 3-提现扣减, 4-提现驳回退还, 5-后台调整
     */
    private Integer changeType;
    
    private Integer points;
    private Integer balanceAfter;
    private String relatedId;
    private String remark;
    private LocalDateTime createTime;
}
