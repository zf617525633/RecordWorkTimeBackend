package com.fission.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fission_user_task_log")
public class FissionUserTaskLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String appId;
    
    private Long userId;
    
    private String taskId;
    
    private LocalDate recordDate;
    
    private Integer progressCount;
    
    private Integer status; // 0-未完成, 1-待领取, 2-已领取
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
