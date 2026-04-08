-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: heritage_db
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` varchar(50) DEFAULT 'Admin_01' COMMENT '操作人ID (目前写死为超级管理员)',
  `action_type` varchar(50) NOT NULL COMMENT '动作类型 (如: 归档, 恢复, 新增, 删除)',
  `resource_id` bigint DEFAULT NULL COMMENT '受影响的资源ID',
  `changes_summary` text COMMENT '变更详情 (如: 状态从 Active 变为 Archived)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作发生时间 (不可修改)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统审计日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,'Admin_01','CREATE',4,'Created new resource: 111','2026-03-29 18:06:18'),(2,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 18:22:12'),(3,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 18:22:12'),(4,'Admin_01','APPROVE/RESTORE',3,'Status changed from 3 to 1','2026-03-29 18:48:30'),(5,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 18:48:51'),(6,'Admin_01','CREATE',5,'Created new resource: 222','2026-03-29 19:14:04'),(7,'Admin_01','APPROVE/RESTORE',5,'Status changed from 0 to 1','2026-03-29 19:14:21'),(8,'Admin_01','APPROVE/RESTORE',3,'Status changed from 3 to 1','2026-03-29 19:14:38'),(9,'Admin_01','APPROVE/RESTORE',1,'Status changed from 3 to 1','2026-03-29 19:14:40'),(10,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 19:14:48'),(11,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 19:14:48'),(12,'System (Auto-Archive Policy)','AUTO_ARCHIVE',5,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 19:16:18'),(13,'Admin_01','APPROVE/RESTORE',1,'Status changed from 3 to 1','2026-03-29 19:25:56'),(14,'Admin_01','APPROVE/RESTORE',3,'Status changed from 3 to 1','2026-03-29 19:26:00'),(15,'Admin_01','CREATE',6,'Created new resource: 333','2026-03-29 19:38:55'),(16,'Admin_01','UPDATE_STATUS',6,'Status updated to 1','2026-03-29 19:39:14'),(17,'Admin_01','CREATE',7,'Created new resource: 444','2026-03-29 19:45:28'),(18,'Admin_01','UPDATE_STATUS',7,'Status updated to 1','2026-03-29 19:45:54'),(19,'Admin_01','ARCHIVE',3,'Status updated to 3','2026-03-30 11:36:26'),(20,'System','CONTRIBUTOR_SUBMIT',1,'User admin Submitted for review','2026-03-30 14:23:17'),(21,'System','APPROVE',1,'Status updated to 1','2026-03-30 14:23:31'),(22,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule: Archived resource inactive for > 1 day.','2026-04-01 14:38:22'),(23,'System','APPROVE',1,'Status updated to 1','2026-04-01 14:42:56'),(24,'System','CONTRIBUTOR_SUBMIT',2,'User admin Saved as draft','2026-04-01 14:44:36'),(25,'System','CONTRIBUTOR_EDIT',2,'Resource updated and status set to 0','2026-04-01 14:44:43'),(26,'System','APPROVE',2,'Status updated to 1','2026-04-01 14:46:13'),(27,'System','CONTRIBUTOR_SUBMIT',3,'User admin Saved as draft','2026-04-01 15:09:09'),(28,'System','CONTRIBUTOR_EDIT',3,'Resource updated and status set to 0','2026-04-01 15:09:13'),(29,'System','APPROVE',3,'Status updated to 1','2026-04-01 15:10:01'),(30,'System','ARCHIVE',1,'Status updated to 3','2026-04-01 15:10:30'),(31,'Admin_01','ROLE_CHANGE',6,'Admin manually changed role of new from VIEWER to CONTRIBUTOR','2026-04-01 15:13:43'),(32,'Admin_01','ROLE_CHANGE',6,'Admin manually changed role of new from CONTRIBUTOR to VIEWER','2026-04-01 15:13:45'),(33,'System (Auto-Archive Policy)','AUTO_ARCHIVE',2,'System rule: Archived resource inactive for > 1 day.','2026-04-03 20:45:53'),(34,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule: Archived resource inactive for > 1 day.','2026-04-03 20:45:53'),(35,'System','APPROVE',1,'Status updated to 1','2026-04-03 20:47:49'),(36,'System','APPROVE',2,'Status updated to 1','2026-04-03 20:47:50'),(37,'System','APPROVE',3,'Status updated to 1','2026-04-03 20:47:52');
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` varchar(500) DEFAULT NULL COMMENT '分类描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统分类主数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'非物质文化遗产','传统技艺、表演艺术等','2026-03-30 14:46:02','2026-03-30 14:46:02'),(2,'历史古迹/建筑','历史遗留的建筑、遗址','2026-03-30 14:46:09','2026-03-30 14:46:09'),(3,'民俗活动','地方传统的节日、庆典活动','2026-03-30 14:46:12','2026-03-30 14:46:12'),(4,'传统技艺/手工艺','世代相传的手工制作技艺','2026-03-30 14:46:14','2026-03-30 14:46:14'),(5,'口头传统/神话','神话、传说、故事等','2026-03-30 14:46:16','2026-03-30 14:46:16');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resource_id` bigint NOT NULL COMMENT '所属资源ID',
  `username` varchar(50) NOT NULL COMMENT '评论发布者',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID (0表示这是一级评论)',
  `reply_to` varchar(50) DEFAULT NULL COMMENT '回复目标用户的账号',
  `likes` int DEFAULT '0' COMMENT '点赞数',
  `dislikes` int DEFAULT '0' COMMENT '踩/反对数',
  `is_deleted` tinyint DEFAULT '0' COMMENT '软删除标记：0-正常, 1-已删除(显示占位符)',
  `is_edited` tinyint DEFAULT '0' COMMENT '是否编辑过：0-未编辑, 1-已编辑',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资源评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,1,'admin','这是沙发！',0,NULL,2,2,0,1,'2026-03-30 14:05:53','2026-03-30 14:05:59'),(2,1,'admin','111',1,'admin',1,0,0,0,'2026-03-30 14:06:05','2026-03-30 14:06:05'),(3,2,'admin','111！',0,NULL,1,0,1,1,'2026-04-01 14:46:38','2026-04-01 14:46:51'),(4,2,'admin','111\n',3,'admin',0,0,0,0,'2026-04-01 14:46:45','2026-04-01 14:46:45'),(5,3,'admin','11',0,NULL,0,0,0,0,'2026-04-01 15:10:17','2026-04-01 15:10:17'),(6,2,'admin','11111',0,NULL,1,0,0,0,'2026-04-03 20:48:22','2026-04-03 20:48:22'),(7,2,'admin','1111\n',6,'admin',0,0,1,1,'2026-04-03 20:48:37','2026-04-03 20:48:50');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_report`
--

DROP TABLE IF EXISTS `comment_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL COMMENT '被举报的评论ID',
  `reporter_username` varchar(50) NOT NULL COMMENT '举报人账号',
  `reason` varchar(50) NOT NULL COMMENT '举报原因',
  `details` varchar(500) DEFAULT NULL COMMENT '补充说明',
  `status` int DEFAULT '0' COMMENT '处理状态：0-待处理, 1-举报成立, 2-举报驳回',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论举报记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_report`
--

LOCK TABLES `comment_report` WRITE;
/*!40000 ALTER TABLE `comment_report` DISABLE KEYS */;
INSERT INTO `comment_report` VALUES (1,3,'admin','Spam','',1,'2026-04-01 15:15:33','2026-04-01 15:15:56'),(2,7,'admin','Spam','',1,'2026-04-03 20:48:44','2026-04-03 20:49:07');
/*!40000 ALTER TABLE `comment_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `heritage_resource`
--

