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

 Date: 08/07/2019 14:30:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_cfg_subscriber_lib
-- ----------------------------
DROP TABLE IF EXISTS `t_cfg_subscriber_lib`;
CREATE TABLE `t_cfg_subscriber_lib` (
  `subscriber_id` int(11) NOT NULL,
  `lib_id` varchar(255) NOT NULL COMMENT '订阅库ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
