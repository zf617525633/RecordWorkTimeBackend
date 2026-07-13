package com.fission.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fission_user_checkin")
public class FissionUserCheckin {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String appId;
    
    private Long userId;
    
    private Integer continuousDays;
    
    private LocalDate lastCheckinDate;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
