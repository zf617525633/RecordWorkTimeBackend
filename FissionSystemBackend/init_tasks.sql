CREATE TABLE IF NOT EXISTS `fission_user_checkin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT '接入应用app_id',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `continuous_days` int(11) NOT NULL DEFAULT '0' COMMENT '连续签到天数',
  `last_checkin_date` date DEFAULT NULL COMMENT '最后一次签到日期(格式: YYYY-MM-DD)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户签到记录表';

CREATE TABLE IF NOT EXISTS `fission_user_task_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT '接入应用app_id',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `task_id` varchar(64) NOT NULL COMMENT '任务唯一标识(如: task_video, task_daily_login)',
  `record_date` date NOT NULL COMMENT '任务记录日期(格式: YYYY-MM-DD)',
  `progress_count` int(11) NOT NULL DEFAULT '0' COMMENT '当前完成进度(如看了2次视频)',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态: 0-未完成, 1-待领取, 2-已领取',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `record_date`),
  KEY `idx_app_id_date` (`app_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户每日任务进度表';
