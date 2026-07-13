package com.fission.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_app")
public class SysApp {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appId;
    private String appName;
    private String appSecret;
    private Integer status;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