DROP TABLE IF EXISTS `heritage_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `heritage_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '资源标题',
  `description` text NOT NULL COMMENT '详细描述',
  `category` varchar(50) NOT NULL COMMENT '所属分类',
  `thumbnail` varchar(500) DEFAULT NULL COMMENT '封面图 URL',
  `media_url` varchar(500) DEFAULT NULL COMMENT '附件/视频 URL',
  `tags` varchar(200) DEFAULT NULL COMMENT '相关标签',
  `location` varchar(200) DEFAULT NULL COMMENT '地理位置',
  `contributor_username` varchar(50) NOT NULL COMMENT '贡献者账号',
  `status` int DEFAULT '0' COMMENT '状态: -1草稿, 0待审, 1已发布, 2驳回, 3归档, 4撤回',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='遗产资源主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `heritage_resource`
--

LOCK TABLES `heritage_resource` WRITE;
/*!40000 ALTER TABLE `heritage_resource` DISABLE KEYS */;
INSERT INTO `heritage_resource` VALUES (1,'111','111','非物质文化遗产','https://ts3.tc.mm.bing.net/th?id=ORMS.097b22616907f314f7ad96628c6fd598&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1.25&p=0','','','111','admin',1,'2026-03-30 14:23:17','2026-04-03 20:47:49'),(2,'香蕉','111','民俗活动','https://ts4.tc.mm.bing.net/th?id=ORMS.9a97e65f0798dd8ddf3bdd1a6104da4d&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1.5&p=0','','','','admin',1,'2026-04-01 14:44:36','2026-04-03 20:47:50'),(3,'111','111','非物质文化遗产','https://ts4.tc.mm.bing.net/th?id=ORMS.e45eb21cab0894129ef7237b828d5e6e&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1.5&p=0','','','','admin',1,'2026-04-01 15:09:09','2026-04-03 20:47:52');
/*!40000 ALTER TABLE `heritage_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `receiver_username` varchar(50) NOT NULL,
  `content` text NOT NULL,
  `is_read` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `type` varchar(20) DEFAULT 'SYSTEM' COMMENT '通知类型: REVIEW, COMMENT, SYSTEM',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,'guest01','您的遗产资源《333》审核状态更新为：【已通过】',1,'2026-03-29 19:39:14','SYSTEM'),(2,'guest01','您的遗产资源《444》审核状态更新为：【已通过】',1,'2026-03-29 19:45:54','SYSTEM'),(3,'guest01','关于《测试剪纸》的审核结果：【已归档】资源已被管理员归档。',0,'2026-03-30 11:36:26','SYSTEM'),(4,'admin','🔔 用户 admin 回复了您的评论！内容：111',1,'2026-03-30 14:06:05','SYSTEM'),(5,'admin','关于《111》的审核结果：【已通过】您的资源已公开发布！',1,'2026-03-30 14:23:31','SYSTEM'),(6,'admin','关于《111》的审核结果：【已通过】您的资源已公开发布！',1,'2026-04-01 14:42:56','SYSTEM'),(7,'admin','关于《香蕉》的审核结果：【已通过】您的资源已公开发布！',1,'2026-04-01 14:46:13','SYSTEM'),(8,'admin','🔔 用户 admin 回复了您的评论！内容：111\n',0,'2026-04-01 14:46:45','SYSTEM'),(9,'admin','关于《111》的审核结果：【已通过】您的资源已公开发布！',0,'2026-04-01 15:10:01','SYSTEM'),(10,'admin','关于《111》的审核结果：【已归档】资源已被下架。',0,'2026-04-01 15:10:30','SYSTEM'),(11,'admin','🛡️ 【风控中心】您举报的评论 已被系统核实并清理。 感谢您的反馈！',0,'2026-04-01 15:15:56','SYSTEM'),(12,'admin','⚠️ 【系统警告】您的一条评论因违反社区规定被多名用户举报，现已被系统强制清理。请规范发言！',0,'2026-04-01 15:15:56','SYSTEM'),(13,'admin','关于《111》的审核结果：【已通过】您的资源已公开发布！',0,'2026-04-03 20:47:49','SYSTEM'),(14,'admin','关于《香蕉》的审核结果：【已通过】您的资源已公开发布！',0,'2026-04-03 20:47:50','SYSTEM'),(15,'admin','关于《111》的审核结果：【已通过】您的资源已公开发布！',0,'2026-04-03 20:47:52','SYSTEM'),(16,'admin','🔔 用户 admin 回复了您的评论！内容：11111',0,'2026-04-03 20:48:37','SYSTEM'),(17,'admin','🛡️ 【风控中心】您举报的评论 已被系统核实并清理。 感谢您的反馈！',0,'2026-04-03 20:49:07','SYSTEM'),(18,'admin','⚠️ 【系统警告】您的一条评论因违反社区规定被多名用户举报，现已被系统强制清理。请规范发言！',0,'2026-04-03 20:49:07','SYSTEM');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_application`
--

DROP TABLE IF EXISTS `role_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_application` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '申请人账号',
  `reason` varchar(500) NOT NULL COMMENT '申请理由 (Brief statement)',
  `status` int DEFAULT '0' COMMENT '状态: 0-待审核(Pending), 1-已通过(Approved), 2-已驳回(Rejected)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色晋升申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_application`
--

LOCK TABLES `role_application` WRITE;
/*!40000 ALTER TABLE `role_application` DISABLE KEYS */;
INSERT INTO `role_application` VALUES (1,'111','111',0,'2026-03-30 13:40:51','2026-03-30 13:40:51');
/*!40000 ALTER TABLE `role_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'CONTRIBUTOR',
  `real_name` varchar(50) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL链接',
  `email` varchar(100) DEFAULT NULL COMMENT '注册邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '个人昵称',
  `notify_review` int DEFAULT '1' COMMENT '接收审核通知',
  `notify_comment` int DEFAULT '1' COMMENT '接收评论通知',
  `notify_system` int DEFAULT '1' COMMENT '接收系统通知',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456','ADMIN','超级管理员','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-29 18:37:31',NULL,NULL,'管理员111',1,1,1),(2,'guest01','123456','CONTRIBUTOR','非遗传承人张师傅','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-29 18:37:31',NULL,NULL,NULL,1,1,1),(3,'liu','111111','CONTRIBUTOR','liu','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-29 20:19:54',NULL,NULL,NULL,1,1,1),(4,'test1','96e79218965eb72c92a549dd5a330112','CONTRIBUTOR','test1','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-30 11:55:38',NULL,'test1@qq.com',NULL,1,1,1),(5,'111','96e79218965eb72c92a549dd5a330112','VIEWER','111','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-30 13:40:26',NULL,'111@qq.com',NULL,1,1,1),(6,'new','e10adc3949ba59abbe56e057f20f883e','VIEWER','new','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-04-01 15:13:07',NULL,'new@qq.com',NULL,1,1,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_favorite`
--

DROP TABLE IF EXISTS `user_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '收藏用户的账号',
  `resource_id` bigint NOT NULL COMMENT '被收藏的资源ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fav_user_res` (`username`,`resource_id`) COMMENT '防止重复收藏'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_favorite`
--

LOCK TABLES `user_favorite` WRITE;
/*!40000 ALTER TABLE `user_favorite` DISABLE KEYS */;
INSERT INTO `user_favorite` VALUES (1,'test1',1,'2026-03-30 12:31:24'),(2,'admin',2,'2026-04-01 14:46:20'),(3,'admin',3,'2026-04-01 15:10:46');
/*!40000 ALTER TABLE `user_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_like`
--

DROP TABLE IF EXISTS `user_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '点赞用户的账号',
  `resource_id` bigint NOT NULL COMMENT '被点赞的资源ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_like_user_res` (`username`,`resource_id`) COMMENT '防止重复点赞'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_like`
--

LOCK TABLES `user_like` WRITE;
/*!40000 ALTER TABLE `user_like` DISABLE KEYS */;
INSERT INTO `user_like` VALUES (2,'admin',3,'2026-04-01 15:10:13'),(3,'admin',2,'2026-04-03 20:48:17');
/*!40000 ALTER TABLE `user_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'heritage_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-08 14:45:39
