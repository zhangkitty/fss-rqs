/*
 Navicat MySQL Data Transfer

 Source Server         : 人脸项目开发环境
 Source Server Type    : MySQL
 Source Server Version : 50715
 Source Host           : 10.45.157.117:3306
 Source Schema         : usmsc

 Target Server Type    : MySQL
 Target Server Version : 50715
 File Encoding         : 65001

 Date: 08/07/2019 14:29:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_cfg_subscriber
-- ----------------------------
DROP TABLE IF EXISTS `t_cfg_subscriber`;
CREATE TABLE `t_cfg_subscriber` (
  `subscriber_id` int(11) NOT NULL,
  `alarm_push_url` varchar(255) NOT NULL COMMENT '订阅者的地址',
  `push_start_time` datetime DEFAULT NULL,
  `push_end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`subscriber_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_cfg_subscriber
-- ----------------------------
BEGIN;
INSERT INTO `t_cfg_subscriber` VALUES (1, 'http://www.baidu.com', NULL, NULL);
INSERT INTO `t_cfg_subscriber` VALUES (2, 'http://www.sina.com', NULL, NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
