package com.fission.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExchangeTokenRequest {

    @NotBlank(message = "AppId不能为空")
    private String appId;

    @NotBlank(message = "Ticket不能为空")
    private String ticket;

    /**
     * 前端设备的唯一标识或指纹，用于二次校验防重放和抓包
     */
    private String deviceFingerprint;
}
