package com.fission.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_admin")
public class SysAdmin {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    
    /**
     * 角色：1-超级管理员，2-普通管理员
     */
    private Integer role;
    
    /**
     * 普通管理员绑定的APPID
     */
    private String appId;
    
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
