package com.fission.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fission.backend.entity.FissionAccount;
import com.fission.backend.entity.FissionUser;
import com.fission.backend.mapper.FissionAccountMapper;
import com.fission.backend.mapper.FissionUserMapper;
import com.fission.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private FissionUserMapper userMapper;

    @Autowired
    private FissionAccountMapper accountMapper;

    private static final String TICKET_PREFIX = "fission:auth:ticket:";
    private static final String TOKEN_PREFIX = "fission:auth:token:";
    private static final String SMS_PREFIX = "fission:auth:sms:";

    @Override
    public String generateTicket(String appId, String appUid, String deviceFingerprint) {
        String ticket = UUID.randomUUID().toString().replace("-", "");
        String redisKey = TICKET_PREFIX + ticket;

        String payload = appId + ":" + appUid + ":" + (deviceFingerprint != null ? deviceFingerprint : "UNKNOWN");
        redisTemplate.opsForValue().set(redisKey, payload, 30, TimeUnit.SECONDS);

        return ticket;
    }

    @Override
    public String exchangeToken(String appId, String ticket, String deviceFingerprint) {
        String redisKey = TICKET_PREFIX + ticket;

        String payload = redisTemplate.opsForValue().get(redisKey);
        if (payload == null) {
            throw new RuntimeException("Ticket已失效或不存在 (可能已过期)");
        }

        redisTemplate.delete(redisKey);

        String[] parts = payload.split(":");
        String storedAppId = parts[0];
        String storedAppUid = parts[1];
        String storedFingerprint = parts[2];

        if (!storedAppId.equals(appId)) {
            throw new RuntimeException("非法的跨应用换取 Token");
        }
        if (deviceFingerprint != null && !deviceFingerprint.equals("UNKNOWN") && !storedFingerprint.equals(deviceFingerprint)) {
            throw new RuntimeException("环境异常，拒绝授权 (疑似黑产操作)");
        }

        // H5 token: check if user exists, if not, wait we didn't implement auto-creation here yet.
        // For now, we continue to use storedAppUid as user id for demo.
        return generateSessionToken(storedAppUid);
    }

    @Override
    public void sendSmsCode(String appId, String phone) {
        // 生成6位随机验证码
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        
        // 模拟发送短信
        System.out.println("【Mock SMS】向手机号 " + phone + " 发送验证码: " + code);

        // 存入 Redis，有效期5分钟
        String redisKey = SMS_PREFIX + appId + ":" + phone;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);
    }

    @Override
    @Transactional
    public String loginByPhone(String appId, String phone, String smsCode) {
        String redisKey = SMS_PREFIX + appId + ":" + phone;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null || !storedCode.equals(smsCode)) {
            // For mock testing convenience, we can allow a universal code "123456"
            if (!"123456".equals(smsCode)) {
                throw new RuntimeException("验证码错误或已过期");
            }
        } else {
            // 验证通过，删除验证码
            redisTemplate.delete(redisKey);
        }

        // 查找用户
        QueryWrapper<FissionUser> qw = new QueryWrapper<>();
        qw.eq("app_id", appId).eq("mobile", phone);
        FissionUser user = userMapper.selectOne(qw);

        if (user == null) {
            user = createNewUser(appId, phone, "phone", null, null);
        }

        return generateSessionToken(String.valueOf(user.getId()));
    }

    @Override
    @Transactional
    public String loginByWechat(String appId, String code) {
        // 1. 模拟微信 API 请求 (code 换 openid)
        System.out.println("【Mock WeChat】使用 code: " + code + " 换取 openid...");
        String mockOpenid = "ow_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        
        // 如果传入了测试用的固定code，我们返回固定openid方便调试
        if ("test_code".equals(code)) {
            mockOpenid = "ow_test_123456789";
        }

        // 2. 查找用户
        QueryWrapper<FissionUser> qw = new QueryWrapper<>();
        qw.eq("app_id", appId).eq("wechat_openid", mockOpenid);
        FissionUser user = userMapper.selectOne(qw);

        if (user == null) {
            user = createNewUser(appId, "user_" + mockOpenid.substring(0, 8), "wechat", mockOpenid, null);
        }

        return generateSessionToken(String.valueOf(user.getId()));
    }

    @Override
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            String tokenKey = TOKEN_PREFIX + token;
            redisTemplate.delete(tokenKey);
        }
    }

    /**
     * 生成长效 Token
     */
    private String generateSessionToken(String userIdStr) {
        String token = UUID.randomUUID().toString().replace("-", "") + "_" + userIdStr;
        String tokenKey = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, userIdStr, 7, TimeUnit.DAYS);
        return token;
    }

    /**
     * 自动注册新用户并初始化积分账户
     */
    private FissionUser createNewUser(String appId, String appUid, String channel, String wechatOpenid, String wechatUnionid) {
        FissionUser user = new FissionUser();
        user.setAppId(appId);
        user.setAppUid(appUid);
        user.setMobile(channel.equals("phone") ? appUid : null);
        user.setWechatOpenid(wechatOpenid);
        user.setWechatUnionid(wechatUnionid);
        user.setNickname("用户_" + UUID.randomUUID().toString().substring(0, 6));
        user.setSourceChannel(channel);
        user.setStatus(1);
        user.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        userMapper.insert(user);

        FissionAccount account = new FissionAccount();
        account.setUserId(user.getId());
        account.setAppId(appId);
        account.setAvailablePoints(0);
        account.setFrozenPoints(0);
        account.setTotalPoints(0);
        account.setVersion(0);
        accountMapper.insert(account);

        return user;
    }
}
