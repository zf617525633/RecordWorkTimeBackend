package com.fission.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginByPhoneRequest {
    @NotBlank(message = "appId不能为空")
    private String appId;
    
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    @NotBlank(message = "验证码不能为空")
    private String smsCode;
}
