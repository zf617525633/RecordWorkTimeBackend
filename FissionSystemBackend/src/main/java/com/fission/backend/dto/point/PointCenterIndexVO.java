package com.fission.backend.dto.point;

import lombok.Data;
import java.util.List;

@Data
public class PointCenterIndexVO {
    private AccountInfo account;
    private NewcomerTaskInfo newcomerTask;
    private VideoTaskInfo videoTask;
    private List<DailyTaskInfo> dailyTasks;

    @Data
    public static class AccountInfo {
        private Integer availablePoints;
        private Integer todayEarnedPoints;
        private String cnyEquivalent;
    }

    @Data
    public static class NewcomerTaskInfo {
        private Integer checkInProgress;
        private Boolean isTodayCheckedIn;
        private Integer withdrawProgress;
        private Boolean isTodayWithdrawn;
    }

    @Data
    public static class VideoTaskInfo {
        private Integer watchedCount;
        private Integer maxLimit;
        private Integer rewardPerVideo;
    }

    @Data
    public static class DailyTaskInfo {
        private String taskId;
        private String title;
        private String subTitle;
        private Integer rewardPoints;
        private String iconType;
        private Integer status; // 0-未完成, 1-待领取, 2-已领取
    }
}
