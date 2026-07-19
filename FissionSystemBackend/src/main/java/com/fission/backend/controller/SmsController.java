package com.fission.backend.controller;

import com.fission.backend.common.Result;
import com.fission.backend.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public Result<Void> sendCode(@RequestParam String phone) {
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error(500, "手机号码格式不正确");
        }
        
        try {
            boolean success = smsService.sendSmsCode(phone);
            if (success) {
                return Result.success(null);
            }
            return Result.error(500, "发送失败");
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
