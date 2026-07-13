package com.fission.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fission_account")
public class FissionAccount {

    /**
     * 关联用户ID (主键)
     */
    @TableId
    private Long userId;

    /**
     * 所属应用app_id
     */
    private String appId;

    /**
     * 历史累计获得总积分
     */
    private Integer totalPoints;

    /**
     * 当前可用积分
     */
    private Integer availablePoints;

    /**
     * 冻结积分(提现审核中)
     */
    private Integer frozenPoints;

    /**
     * 乐观锁版本号,防止高并发超扣
     */
    @Version
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
