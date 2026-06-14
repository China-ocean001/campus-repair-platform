-- 校园智能报修服务平台 数据库初始化脚本
-- 数据库版本: MySQL 8.0+

CREATE DATABASE IF NOT EXISTS campus_repair DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_repair;

-- =============================
-- 用户表
-- =============================
CREATE TABLE IF NOT EXISTS `t_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名/学工号',
  `password`    VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
  `real_name`   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色: 0学生 1维修工 2管理员',
  `department`  VARCHAR(100) DEFAULT NULL COMMENT '所在院系/部门',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================
-- 维修工技能/类别表
-- =============================
CREATE TABLE IF NOT EXISTS `t_worker_skill` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `worker_id`   BIGINT   NOT NULL COMMENT '维修工ID',
  `category`    TINYINT  NOT NULL COMMENT '擅长类别: 1水电 2网络 3门窗 4电器 5其他',
  PRIMARY KEY (`id`),
  KEY `idx_worker_id` (`worker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修工技能表';

-- =============================
-- 报修工单表（核心）
-- =============================
CREATE TABLE IF NOT EXISTS `t_repair_order` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `order_no`      VARCHAR(32)  NOT NULL COMMENT '工单编号(唯一)',
  `student_id`    BIGINT       NOT NULL COMMENT '报修学生ID',
  `worker_id`     BIGINT       DEFAULT NULL COMMENT '维修工ID',
  `category`      TINYINT      NOT NULL COMMENT '报修类别: 1水电 2网络 3门窗 4电器 5其他',
  `location`      VARCHAR(200) NOT NULL COMMENT '报修地点',
  `description`   TEXT         NOT NULL COMMENT '故障描述',
  `images`        VARCHAR(500) DEFAULT NULL COMMENT '图片URL列表(逗号分隔)',
  `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0待派单 1已派单 2处理中 3已完成 4已评价 5已取消',
  `priority`      TINYINT      NOT NULL DEFAULT 1 COMMENT '优先级: 1普通 2紧急 3非常紧急',
  `assign_time`   DATETIME     DEFAULT NULL COMMENT '派单时间',
  `accept_time`   DATETIME     DEFAULT NULL COMMENT '接单时间',
  `finish_time`   DATETIME     DEFAULT NULL COMMENT '完成时间',
  `remark`        VARCHAR(500) DEFAULT NULL COMMENT '维修备注',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_status` (`status`),
  KEY `idx_category` (`category`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修工单表';

-- =============================
-- 工单状态变更日志
-- =============================
CREATE TABLE IF NOT EXISTS `t_order_log` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `order_id`    BIGINT       NOT NULL COMMENT '工单ID',
  `from_status` TINYINT      DEFAULT NULL COMMENT '变更前状态',
  `to_status`   TINYINT      NOT NULL COMMENT '变更后状态',
  `operator_id` BIGINT       DEFAULT NULL COMMENT '操作人ID',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单状态日志';

-- =============================
-- 评价表
-- =============================
CREATE TABLE IF NOT EXISTS `t_evaluation` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `order_id`    BIGINT   NOT NULL COMMENT '工单ID',
  `student_id`  BIGINT   NOT NULL COMMENT '评价学生ID',
  `worker_id`   BIGINT   NOT NULL COMMENT '被评维修工ID',
  `score`       TINYINT  NOT NULL COMMENT '评分 1-5',
  `content`     VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- =============================
-- 公告表
-- =============================
CREATE TABLE IF NOT EXISTS `t_notice` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `title`       VARCHAR(200) NOT NULL COMMENT '标题',
  `content`     TEXT         NOT NULL COMMENT '内容',
  `admin_id`    BIGINT       NOT NULL COMMENT '发布管理员',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '1发布 0下架',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- =============================
-- 初始化数据
-- =============================
-- 密码均为 123456 的BCrypt加密值
INSERT INTO `t_user` (`username`, `password`, `real_name`, `phone`, `role`, `department`, `status`) VALUES
('admin',      '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', '13800000000', 2, '信息中心', 1),
('worker01',   '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张师傅',   '13811111111', 1, '后勤维修部', 1),
('worker02',   '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李师傅',   '13822222222', 1, '后勤维修部', 1),
('student01',  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '都海洋',   '13833333333', 0, '软件工程学院', 1),
('student02',  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三',     '13844444444', 0, '软件工程学院', 1);

INSERT INTO `t_worker_skill` (`worker_id`, `category`) VALUES
(2, 1), (2, 3),
(3, 2), (3, 4);

INSERT INTO `t_notice` (`title`, `content`, `admin_id`, `status`) VALUES
('报修平台上线公告', '校园智能报修服务平台正式上线，请同学们体验使用，如有问题请联系信息中心。', 1, 1),
('维修服务时间说明', '维修服务时间为工作日 8:00-22:00，节假日 9:00-18:00，紧急情况请拨打后勤热线。', 1, 1);

-- 示例工单数据
INSERT INTO `t_repair_order` (`order_no`, `student_id`, `worker_id`, `category`, `location`, `description`, `status`, `priority`, `assign_time`, `accept_time`, `finish_time`) VALUES
('RP202406110001', 4, 2, 1, '3号宿舍楼302室', '宿舍水龙头漏水，影响正常使用', 4, 1, '2024-06-11 09:00:00', '2024-06-11 09:30:00', '2024-06-11 14:00:00'),
('RP202406110002', 5, 3, 2, '图书馆3楼阅览室', '网络连接不稳定，频繁断线', 2, 2, '2024-06-11 10:00:00', '2024-06-11 10:15:00', NULL),
('RP202406110003', 4, NULL, 4, '2号教学楼205教室', '投影仪无法正常开机', 0, 1, NULL, NULL, NULL);

INSERT INTO `t_evaluation` (`order_id`, `student_id`, `worker_id`, `score`, `content`) VALUES
(1, 4, 2, 5, '师傅很专业，维修速度快，态度也很好！');

INSERT INTO `t_order_log` (`order_id`, `from_status`, `to_status`, `operator_id`, `remark`) VALUES
(1, NULL, 0, 4, '学生提交报修'),
(1, 0, 1, 1, '管理员派单给张师傅'),
(1, 1, 2, 2, '维修工接单'),
(1, 2, 3, 2, '维修完成'),
(1, 3, 4, 4, '学生已评价');
