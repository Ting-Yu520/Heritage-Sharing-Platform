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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统审计日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,'Admin_01','CREATE',4,'Created new resource: 111','2026-03-29 18:06:18'),(2,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 18:22:12'),(3,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 18:22:12'),(4,'Admin_01','APPROVE/RESTORE',3,'Status changed from 3 to 1','2026-03-29 18:48:30'),(5,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 18:48:51'),(6,'Admin_01','CREATE',5,'Created new resource: 222','2026-03-29 19:14:04'),(7,'Admin_01','APPROVE/RESTORE',5,'Status changed from 0 to 1','2026-03-29 19:14:21'),(8,'Admin_01','APPROVE/RESTORE',3,'Status changed from 3 to 1','2026-03-29 19:14:38'),(9,'Admin_01','APPROVE/RESTORE',1,'Status changed from 3 to 1','2026-03-29 19:14:40'),(10,'System (Auto-Archive Policy)','AUTO_ARCHIVE',1,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 19:14:48'),(11,'System (Auto-Archive Policy)','AUTO_ARCHIVE',3,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 19:14:48'),(12,'System (Auto-Archive Policy)','AUTO_ARCHIVE',5,'System rule triggered: Archived resource inactive for > 2 mins.','2026-03-29 19:16:18'),(13,'Admin_01','APPROVE/RESTORE',1,'Status changed from 3 to 1','2026-03-29 19:25:56'),(14,'Admin_01','APPROVE/RESTORE',3,'Status changed from 3 to 1','2026-03-29 19:26:00'),(15,'Admin_01','CREATE',6,'Created new resource: 333','2026-03-29 19:38:55'),(16,'Admin_01','UPDATE_STATUS',6,'Status updated to 1','2026-03-29 19:39:14'),(17,'Admin_01','CREATE',7,'Created new resource: 444','2026-03-29 19:45:28'),(18,'Admin_01','UPDATE_STATUS',7,'Status updated to 1','2026-03-29 19:45:54');
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resource_id` bigint NOT NULL,
  `username` varchar(50) DEFAULT '匿名文化爱好者',
  `content` text NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,1,'匿名文化爱好者','111','2026-03-29 19:15:09');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `heritage_resource`
--

DROP TABLE IF EXISTS `heritage_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `heritage_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源唯一标识 ID',
  `title` varchar(100) NOT NULL COMMENT '遗产名称/标题',
  `description` text NOT NULL COMMENT '遗产详细描述/策展文案',
  `category` varchar(50) DEFAULT NULL COMMENT '分类 (如：非遗、古建筑、民俗)',
  `image_url` varchar(255) DEFAULT NULL COMMENT '封面图/资源文件链接',
  `status` int DEFAULT '0' COMMENT '审核状态',
  `submitter_id` bigint DEFAULT NULL COMMENT '提交者/贡献者的用户 ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区遗产资源主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `heritage_resource`
--

LOCK TABLES `heritage_resource` WRITE;
/*!40000 ALTER TABLE `heritage_resource` DISABLE KEYS */;
INSERT INTO `heritage_resource` VALUES (1,'百年老街皮影戏','一段关于传统皮影戏在现代社区传承的珍贵录像与图文记录。','非物质文化遗产',NULL,1,NULL,'2026-03-29 16:21:32','2026-03-29 16:21:32'),(3,'测试剪纸','这是一段测试文本','传统手工艺',NULL,1,NULL,'2026-03-29 17:50:30','2026-03-29 17:50:40'),(4,'111','111','非物质文化遗产',NULL,0,NULL,'2026-03-29 18:06:18','2026-03-29 18:06:18'),(5,'222','222','古建筑',NULL,3,NULL,'2026-03-29 19:14:03','2026-03-29 19:14:03'),(6,'333','333','民俗活动',NULL,1,NULL,'2026-03-29 19:38:55','2026-03-29 19:38:55'),(7,'444','444','非物质文化遗产',NULL,1,NULL,'2026-03-29 19:45:27','2026-03-29 19:45:27');
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,'guest01','您的遗产资源《333》审核状态更新为：【已通过】',1,'2026-03-29 19:39:14'),(2,'guest01','您的遗产资源《444》审核状态更新为：【已通过】',1,'2026-03-29 19:45:54');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456','ADMIN','超级管理员','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-29 18:37:31'),(2,'guest01','123456','CONTRIBUTOR','非遗传承人张师傅','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-29 18:37:31'),(3,'liu','111111','CONTRIBUTOR','liu','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','2026-03-29 20:19:54');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
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

-- Dump completed on 2026-03-29 20:28:29
