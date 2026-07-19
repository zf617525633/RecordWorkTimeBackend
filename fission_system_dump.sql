-- MySQL dump 10.13  Distrib 9.2.0, for macos14.7 (x86_64)
--
-- Host: localhost    Database: fission_system
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `fission_account`
--

DROP TABLE IF EXISTS `fission_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fission_account` (
  `user_id` bigint NOT NULL COMMENT 'å…³è”ç”¨æˆ·ID',
  `app_id` varchar(64) NOT NULL COMMENT 'æ‰€å±žåº”ç”¨app_id',
  `total_points` int NOT NULL DEFAULT '0' COMMENT 'åŽ†å²ç´¯è®¡èŽ·å¾—æ€»ç§¯åˆ†',
  `available_points` int NOT NULL DEFAULT '0' COMMENT 'å½“å‰å¯ç”¨ç§¯åˆ†',
  `frozen_points` int NOT NULL DEFAULT '0' COMMENT 'å†»ç»“ç§¯åˆ†(æçŽ°å®¡æ ¸ä¸­)',
  `version` int NOT NULL DEFAULT '0' COMMENT 'ä¹è§‚é”ç‰ˆæœ¬å·,é˜²æ­¢é«˜å¹¶å‘è¶…æ‰£',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ç”¨æˆ·ç§¯åˆ†è´¦æˆ·è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fission_account`
--

LOCK TABLES `fission_account` WRITE;
/*!40000 ALTER TABLE `fission_account` DISABLE KEYS */;
INSERT INTO `fission_account` VALUES (1,'app_fc131d57',4144,4144,0,0,'2026-07-06 22:02:19','2026-07-06 22:02:26');
/*!40000 ALTER TABLE `fission_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fission_invite_log`
--

DROP TABLE IF EXISTS `fission_invite_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fission_invite_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT 'æ‰€å±žåº”ç”¨app_id',
  `inviter_id` bigint NOT NULL COMMENT 'é‚€è¯·äººç”¨æˆ·ID',
  `invitee_id` bigint NOT NULL COMMENT 'è¢«é‚€è¯·äºº(æ–°æ³¨å†Œ)ç”¨æˆ·ID',
  `source_channel` varchar(64) DEFAULT NULL COMMENT 'é‚€è¯·æ¸ é“æ¥æº',
  `reward_points` int NOT NULL DEFAULT '0' COMMENT 'æœ¬æ¬¡é‚€è¯·å¸¦æ¥çš„ç§¯åˆ†å¥–åŠ±',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'çŠ¶æ€: 1-å·²å‘æ”¾å¥–åŠ±, 0-é£ŽæŽ§æ‹¦æˆª/å¾…å‘æ”¾',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_inviter_id` (`inviter_id`),
  KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='é‚€è¯·è£‚å˜è®°å½•è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fission_invite_log`
--

LOCK TABLES `fission_invite_log` WRITE;
/*!40000 ALTER TABLE `fission_invite_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `fission_invite_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fission_point_log`
--

DROP TABLE IF EXISTS `fission_point_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fission_point_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT 'æ‰€å±žåº”ç”¨app_id',
  `user_id` bigint NOT NULL COMMENT 'ç”¨æˆ·ID',
  `change_type` tinyint NOT NULL COMMENT 'å˜åŠ¨ç±»åž‹: 1-ç­¾åˆ°, 2-é‚€è¯·å¥–åŠ±, 3-æçŽ°æ‰£å‡, 4-æçŽ°é©³å›žé€€è¿˜, 5-åŽå°è°ƒæ•´',
  `points` int NOT NULL COMMENT 'å˜åŠ¨ç§¯åˆ†æ•°(æ­£æ•°ä¸ºåŠ ,è´Ÿæ•°ä¸ºå‡)',
  `balance_after` int NOT NULL COMMENT 'å˜åŠ¨åŽè´¦æˆ·å¯ç”¨ä½™é¢',
  `related_id` varchar(64) DEFAULT NULL COMMENT 'å…³è”ä¸šåŠ¡ID(å¦‚æçŽ°å•å·ã€è¢«é‚€è¯·äººID)',
  `remark` varchar(255) DEFAULT NULL COMMENT 'å¤‡æ³¨æè¿°',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_app_id_type` (`app_id`,`change_type`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ç§¯åˆ†å˜åŠ¨æµæ°´è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fission_point_log`
--

LOCK TABLES `fission_point_log` WRITE;
/*!40000 ALTER TABLE `fission_point_log` DISABLE KEYS */;
INSERT INTO `fission_point_log` VALUES (1,'app_fc131d57',1,5,1144,1144,NULL,'看视频领红包','2026-07-06 22:02:19'),(2,'app_fc131d57',1,5,1000,2144,NULL,'领取日常任务','2026-07-06 22:02:22'),(3,'app_fc131d57',1,5,1000,3144,NULL,'领取日常任务','2026-07-06 22:02:24'),(4,'app_fc131d57',1,5,1000,4144,NULL,'领取日常任务','2026-07-06 22:02:26');
/*!40000 ALTER TABLE `fission_point_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fission_user`
--

DROP TABLE IF EXISTS `fission_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fission_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT 'æ‰€å±žåº”ç”¨app_id',
  `app_uid` varchar(128) NOT NULL COMMENT 'ç¬¬ä¸‰æ–¹Appå†…çš„ç”¨æˆ·å”¯ä¸€æ ‡è¯†(å¦‚uidæˆ–openid)',
  `nickname` varchar(128) DEFAULT NULL COMMENT 'ç”¨æˆ·æ˜µç§°',
  `avatar` varchar(255) DEFAULT NULL COMMENT 'ç”¨æˆ·å¤´åƒ',
  `mobile` varchar(20) DEFAULT NULL COMMENT 'æ‰‹æœºå·',
  `invite_code` varchar(32) NOT NULL COMMENT 'è¯¥ç”¨æˆ·çš„ä¸“å±žé‚€è¯·ç ',
  `inviter_id` bigint DEFAULT NULL COMMENT 'é‚€è¯·äººç”¨æˆ·ID(è°é‚€è¯·äº†ä»–)',
  `source_channel` varchar(64) DEFAULT 'organic' COMMENT 'æ³¨å†ŒæŽ¨å¹¿æ¥æº(å¦‚: wechat_moments, group)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'çŠ¶æ€: 1-æ­£å¸¸, 0-å°ç¦',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `wechat_openid` varchar(128) DEFAULT NULL COMMENT 'å¾®ä¿¡OpenID',
  `wechat_unionid` varchar(128) DEFAULT NULL COMMENT 'å¾®ä¿¡UnionID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_uid` (`app_id`,`app_uid`),
  UNIQUE KEY `uk_invite_code` (`invite_code`),
  UNIQUE KEY `uk_wechat_openid` (`app_id`,`wechat_openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Cç«¯ç”¨æˆ·è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fission_user`
--

LOCK TABLES `fission_user` WRITE;
/*!40000 ALTER TABLE `fission_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `fission_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fission_user_checkin`
--

DROP TABLE IF EXISTS `fission_user_checkin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fission_user_checkin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT 'æŽ¥å…¥åº”ç”¨app_id',
  `user_id` bigint NOT NULL COMMENT 'ç”¨æˆ·ID',
  `continuous_days` int NOT NULL DEFAULT '0' COMMENT 'è¿žç»­ç­¾åˆ°å¤©æ•°',
  `last_checkin_date` date DEFAULT NULL COMMENT 'æœ€åŽä¸€æ¬¡ç­¾åˆ°æ—¥æœŸ(æ ¼å¼: YYYY-MM-DD)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ç”¨æˆ·ç­¾åˆ°è®°å½•è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fission_user_checkin`
--

LOCK TABLES `fission_user_checkin` WRITE;
/*!40000 ALTER TABLE `fission_user_checkin` DISABLE KEYS */;
/*!40000 ALTER TABLE `fission_user_checkin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fission_user_task_log`
--

DROP TABLE IF EXISTS `fission_user_task_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fission_user_task_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT 'æŽ¥å…¥åº”ç”¨app_id',
  `user_id` bigint NOT NULL COMMENT 'ç”¨æˆ·ID',
  `task_id` varchar(64) NOT NULL COMMENT 'ä»»åŠ¡å”¯ä¸€æ ‡è¯†(å¦‚: task_video, task_daily_login)',
  `record_date` date NOT NULL COMMENT 'ä»»åŠ¡è®°å½•æ—¥æœŸ(æ ¼å¼: YYYY-MM-DD)',
  `progress_count` int NOT NULL DEFAULT '0' COMMENT 'å½“å‰å®Œæˆè¿›åº¦(å¦‚çœ‹äº†2æ¬¡è§†é¢‘)',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'çŠ¶æ€: 0-æœªå®Œæˆ, 1-å¾…é¢†å–, 2-å·²é¢†å–',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_task_date` (`user_id`,`task_id`,`record_date`),
  KEY `idx_app_id_date` (`app_id`,`record_date`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ç”¨æˆ·æ¯æ—¥ä»»åŠ¡è¿›åº¦è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fission_user_task_log`
--

LOCK TABLES `fission_user_task_log` WRITE;
/*!40000 ALTER TABLE `fission_user_task_log` DISABLE KEYS */;
INSERT INTO `fission_user_task_log` VALUES (1,'app_fc131d57',1,'task_video','2026-07-06',1,0,'2026-07-06 21:50:42','2026-07-06 21:50:42'),(2,'app_fc131d57',1,'task_daily_login','2026-07-06',0,2,'2026-07-06 21:50:42','2026-07-06 21:50:42'),(3,'app_fc131d57',1,'task_lottery_ticket','2026-07-06',0,2,'2026-07-06 21:50:42','2026-07-06 21:50:42'),(4,'app_fc131d57',1,'task_lucky_wheel','2026-07-06',0,2,'2026-07-06 21:50:42','2026-07-06 21:50:42');
/*!40000 ALTER TABLE `fission_user_task_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fission_withdraw`
--

DROP TABLE IF EXISTS `fission_withdraw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fission_withdraw` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `withdraw_no` varchar(64) NOT NULL COMMENT 'æçŽ°æµæ°´å•å·',
  `app_id` varchar(64) NOT NULL COMMENT 'æ‰€å±žåº”ç”¨app_id',
  `user_id` bigint NOT NULL COMMENT 'ç”³è¯·ç”¨æˆ·ID',
  `points_consumed` int NOT NULL COMMENT 'æ¶ˆè€—ç§¯åˆ†',
  `amount` decimal(10,2) NOT NULL COMMENT 'ç”³è¯·æçŽ°é‡‘é¢(å…ƒ)',
  `pay_type` tinyint NOT NULL COMMENT 'æ‰“æ¬¾æ–¹å¼: 1-æ”¯ä»˜å®, 2-å¾®ä¿¡',
  `account_name` varchar(64) NOT NULL COMMENT 'çœŸå®žå§“å',
  `account_no` varchar(128) NOT NULL COMMENT 'æ”¶æ¬¾è´¦å·',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'çŠ¶æ€: 0-å¾…å®¡æ ¸, 1-å®¡æ ¸é€šè¿‡(æ‰“æ¬¾ä¸­), 2-æ‰“æ¬¾æˆåŠŸ, 3-å·²é©³å›ž',
  `audit_admin_id` bigint DEFAULT NULL COMMENT 'å®¡æ ¸ç®¡ç†å‘˜ID',
  `audit_time` datetime DEFAULT NULL COMMENT 'å®¡æ ¸æ—¶é—´',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT 'é©³å›žåŽŸå› ',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_withdraw_no` (`withdraw_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_app_id_status` (`app_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ç§¯åˆ†æçŽ°ç”³è¯·è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fission_withdraw`
--

LOCK TABLES `fission_withdraw` WRITE;
/*!40000 ALTER TABLE `fission_withdraw` DISABLE KEYS */;
/*!40000 ALTER TABLE `fission_withdraw` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_admin`
--

DROP TABLE IF EXISTS `sys_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_admin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL COMMENT 'ç™»å½•è´¦å·',
  `password` varchar(128) NOT NULL COMMENT 'MD5åŠ å¯†å¯†ç ',
  `role` tinyint NOT NULL DEFAULT '2' COMMENT 'è§’è‰²ï¼š1-è¶…çº§ç®¡ç†å‘˜ï¼Œ2-æ™®é€šç®¡ç†å‘˜',
  `app_id` varchar(64) DEFAULT NULL COMMENT 'æ™®é€šç®¡ç†å‘˜ç»‘å®šçš„æŽ¥å…¥åº”ç”¨APPID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'çŠ¶æ€ï¼š0-ç¦ç”¨ï¼Œ1-æ­£å¸¸',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='åŽå°ç®¡ç†å‘˜è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_admin`
--

LOCK TABLES `sys_admin` WRITE;
/*!40000 ALTER TABLE `sys_admin` DISABLE KEYS */;
INSERT INTO `sys_admin` VALUES (1,'zhangfeng','77634958e37a92add27199c3c7206876',1,NULL,1,'2026-07-01 20:52:46','2026-07-01 20:52:46'),(2,'lisi','e10adc3949ba59abbe56e057f20f883e',2,'app_fc131d57',1,'2026-07-01 22:44:42','2026-07-01 22:44:42');
/*!40000 ALTER TABLE `sys_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_app`
--

DROP TABLE IF EXISTS `sys_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_app` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT 'æŽ¥å…¥åº”ç”¨çš„å”¯ä¸€æ ‡è¯†(å¦‚: app_test_01)',
  `app_name` varchar(128) NOT NULL COMMENT 'åº”ç”¨åç§°',
  `app_secret` varchar(128) NOT NULL COMMENT 'åº”ç”¨é‰´æƒç§˜é’¥',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'çŠ¶æ€: 1-å¯ç”¨, 0-ç¦ç”¨',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_id` (`app_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='æŽ¥å…¥åº”ç”¨ç®¡ç†è¡¨(å¤šç§Ÿæˆ·)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_app`
--

LOCK TABLES `sys_app` WRITE;
/*!40000 ALTER TABLE `sys_app` DISABLE KEYS */;
INSERT INTO `sys_app` VALUES (1,'app_fc131d57','我爱记加班','sk_b3229258f425489195e75ad0b5fafffd',1,'2026-07-01 22:44:19','2026-07-01 22:44:19');
/*!40000 ALTER TABLE `sys_app` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_operation_log`
--

DROP TABLE IF EXISTS `sys_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint NOT NULL COMMENT 'æ“ä½œäººID',
  `admin_username` varchar(64) NOT NULL COMMENT 'æ“ä½œäººè´¦å·',
  `module` varchar(64) NOT NULL COMMENT 'æ“ä½œæ¨¡å—',
  `action` varchar(64) NOT NULL COMMENT 'æ“ä½œåŠ¨ä½œ(å¦‚: å®¡æ ¸é€šè¿‡æçŽ°)',
  `ip_address` varchar(64) DEFAULT NULL COMMENT 'æ“ä½œIP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='åŽå°æ“ä½œå®¡è®¡æ—¥å¿—è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_operation_log`
--

LOCK TABLES `sys_operation_log` WRITE;
/*!40000 ALTER TABLE `sys_operation_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(64) NOT NULL COMMENT 'è§’è‰²åç§°',
  `role_code` varchar(64) NOT NULL COMMENT 'è§’è‰²æ ‡è¯†',
  `description` varchar(255) DEFAULT NULL COMMENT 'è§’è‰²æè¿°',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'çŠ¶æ€: 1-æ­£å¸¸, 0-åœç”¨',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='åŽå°è§’è‰²è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-13 21:50:32
