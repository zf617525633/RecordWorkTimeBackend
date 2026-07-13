package com.fission.backend.service;

public interface AuthService {

    /**
     * 给 Native App 生成一次性 Ticket (短时效)
     *
     * @param appId             接入应用的唯一标识
     * @param appUid            App内的用户ID
     * @param deviceFingerprint 设备指纹
     * @return 生成的 Ticket 字符串
     */
    String generateTicket(String appId, String appUid, String deviceFingerprint);

    /**
     * H5 拿 Ticket 换取真正用于业务交互的 Session Token
     *
     * @param appId             接入应用的唯一标识
     * @param ticket            一次性凭证
     * @param deviceFingerprint 请求时的设备指纹（需与生成时校验一致）
     * @return 长期有效的 H5 Token
     */
    String exchangeToken(String appId, String ticket, String deviceFingerprint);

    /**
     * 发送短信验证码
     */
    void sendSmsCode(String appId, String phone);

    /**
     * 手机号验证码登录
     */
    String loginByPhone(String appId, String phone, String smsCode);

    /**
     * 微信登录
     */
    String loginByWechat(String appId, String code);

    /**
     * 退出登录
     */
    void logout(String token);
}
