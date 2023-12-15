/*
 Navicat Premium Data Transfer

 Source Server         : 群智感知
 Source Server Type    : MySQL
 Source Server Version : 50739 (5.7.39-log)
 Source Host           : 101.34.16.47:8060
 Source Schema         : crowdsense

 Target Server Type    : MySQL
 Target Server Version : 50739 (5.7.39-log)
 File Encoding         : 65001

 Date: 21/06/2023 17:30:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for afferent_task_publish
-- ----------------------------
DROP TABLE IF EXISTS `afferent_task_publish`;
CREATE TABLE `afferent_task_publish` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `publisher_id` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `title` varchar(40) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0表示“未完成”，1表示“已完成”，2表示“失效”',
  `details` varchar(1000) DEFAULT NULL,
  `images_path` varchar(500) DEFAULT NULL,
  `submit_limit` varchar(4) DEFAULT NULL,
  `coordinate` varchar(3000) DEFAULT NULL,
  `max_passed` int(11) DEFAULT NULL,
  `current_passed` int(11) DEFAULT NULL,
  `integration` float DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `size` int(11) DEFAULT NULL COMMENT '网格大小，单位为米',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=480 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for contribution
-- ----------------------------
DROP TABLE IF EXISTS `contribution`;
CREATE TABLE `contribution` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `number_of_publish_tasks` int(11) DEFAULT '0',
  `number_of_publish_tasks_submit` int(11) DEFAULT '0',
  `number_of_submit` int(11) DEFAULT '0',
  `number_of_submit_passed` int(11) DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  UNIQUE KEY `id` (`id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dict
-- ----------------------------
DROP TABLE IF EXISTS `dict`;
CREATE TABLE `dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `key_name` varchar(50) DEFAULT NULL,
  `default_value` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dict_sub
-- ----------------------------
DROP TABLE IF EXISTS `dict_sub`;
CREATE TABLE `dict_sub` (
  `id` int(11) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `value` int(11) DEFAULT NULL,
  `content` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites` (
  `id` bigint(11) NOT NULL,
  `user_id` bigint(11) DEFAULT NULL,
  `task_id` bigint(11) DEFAULT NULL,
  `status` varchar(255) NOT NULL DEFAULT '0' COMMENT '0代表未完成，1代表完成',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `advice` varchar(200) DEFAULT NULL,
  `contact_details` varchar(255) DEFAULT NULL,
  `feedback_user_id` bigint(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COMMENT='预留的一张表，目前只设置提交反馈，也就是新增记录功能。';

-- ----------------------------
-- Table structure for map_date
-- ----------------------------
DROP TABLE IF EXISTS `map_date`;
CREATE TABLE `map_date` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `longitude` float DEFAULT NULL,
  `latitude` float DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `topic` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for match_new
-- ----------------------------
DROP TABLE IF EXISTS `match_new`;
CREATE TABLE `match_new` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `big_task_id` int(11) DEFAULT NULL,
  `small_task_id` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1207 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `t_id` bigint(20) NOT NULL COMMENT '任务Id或者任务提交Id\n',
  `type` int(11) NOT NULL COMMENT '消息类型\n0: tId 为 任务Id\n1: tId 为 任务提交Id，且可编辑(发布者的数据审核通知)\n2: tId 为 任务提交Id，仅查看(提交者的数据审核成功/失败通知)\n',
  `title` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 NOT NULL COMMENT '内容',
  `status` int(11) NOT NULL COMMENT '状态：0 未读，1 已读',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` int(11) NOT NULL COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户 消息表';

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rootId` bigint(20) NOT NULL DEFAULT '-1',
  `publisherId` bigint(20) DEFAULT NULL,
  `type` varchar(256) DEFAULT NULL,
  `title` varchar(40) DEFAULT NULL,
  `details` varchar(1000) DEFAULT NULL,
  `imagesPath` varchar(500) DEFAULT NULL,
  `submitLimit` varchar(20) DEFAULT NULL COMMENT '任务提交数量限制',
  `maxPassed` bigint(20) NOT NULL COMMENT '任务通过数量限制',
  `currentPassed` bigint(20) DEFAULT NULL COMMENT '当前通过量',
  `completedSmallTask` int(11) NOT NULL COMMENT '大任务中已完成小任务的数量，小任务的该项值为-1',
  `NumberOfSmallTask` int(11) DEFAULT NULL COMMENT '包含小任务总数量，小任务的该项值为-1',
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `size` int(11) DEFAULT NULL COMMENT '网格大小，单位为?',
  `integration` double DEFAULT NULL,
  `onlineStatus` int(4) DEFAULT NULL COMMENT '0表示“下线”，1表示“上线”',
  `submitStatus` int(4) DEFAULT NULL COMMENT '0表示“未完成”，1表示“已完成”，2表示“失效”',
  `checkStatus` int(4) DEFAULT NULL COMMENT '0表示“待审核”，1表示“审核通过”，2表示“未通过”',
  `invalidationReason` varchar(1000) DEFAULT NULL COMMENT '审核不通过的理由',
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`completedSmallTask`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=643 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for task-type
-- ----------------------------
DROP TABLE IF EXISTS `task-type`;
CREATE TABLE `task-type` (
  `taskId` bigint(20) NOT NULL,
  `typeId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for task_publish
-- ----------------------------
DROP TABLE IF EXISTS `task_publish`;
CREATE TABLE `task_publish` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `publisher_id` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `title` varchar(40) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0表示“未完成”，1表示“已完成”，2表示“失效”',
  `details` varchar(1000) DEFAULT NULL,
  `images_path` varchar(500) DEFAULT NULL,
  `submit_limit` varchar(4) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `max_passed` int(11) DEFAULT NULL,
  `current_passed` int(11) DEFAULT NULL,
  `integration` float DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `size` int(11) DEFAULT NULL COMMENT '网格大小，单位为米',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=307 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for task_reject
-- ----------------------------
DROP TABLE IF EXISTS `task_reject`;
CREATE TABLE `task_reject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `taskId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for task_submit
-- ----------------------------
DROP TABLE IF EXISTS `task_submit`;
CREATE TABLE `task_submit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `taskId` bigint(20) DEFAULT NULL,
  `rootTaskId` bigint(20) DEFAULT NULL,
  `submitterId` bigint(20) DEFAULT NULL,
  `type` varchar(256) DEFAULT NULL COMMENT '用于获取用户喜好 这个字段在提交时前端给定 不对用户开放',
  `description` varchar(1000) DEFAULT NULL,
  `numericalValue` float DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `filesPath` varchar(300) DEFAULT NULL,
  `status` int(4) DEFAULT NULL COMMENT '0表示"待审核"，1表示"已通过"，2表示"不合格"',
  `checkTime` datetime DEFAULT NULL,
  `reason` varchar(200) DEFAULT NULL,
  `completeTime` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新保存时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for type
-- ----------------------------
DROP TABLE IF EXISTS `type`;
CREATE TABLE `type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `statu` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '0表示正常使用，1表示停止使用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号\n',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `icon` varchar(300) DEFAULT NULL COMMENT '头像地址',
  `nick_name` varchar(36) DEFAULT NULL COMMENT '昵称',
  `signature` varchar(160) DEFAULT NULL COMMENT '签名',
  `longitude` double DEFAULT NULL COMMENT '经度',
  `latitude` double DEFAULT NULL COMMENT '纬度',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `role` varchar(50) DEFAULT NULL COMMENT '角色',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1651146975216799745 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
