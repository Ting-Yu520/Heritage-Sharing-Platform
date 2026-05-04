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
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Log ID',
  `user_id` varchar(50) DEFAULT 'Admin_01' COMMENT 'Operator ID (currently hardcoded as super admin)',
  `action_type` varchar(50) NOT NULL COMMENT 'Action type (e.g., archive, restore, create, delete)',
  `resource_id` bigint DEFAULT NULL COMMENT 'Affected resource ID',
  `changes_summary` text COMMENT 'Change summary (e.g., status from Active to Archived)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Action time (immutable)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='System audit log table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,'Admin_01','CREATE',4,'Created new resource: 111','2026-03-29 18:06:18'),(2,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 18:22:12'),(3,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 18:22:12'),(4,'Admin_01','APPROVE/RESTORE',3,'Status changed from 3 to 1','2026-03-29 18:48:30'),(5,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 18:48:51'),(6,'Admin_01','CREATE',5,'Created new resource: 222','2026-03-29 19:14:04'),(7,'Admin_01','APPROVE/RESTORE',5,'Status changed from 0 to 1','2026-03-29 19:14:21'),(8,'Admin_01','APPROVE/RESTORE',3,'Status changed from 3 to 1','2026-03-29 19:14:38'),(9,'Admin_01','APPROVE/RESTORE',1,'Status changed from 3 to 1','2026-03-29 19:14:40'),(10,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 19:14:48'),(11,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 19:14:48'),(12,'System (Auto-Archive Policy)','AUTO_ARCHIVE',5,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 19:16:18'),(13,'Admin_01','APPROVE/RESTORE',1,'Status changed from 3 to 1','2026-03-29 19:25:56'),(14,'Admin_01','APPROVE/RESTORE',3,'Status changed from 3 to 1','2026-03-29 19:26:00'),(15,'Admin_01','CREATE',6,'Created new resource: 333','2026-03-29 19:38:55'),(16,'Admin_01','UPDATE_STATUS',6,'Status updated to 1','2026-03-29 19:39:14'),(17,'Admin_01','CREATE',7,'Created new resource: 444','2026-03-29 19:45:28'),(18,'Admin_01','UPDATE_STATUS',7,'Status updated to 1','2026-03-29 19:45:54'),(19,'Admin_01','ARCHIVE',3,'Status updated to 3','2026-03-30 11:36:26'),(20,'System','CONTRIBUTOR_SUBMIT',1,'User admin Submitted for review','2026-03-30 14:23:17'),(21,'System','APPROVE',1,'Status updated to 1','2026-03-30 14:23:31'),(22,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule: Archived resource inactive for > 1 day.','2026-04-01 14:38:22'),(23,'System','APPROVE',1,'Status updated to 1','2026-04-01 14:42:56'),(24,'System','CONTRIBUTOR_SUBMIT',2,'User admin Saved as draft','2026-04-01 14:44:36'),(25,'System','CONTRIBUTOR_EDIT',2,'Resource updated and status set to 0','2026-04-01 14:44:43'),(26,'System','APPROVE',2,'Status updated to 1','2026-04-01 14:46:13'),(27,'System','CONTRIBUTOR_SUBMIT',3,'User admin Saved as draft','2026-04-01 15:09:09'),(28,'System','CONTRIBUTOR_EDIT',3,'Resource updated and status set to 0','2026-04-01 15:09:13'),(29,'System','APPROVE',3,'Status updated to 1','2026-04-01 15:10:01'),(30,'System','ARCHIVE',1,'Status updated to 3','2026-04-01 15:10:30'),(31,'Admin_01','ROLE_CHANGE',6,'Admin manually changed role of new from VIEWER to CONTRIBUTOR','2026-04-01 15:13:43'),(32,'Admin_01','ROLE_CHANGE',6,'Admin manually changed role of new from CONTRIBUTOR to VIEWER','2026-04-01 15:13:45'),(33,'System (Auto-Archive Policy)','AUTO_ARCHIVE',2,'System rule: Archived resource inactive for > 1 day.','2026-04-03 20:45:53'),(34,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule: Archived resource inactive for > 1 day.','2026-04-03 20:45:53'),(35,'System','APPROVE',1,'Status updated to 1','2026-04-03 20:47:49'),(36,'System','APPROVE',2,'Status updated to 1','2026-04-03 20:47:50'),(37,'System','APPROVE',3,'Status updated to 1','2026-04-03 20:47:52'),(38,'System','CONTRIBUTOR_SUBMIT',4,'User admin Saved as draft','2026-04-11 16:50:50'),(39,'System','CONTRIBUTOR_EDIT',4,'Resource updated and status set to 0','2026-04-11 16:50:58'),(40,'System','APPROVE',4,'Status updated to 1','2026-04-11 16:51:33'),(41,'System','ARCHIVE',4,'Status updated to 3','2026-04-11 16:52:07'),(42,'System','APPROVE',4,'Status updated to 1','2026-04-11 16:52:14'),(43,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule: Archived resource inactive for > 1 day.','2026-04-26 22:05:00'),(44,'System (Auto-Archive Policy)','AUTO_ARCHIVE',2,'System rule: Archived resource inactive for > 1 day.','2026-04-26 22:05:00'),(45,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule: Archived resource inactive for > 1 day.','2026-04-26 22:05:00'),(46,'System (Auto-Archive Policy)','AUTO_ARCHIVE',4,'System rule: Archived resource inactive for > 1 day.','2026-04-26 22:05:00'),(47,'System','APPROVE',1,'Status updated to 1','2026-04-26 23:15:41'),(48,'System','APPROVE',2,'Status updated to 1','2026-04-26 23:15:44'),(49,'System','CONTRIBUTOR_DELETE',3,'Resource titled \'111\' deleted by contributor','2026-04-26 23:28:32'),(50,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule: Archived resource inactive for > 1 day.','2026-04-28 00:20:47'),(51,'System (Auto-Archive Policy)','AUTO_ARCHIVE',2,'System rule: Archived resource inactive for > 1 day.','2026-04-28 00:20:47'),(52,'admin','APPROVE',1,'Status updated to 1','2026-04-28 00:24:00'),(53,'admin','APPROVE',2,'Status updated to 1','2026-04-28 00:24:02'),(54,'admin','APPROVE',4,'Status updated to 1','2026-04-28 00:24:03');
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
  `name` varchar(100) NOT NULL COMMENT 'Category name',
  `description` varchar(500) DEFAULT NULL COMMENT 'Category description',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='System category master table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Intangible Cultural Heritage','Traditional crafts, performing arts, etc.','2026-03-30 14:46:02','2026-03-30 14:46:02'),(2,'Historic Sites/Architecture','Historic buildings and sites','2026-03-30 14:46:09','2026-03-30 14:46:09'),(3,'Folk Activities','Local traditional festivals and celebrations','2026-03-30 14:46:12','2026-03-30 14:46:12'),(4,'Traditional Crafts/Handicrafts','Handcraft skills passed down through generations','2026-03-30 14:46:14','2026-03-30 14:46:14'),(5,'Oral Traditions/Myths','Myths, legends, stories, etc.','2026-03-30 14:46:16','2026-03-30 14:46:16');
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
  `resource_id` bigint NOT NULL COMMENT 'Resource ID',
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Comment author',
  `content` varchar(1000) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Comment content',
  `parent_id` bigint DEFAULT '0' COMMENT 'Parent comment ID (0 = top-level comment)',
  `reply_to` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Replied-to username',
  `likes` int DEFAULT '0' COMMENT 'Like count',
  `dislikes` int DEFAULT '0' COMMENT 'Dislike count',
  `is_deleted` tinyint DEFAULT '0' COMMENT 'Soft delete flag: 0=active, 1=deleted (placeholder shown)',
  `is_edited` tinyint DEFAULT '0' COMMENT 'Edited flag: 0=no, 1=yes',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_comment_resource` (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Resource comments table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,1,'admin','First!',0,NULL,2,2,0,1,'2026-03-30 14:05:53','2026-03-30 14:05:59'),(2,1,'admin','111',1,'admin',1,0,0,0,'2026-03-30 14:06:05','2026-03-30 14:06:05'),(3,2,'admin','111！',0,NULL,1,0,1,1,'2026-04-01 14:46:38','2026-04-01 14:46:51'),(4,2,'admin','111\n',3,'admin',0,0,0,0,'2026-04-01 14:46:45','2026-04-01 14:46:45'),(5,3,'admin','11',0,NULL,0,0,0,0,'2026-04-01 15:10:17','2026-04-01 15:10:17'),(6,2,'admin','11111',0,NULL,2,0,0,0,'2026-04-03 20:48:22','2026-04-03 20:48:22'),(7,2,'admin','1111\n',6,'admin',0,0,1,1,'2026-04-03 20:48:37','2026-04-03 20:48:50'),(8,1,'admin','111',0,NULL,0,0,0,0,'2026-04-26 23:15:51','2026-04-26 23:15:51'),(9,1,'admin','222',8,'admin',5,2,0,0,'2026-04-26 23:15:57','2026-04-26 23:15:57');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_like`
