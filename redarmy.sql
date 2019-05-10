/*
Navicat MySQL Data Transfer

Source Server         : xxx
Source Server Version : 50724
Source Host           : localhost:3306
Source Database       : redarmy

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2019-05-10 11:35:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for company
-- ----------------------------
DROP TABLE IF EXISTS `company`;
CREATE TABLE `company` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT '',
  `status` tinyint(4) DEFAULT '1' COMMENT '1：正常;0禁用;',
  `create_time` int(11) DEFAULT NULL,
  `update_time` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of company
-- ----------------------------
INSERT INTO `company` VALUES ('1', '魔鱼互动', '1', null, null);

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `imei` varchar(50) DEFAULT NULL,
  `number` varchar(20) DEFAULT NULL,
  `status` tinyint(11) DEFAULT '1',
  `type` tinyint(4) DEFAULT '1',
  `company_id` int(11) DEFAULT NULL,
  `create_time` int(11) DEFAULT NULL,
  `update_time` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `imei` (`imei`(32))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of device
-- ----------------------------
INSERT INTO `device` VALUES ('1', 'aa2d19794e9835baad89aade7e6ce691', 'E001', '1', '1', '1', '1552994758', '1552994867');
INSERT INTO `device` VALUES ('2', '72860caa3fe2b85ba5b1f20ed2fc7645', 'L001', '1', '2', '1', '1552994881', '1552994906');
INSERT INTO `device` VALUES ('3', '373271a587cff04379288f710f238c76', 'L002', '1', '2', '1', '1553146012', '1553146042');
INSERT INTO `device` VALUES ('4', 'a430b46f0f489e2439263b95014c97e0', 'E9999', '1', '1', '1', '1553859261', '1553859288');

-- ----------------------------
-- Table structure for resource
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  `create_time` int(11) DEFAULT NULL,
  `update_time` int(11) DEFAULT NULL,
  `cover_img` varchar(255) DEFAULT NULL,
  `desc` text,
  `category_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of resource
-- ----------------------------
INSERT INTO `resource` VALUES ('1', '重走长征路', '1', '1552557707', '1552557707', '/upload/images/201903141801381037.png', '重走长征路', '1');
INSERT INTO `resource` VALUES ('2', '纸', '1', '1552557707', '1552557707', '/upload/images/201904261529016947.png', '2', '1');
INSERT INTO `resource` VALUES ('3', 'QQ', '1', '1552557707', '1552557707', '/upload/images/201904261531584361.png', '3', '1');
INSERT INTO `resource` VALUES ('4', '笑脸', '1', '1552557707', '1552557707', '/upload/images/201904261535006461.png', '4', '1');

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `leader_id` int(11) DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  `create_time` int(11) DEFAULT NULL,
  `update_time` int(11) DEFAULT NULL,
  `resource_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES ('2', null, '3', '1', '1', '1553146044', '1553146044', '1');
INSERT INTO `room` VALUES ('3', null, '2', '1', '1', '1', '1553757008', '1');

-- ----------------------------
-- Table structure for roomexperiencer
-- ----------------------------
DROP TABLE IF EXISTS `roomexperiencer`;
CREATE TABLE `roomexperiencer` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `device_id` int(11) DEFAULT NULL,
  `room_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of roomexperiencer
-- ----------------------------
INSERT INTO `roomexperiencer` VALUES ('4', '1', '3');
INSERT INTO `roomexperiencer` VALUES ('5', '4', '3');

-- ----------------------------
-- Table structure for screen
-- ----------------------------
DROP TABLE IF EXISTS `screen`;
CREATE TABLE `screen` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` int(11) DEFAULT NULL,
  `create_time` int(11) DEFAULT NULL,
  `cover_img` varchar(255) DEFAULT NULL,
  `expiry_time` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of screen
-- ----------------------------
INSERT INTO `screen` VALUES ('32', '123', '1554260890', '/upload/screen/deviceId-123-TheLast.jpg', '1555578503');
INSERT INTO `screen` VALUES ('34', '111', '1556595326', '/upload/screen/deviceId-111-TheLast.png', '1556595446');

-- ----------------------------
-- Table structure for syncresource
-- ----------------------------
DROP TABLE IF EXISTS `syncresource`;
CREATE TABLE `syncresource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  `create_time` int(11) DEFAULT NULL,
  `update_time` int(11) DEFAULT NULL,
  `desc` text,
  `category_id` int(11) DEFAULT NULL,
  `md5` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of syncresource
-- ----------------------------
INSERT INTO `syncresource` VALUES ('63', '432af0b0faa1a0eab73aee8a3efc9661.mp4', '1', '1557386274', '1557386274', null, '0', 'e34d9ff859755f37a516dd5c2abc50b2', '/upload/videos/432af0b0faa1a0eab73aee8a3efc9661.mp4');
INSERT INTO `syncresource` VALUES ('64', 'C6C9E2B4-516C-9C4B-530E-6BBB5731652420190201.mp4', '1', '1557386275', '1557386275', null, '0', '5f6eae6528ab4ce0eb6be161fd3c307e', '/upload/videos/C6C9E2B4-516C-9C4B-530E-6BBB5731652420190201.mp4');
INSERT INTO `syncresource` VALUES ('65', 'CCA886E5-B4FF-819E-529F-CE2A4AD1A7AA20190201.mp4', '1', '1557386275', '1557386275', null, '0', 'b29a368a36e93a54e2eea10fb232842a', '/upload/videos/CCA886E5-B4FF-819E-529F-CE2A4AD1A7AA20190201.mp4');
INSERT INTO `syncresource` VALUES ('66', 'QQ视频20190423093125.mp4', '1', '1557386275', '1557386275', null, '0', '06de9592db58ae064c9965e25ff1f214', '/upload/videos/QQ视频20190423093125.mp4');

-- ----------------------------
-- Table structure for syncversion
-- ----------------------------
DROP TABLE IF EXISTS `syncversion`;
CREATE TABLE `syncversion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `desc` text,
  `create_time` int(11) DEFAULT NULL,
  `update_time` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of syncversion
-- ----------------------------

-- ----------------------------
-- Table structure for version
-- ----------------------------
DROP TABLE IF EXISTS `version`;
CREATE TABLE `version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` int(11) DEFAULT NULL,
  `version_name` varchar(50) DEFAULT NULL,
  `desc` text,
  `path` varchar(255) DEFAULT NULL,
  `create_time` int(11) DEFAULT NULL,
  `update_time` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of version
-- ----------------------------
INSERT INTO `version` VALUES ('1', '1', 'v1', null, '/upload/scene/201903141800215280/cz/', '1552557707', '1552557707', '1');
