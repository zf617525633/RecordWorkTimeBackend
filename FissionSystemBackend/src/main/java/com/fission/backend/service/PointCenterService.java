package com.fission.backend.service;

import com.fission.backend.dto.point.CheckInResultVO;
import com.fission.backend.dto.point.PointCenterIndexVO;
import com.fission.backend.dto.point.TaskClaimResultVO;
import com.fission.backend.dto.point.VideoTaskResultVO;

public interface PointCenterService {
    
    PointCenterIndexVO getHomeData(Long userId, String appId);
    
    CheckInResultVO checkIn(Long userId, String appId);
    
    VideoTaskResultVO watchVideo(Long userId, String appId);
    
    TaskClaimResultVO claimTask(Long userId, String appId, String taskId);
}
