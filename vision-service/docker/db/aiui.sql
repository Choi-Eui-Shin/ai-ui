CREATE DATABASE aiui DEFAULT CHARACTER SET utf8;

USE aiui;

CREATE USER IF NOT EXISTS 'aiui' IDENTIFIED BY 'aiui';

GRANT ALL ON aiui.* TO 'aiui';

set names utf8;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `convert_history`
--

DROP TABLE IF EXISTS `convert_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `convert_history` (
  `create_date` varchar(14) NOT NULL,
  `user_id` varchar(40) NOT NULL,
  `image` tinyblob,
  `meta_json` varchar(8192) DEFAULT NULL,
  `screen_title` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`create_date`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `convert_history`
--

LOCK TABLES `convert_history` WRITE;
/*!40000 ALTER TABLE `convert_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `convert_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_detail`
--

DROP TABLE IF EXISTS `rule_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rule_detail` (
  `clsss_id` varchar(80) NOT NULL,
  `uuid` varchar(36) NOT NULL,
  `change_event_tag` varchar(1024) DEFAULT NULL,
  `click_event_tag` varchar(1024) DEFAULT NULL,
  `default_value` varchar(1024) DEFAULT NULL,
  `extra_attribute` varchar(1024) DEFAULT NULL,
  `input_event_tag` varchar(1024) DEFAULT NULL,
  `ui_tag` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`clsss_id`,`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_detail`
--

LOCK TABLES `rule_detail` WRITE;
/*!40000 ALTER TABLE `rule_detail` DISABLE KEYS */;
INSERT INTO `rule_detail` VALUES ('_ALIGN_CENTER_','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,NULL,'class=\"d-flex justify-center\"',NULL,NULL),('_ALIGN_END_','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,NULL,'class=\"d-flex justify-end\"',NULL,NULL),('_ALIGN_START_','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,NULL,'class=\"d-flex justify-start\"',NULL,NULL),('_COL_','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,NULL,'cols=\"${SIZE}\"',NULL,'v-col'),('_CONTAINER_','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,NULL,NULL,NULL,'v-container'),('_ROW_','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,NULL,NULL,NULL,'v-row'),('button','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,'@click','버튼','color=\"primary\"',NULL,'v-btn'),('checkbox','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,'체크박스',NULL,NULL,'v-checkbox'),('div','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,NULL,NULL,NULL,'div'),('label','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,'라벨','class=\"font-weight-regular\"',NULL,'p'),('textbox','e5558d53-aaa0-40d1-9367-5458a7d123e4',NULL,NULL,'텍스트박스','hide-details=\"auto\"','@input','v-text-field');
/*!40000 ALTER TABLE `rule_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_master`
--

DROP TABLE IF EXISTS `rule_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rule_master` (
  `uuid` varchar(36) NOT NULL,
  `create_date` varchar(14) DEFAULT NULL,
  `rule_title` varchar(1024) DEFAULT NULL,
  `user_id` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_master`
--

LOCK TABLES `rule_master` WRITE;
/*!40000 ALTER TABLE `rule_master` DISABLE KEYS */;
INSERT INTO `rule_master` VALUES ('884b57eb-2a93-486e-96ae-7eae18d7a0bd','20231230070000','React (MUI)','SYSTEM'),('b906fb54-a525-4310-88fe-4153dddae2d7','20231230070000','Vue (Vuetify 3.x)','SYSTEM'),('e5558d53-aaa0-40d1-9367-5458a7d123e4','20231230070000','Vue (Vuetify 2.x)','SYSTEM');
/*!40000 ALTER TABLE `rule_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` varchar(40) NOT NULL,
  `user_name` varchar(80) DEFAULT NULL,
  `user_password` varchar(80) DEFAULT NULL,
  `user_role` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;