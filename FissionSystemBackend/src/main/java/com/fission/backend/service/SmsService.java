package com.fission.backend.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.tea.TeaException;
import com.fission.backend.config.SmsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsProperties smsProperties;
    private final StringRedisTemplate redisTemplate;

    private static final String SMS_CACHE_KEY_PREFIX = "sms:captcha:";
    private static final String SMS_LIMIT_KEY_PREFIX = "sms:limit:";

    public boolean sendSmsCode(String phone) {
        String limitKey = SMS_LIMIT_KEY_PREFIX + phone;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            log.warn("短信发送频率过高，手机号：{}", phone);
            throw new RuntimeException("操作频繁，请60秒后再试");
        }

        String code = generateRandomCode();

        try {
            Config config = new Config()
                    .setAccessKeyId(smsProperties.getAccessKeyId())
                    .setAccessKeySecret(smsProperties.getAccessKeySecret());
            // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
            config.endpoint = "dysmsapi.aliyuncs.com";
            
            Client client = new Client(config);

            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(smsProperties.getSignName())
                    .setTemplateCode(smsProperties.getTemplateCode())
                    .setTemplateParam("{\"code\":\"" + code + "\"}");
                    
            RuntimeOptions runtime = new RuntimeOptions();
            
            SendSmsResponse response = client.sendSmsWithOptions(request, runtime);
            if ("OK".equals(response.getBody().getCode())) {
                // 保存验证码到Redis，5分钟有效
                redisTemplate.opsForValue().set(SMS_CACHE_KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
                // 设置60秒的限流
                redisTemplate.opsForValue().set(limitKey, "1", 60, TimeUnit.SECONDS);
                log.info("短信发送成功，手机号：{}，验证码：{}", phone, code);
                return true;
            } else {
                log.error("短信发送失败，错误码：{}，错误信息：{}", response.getBody().getCode(), response.getBody().getMessage());
                throw new RuntimeException("短信发送失败：" + response.getBody().getMessage());
            }
        } catch (TeaException error) {
            log.error("阿里云短信接口调用异常: {}", error.getMessage());
            if (error.getData() != null && error.getData().get("Recommend") != null) {
                log.error("诊断地址: {}", error.getData().get("Recommend"));
            }
            throw new RuntimeException("短信发送异常");
        } catch (Exception _error) {
            log.error("阿里云短信接口调用异常", _error);
            throw new RuntimeException("短信发送异常");
        }
    }

    public boolean verifyCode(String phone, String code) {
        String cacheCode = redisTemplate.opsForValue().get(SMS_CACHE_KEY_PREFIX + phone);
        if (cacheCode != null && cacheCode.equals(code)) {
            // 验证成功后删除验证码，防止重复使用
            redisTemplate.delete(SMS_CACHE_KEY_PREFIX + phone);
            return true;
        }
        return false;
    }

    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
