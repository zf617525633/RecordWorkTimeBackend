package com.fission.backend.controller;

import com.fission.backend.common.Result;
import com.fission.backend.dto.ExchangeTokenRequest;
import com.fission.backend.dto.LoginByPhoneRequest;
import com.fission.backend.dto.LoginByWechatRequest;
import com.fission.backend.dto.SendSmsRequest;
import com.fission.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * [提供给 Native App 的内部接口]
     * App 端在打开 H5 页面前，调用此接口生成一个有效期极短的一次性 Ticket
     */
    @PostMapping("/generateTicket")
    public Result<String> generateTicket(@RequestParam String appId,
                                         @RequestParam String appUid,
                                         @RequestParam(required = false) String deviceFingerprint) {
        // 实际开发中，这里需要对 App 端的请求做强签名校验 (App Secret Sign)，确保是自家 App 发起的请求
        String ticket = authService.generateTicket(appId, appUid, deviceFingerprint);
        return Result.success(ticket);
    }

    /**
     * [提供给 H5 前端的接口]
     * H5 页面拿到 App 注入的 Ticket 后，调用此接口换取长期的 Session Token
     */
    @PostMapping("/exchangeToken")
    public Result<String> exchangeToken(@Validated @RequestBody ExchangeTokenRequest request) {
        try {
            String token = authService.exchangeToken(
                    request.getAppId(), 
                    request.getTicket(), 
                    request.getDeviceFingerprint()
            );
            return Result.success(token);
        } catch (RuntimeException e) {
            return Result.error(401, "授权失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "授权服务内部错误");
        }
    }

    /**
     * 发送手机验证码
     */
    @PostMapping("/sendSmsCode")
    public Result<String> sendSmsCode(@Validated @RequestBody SendSmsRequest request) {
        authService.sendSmsCode(request.getAppId(), request.getPhone());
        return Result.success("验证码发送成功");
    }

    /**
     * 手机号+验证码登录/注册
     */
    @PostMapping("/loginByPhone")
    public Result<String> loginByPhone(@Validated @RequestBody LoginByPhoneRequest request) {
        try {
            String token = authService.loginByPhone(request.getAppId(), request.getPhone(), request.getSmsCode());
            return Result.success(token);
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 微信授权登录/注册
     */
    @PostMapping("/loginByWechat")
    public Result<String> loginByWechat(@Validated @RequestBody LoginByWechatRequest request) {
        try {
            String token = authService.loginByWechat(request.getAppId(), request.getCode());
            return Result.success(token);
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization,
                                 @RequestParam(required = false) String token) {
        // 支持从 Header 或参数中获取 token
        String actualToken = token;
        if (actualToken == null && authorization != null && authorization.startsWith("Bearer ")) {
            actualToken = authorization.substring(7);
        }
        
        if (actualToken != null && !actualToken.isEmpty()) {
            authService.logout(actualToken);
        }
        return Result.success("已退出登录");
    }
}
