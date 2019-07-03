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

 Date: 03/07/2019 10:06:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_cfg_subscriber
-- ----------------------------
DROP TABLE IF EXISTS `t_cfg_subscriber`;
CREATE TABLE `t_cfg_subscriber` (
  `id` int(11) NOT NULL,
  `url` varchar(255) NOT NULL COMMENT '订阅者的地址',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `camera_ids` varchar(255) NOT NULL COMMENT '订阅摄像头的ID集合',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_cfg_subscriber
-- ----------------------------
BEGIN;
INSERT INTO `t_cfg_subscriber` VALUES (1, 'http://www.baidu.com', NULL, NULL, '32010100001310008051');
INSERT INTO `t_cfg_subscriber` VALUES (2, 'http://www.sina.com', NULL, NULL, '32010100001310008001,32010100001310008002');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
