package com.fission.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginByWechatRequest {
    @NotBlank(message = "appId不能为空")
    private String appId;
    
    @NotBlank(message = "微信授权code不能为空")
    private String code;
}
