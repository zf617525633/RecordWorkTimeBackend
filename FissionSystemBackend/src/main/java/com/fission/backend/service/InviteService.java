package com.fission.backend.service;

public interface InviteService {

    /**
     * 处理新用户注册后的邀请发分逻辑
     *
     * @param appId         应用ID
     * @param newUserId     新注册的用户ID
     * @param inviterId     邀请人ID (可能为空)
     * @param sourceChannel 推广渠道
     */
    void processInviteReward(String appId, Long newUserId, Long inviterId, String sourceChannel);
}