--

DROP TABLE IF EXISTS `comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL,
  `username` varchar(50) NOT NULL,
  `type` varchar(10) NOT NULL COMMENT 'like/dislike',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_comment_user` (`comment_id`,`username`),
  KEY `idx_comment_type` (`comment_id`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_like`
--

LOCK TABLES `comment_like` WRITE;
/*!40000 ALTER TABLE `comment_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_report`
--

DROP TABLE IF EXISTS `comment_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL COMMENT 'Reported comment ID',
  `reporter_username` varchar(50) NOT NULL COMMENT 'Reporter username',
  `reason` varchar(50) NOT NULL COMMENT 'Report reason',
  `details` varchar(500) DEFAULT NULL COMMENT 'Additional details',
  `status` int DEFAULT '0' COMMENT 'Status: 0=pending, 1=confirmed, 2=rejected',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Comment report records table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_report`
--

LOCK TABLES `comment_report` WRITE;
/*!40000 ALTER TABLE `comment_report` DISABLE KEYS */;
INSERT INTO `comment_report` VALUES (1,3,'admin','Spam','',1,'2026-04-01 15:15:33','2026-04-01 15:15:56'),(2,7,'admin','Spam','',1,'2026-04-03 20:48:44','2026-04-03 20:49:07'),(3,5,'admin','Spam','1',0,'2026-04-11 16:53:00','2026-04-11 16:53:00');
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
  `title` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Resource title',
  `description` text COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Detailed description',
  `category` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Category',
  `thumbnail` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Cover image URL',
  `media_url` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Attachment/video URL',
  `tags` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Tags',
  `location` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Location',
  `contributor_username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Contributor username',
  `status` int DEFAULT '0' COMMENT 'Status: -1=draft, 0=pending, 1=published, 2=rejected, 3=archived, 4=withdrawn',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_resource_contributor` (`contributor_username`),
  KEY `idx_resource_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Heritage resources table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `heritage_resource`
--

LOCK TABLES `heritage_resource` WRITE;
/*!40000 ALTER TABLE `heritage_resource` DISABLE KEYS */;
INSERT INTO `heritage_resource` VALUES (1,'111','111','Intangible Cultural Heritage','https://ts3.tc.mm.bing.net/th?id=ORMS.097b22616907f314f7ad96628c6fd598&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1.25&p=0','','','111','admin',1,'2026-03-30 14:23:17','2026-04-28 00:24:00'),(2,'Banana','111','Folk Activities','https://ts4.tc.mm.bing.net/th?id=ORMS.9a97e65f0798dd8ddf3bdd1a6104da4d&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1.5&p=0','','','','admin',1,'2026-04-01 14:44:36','2026-04-28 00:24:02'),(4,'222','222','Historic Sites/Architecture','https://ts1.tc.mm.bing.net/th?id=ORMS.109cbc6f988618ae55daaadb24541252&pid=Wdp&w=268&h=140&qlt=90&c=1&rs=1&dpr=1&p=0','','','','admin',1,'2026-04-11 16:50:50','2026-04-28 00:24:03');
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
  `receiver_username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
  `is_read` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'SYSTEM' COMMENT 'Notification type: REVIEW, COMMENT, SYSTEM',
  PRIMARY KEY (`id`),
  KEY `idx_notification_receiver` (`receiver_username`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,'guest01','Your heritage resource \"333\" review status updated: [APPROVED]',1,'2026-03-29 19:39:14','SYSTEM'),(2,'guest01','Your heritage resource \"444\" review status updated: [APPROVED]',1,'2026-03-29 19:45:54','SYSTEM'),(3,'guest01','Review result for \"Test Paper-cutting\": [ARCHIVED] The resource was archived by an administrator.',0,'2026-03-30 11:36:26','SYSTEM'),(4,'admin','🔔 User admin replied to your comment. Content: 111',1,'2026-03-30 14:06:05','SYSTEM'),(5,'admin','Review result for \"111\": [APPROVED] Your resource has been published.',1,'2026-03-30 14:23:31','SYSTEM'),(6,'admin','Review result for \"111\": [APPROVED] Your resource has been published.',1,'2026-04-01 14:42:56','SYSTEM'),(7,'admin','Review result for \"Banana\": [APPROVED] Your resource has been published.',1,'2026-04-01 14:46:13','SYSTEM'),(8,'admin','🔔 User admin replied to your comment. Content: 111\n',0,'2026-04-01 14:46:45','SYSTEM'),(9,'admin','Review result for \"111\": [APPROVED] Your resource has been published.',0,'2026-04-01 15:10:01','SYSTEM'),(10,'admin','Review result for \"111\": [ARCHIVED] The resource has been taken offline.',0,'2026-04-01 15:10:30','SYSTEM'),(11,'admin','🛡️ [Risk Control] The comment you reported has been verified and removed. Thank you for your feedback!',0,'2026-04-01 15:15:56','SYSTEM'),(12,'admin','⚠️ [System Warning] One of your comments was reported by multiple users for violating community rules and has been removed. Please follow the guidelines.',0,'2026-04-01 15:15:56','SYSTEM'),(13,'admin','Review result for \"111\": [APPROVED] Your resource has been published.',0,'2026-04-03 20:47:49','SYSTEM'),(14,'admin','Review result for \"Banana\": [APPROVED] Your resource has been published.',0,'2026-04-03 20:47:50','SYSTEM'),(15,'admin','Review result for \"111\": [APPROVED] Your resource has been published.',0,'2026-04-03 20:47:52','SYSTEM'),(16,'admin','🔔 User admin replied to your comment. Content: 11111',0,'2026-04-03 20:48:37','SYSTEM'),(17,'admin','🛡️ [Risk Control] The comment you reported has been verified and removed. Thank you for your feedback!',0,'2026-04-03 20:49:07','SYSTEM'),(18,'admin','⚠️ [System Warning] One of your comments was reported by multiple users for violating community rules and has been removed. Please follow the guidelines.',0,'2026-04-03 20:49:07','SYSTEM'),(19,'admin','Review result for \"222\": [APPROVED] Your resource has been published.',1,'2026-04-11 16:51:33','SYSTEM'),(20,'admin','Review result for \"222\": [ARCHIVED] The resource has been taken offline.',0,'2026-04-11 16:52:07','SYSTEM'),(21,'admin','Review result for \"222\": [APPROVED] Your resource has been published.',0,'2026-04-11 16:52:14','SYSTEM'),(22,'admin','Review result for \"111\": [APPROVED] Your resource has been published.',0,'2026-04-26 23:15:41','SYSTEM'),(23,'admin','Review result for \"Banana\": [APPROVED] Your resource has been published.',0,'2026-04-26 23:15:44','SYSTEM'),(24,'admin','🔔 User admin replied to your comment. Content: 222',1,'2026-04-26 23:15:57','SYSTEM'),(25,'admin','Review result for \"111\": [APPROVED] Your resource has been published.',0,'2026-04-28 00:24:00','SYSTEM'),(26,'admin','Review result for \"Banana\": [APPROVED] Your resource has been published.',0,'2026-04-28 00:24:02','SYSTEM'),(27,'admin','Review result for \"222\": [APPROVED] Your resource has been published.',0,'2026-04-28 00:24:03','SYSTEM');
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
  `username` varchar(50) NOT NULL COMMENT 'Applicant username',
  `reason` varchar(500) NOT NULL COMMENT 'Application reason (brief statement)',
  `status` int DEFAULT '0' COMMENT 'Status: 0=pending, 1=approved, 2=rejected',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_role_application_user` (`username`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Role promotion applications';
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
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `role` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'CONTRIBUTOR',
  `real_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `avatar_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `avatar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Avatar URL',
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Registered email',
  `nickname` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Nickname',
  `notify_review` int DEFAULT '1' COMMENT 'Receive review notifications',
  `notify_comment` int DEFAULT '1' COMMENT 'Receive comment notifications',
  `notify_system` int DEFAULT '1' COMMENT 'Receive system notifications',
  `birthday` date DEFAULT NULL COMMENT 'Birthday',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456','ADMIN','Super Admin','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-29 18:37:31',NULL,NULL,'Admin111',1,1,1,NULL),(2,'guest01','123456','CONTRIBUTOR','Intangible Heritage Inheritor Master Zhang','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-29 18:37:31',NULL,NULL,NULL,1,1,1,NULL),(3,'liu','111111','CONTRIBUTOR','liu','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-29 20:19:54',NULL,NULL,NULL,1,1,1,NULL),(4,'test1','96e79218965eb72c92a549dd5a330112','CONTRIBUTOR','test1','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-30 11:55:38',NULL,'test1@qq.com',NULL,1,1,1,NULL),(5,'111','96e79218965eb72c92a549dd5a330112','VIEWER','111','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-30 13:40:26',NULL,'111@qq.com',NULL,1,1,1,NULL),(6,'new','e10adc3949ba59abbe56e057f20f883e','VIEWER','new','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-04-01 15:13:07',NULL,'new@qq.com',NULL,1,1,1,NULL);
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
  `username` varchar(50) NOT NULL COMMENT 'Username who favorited',
  `resource_id` bigint NOT NULL COMMENT 'Favorited resource ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fav_user_res` (`username`,`resource_id`) COMMENT 'Prevent duplicate favorites',
  KEY `idx_user_favorite_resource` (`resource_id`,`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='User favorites table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_favorite`
--

LOCK TABLES `user_favorite` WRITE;
/*!40000 ALTER TABLE `user_favorite` DISABLE KEYS */;
INSERT INTO `user_favorite` VALUES (1,'test1',1,'2026-03-30 12:31:24'),(3,'admin',3,'2026-04-01 15:10:46'),(11,'admin',2,'2026-04-28 00:27:41');
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
  `username` varchar(50) NOT NULL COMMENT 'Username who liked',
  `resource_id` bigint NOT NULL COMMENT 'Liked resource ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_like_user_res` (`username`,`resource_id`) COMMENT 'Prevent duplicate likes',
  KEY `idx_user_like_resource` (`resource_id`,`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='User likes table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_like`
--

LOCK TABLES `user_like` WRITE;
/*!40000 ALTER TABLE `user_like` DISABLE KEYS */;
INSERT INTO `user_like` VALUES (2,'admin',3,'2026-04-01 15:10:13'),(4,'admin',2,'2026-04-11 16:44:53');
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

-- Dump completed on 2026-04-28  1:09:50
