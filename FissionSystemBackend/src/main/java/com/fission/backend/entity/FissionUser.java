package com.fission.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fission_user")
public class FissionUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appId;
    private String appUid;
    private String nickname;
    private String avatar;
    private String mobile;
    private String inviteCode;
    private Long inviterId;
    private String sourceChannel;
    private Integer status;
    private String wechatOpenid;
    private String wechatUnionid;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
