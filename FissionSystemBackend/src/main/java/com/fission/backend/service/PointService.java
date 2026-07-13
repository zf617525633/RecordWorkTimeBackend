package com.fission.backend.service;

public interface PointService {
    
    /**
     * 每日签到
     *
     * @param appId  应用ID
     * @param userId 用户ID
     * @return 签到获得的积分数，如果已签到抛出异常
     */
    Integer checkIn(String appId, Long userId);
}
