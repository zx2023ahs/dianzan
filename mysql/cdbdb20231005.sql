/*
 Navicat MySQL Data Transfer

 Source Server         : c站
 Source Server Type    : MySQL
 Source Server Version : 80028 (8.0.28)
 Source Host           : rm-j0b966s05bxewou16co.mysql.australia.rds.aliyuncs.com:3306
 Source Schema         : cdbdb

 Target Server Type    : MySQL
 Target Server Version : 80028 (8.0.28)
 File Encoding         : 65001

 Date: 05/10/2023 05:11:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_app_appv
-- ----------------------------
DROP TABLE IF EXISTS `t_app_appv`;
CREATE TABLE `t_app_appv`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `app_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '软件类型',
  `app_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '升级链接',
  `dzstatus` int NULL DEFAULT NULL COMMENT '状态',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编号',
  `min_version_number` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '低于版本限制使用',
  `version_number` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '版本更新' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzcredit_creditconfig
-- ----------------------------
DROP TABLE IF EXISTS `t_dzcredit_creditconfig`;
CREATE TABLE `t_dzcredit_creditconfig`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `credit_max` int NULL DEFAULT NULL COMMENT '信誉分高值',
  `credit_min` int NULL DEFAULT NULL COMMENT '信誉分低值',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `yield` decimal(30, 6) NULL DEFAULT NULL COMMENT '收益率',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '信誉分配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzcredit_creditrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_dzcredit_creditrecord`;
CREATE TABLE `t_dzcredit_creditrecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `after_credit` int NULL DEFAULT NULL COMMENT '变更后信誉分',
  `befort_credit` int NULL DEFAULT NULL COMMENT '变更前信誉分',
  `charge_status` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变更类型',
  `credit_change` int NULL DEFAULT NULL COMMENT '信誉分变更值',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `from_account` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源账号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 86117 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '信誉分变动记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzcredit_creditrules
-- ----------------------------
DROP TABLE IF EXISTS `t_dzcredit_creditrules`;
CREATE TABLE `t_dzcredit_creditrules`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `below_credit_score` int NULL DEFAULT NULL COMMENT '低于信用分',
  `higher_credit_score` int NULL DEFAULT NULL COMMENT '高于信用分',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `profit_percentage` decimal(30, 6) NULL DEFAULT NULL COMMENT '收益百分比',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '信用规则' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzcredit_usercredit
-- ----------------------------
DROP TABLE IF EXISTS `t_dzcredit_usercredit`;
CREATE TABLE `t_dzcredit_usercredit`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `credit` int NULL DEFAULT NULL COMMENT '信誉分',
  `final_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备最后一次运营时间',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '信誉分状态',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `vip_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'vip类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 68361 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户信誉分' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzgoods_flowingwaterpb
-- ----------------------------
DROP TABLE IF EXISTS `t_dzgoods_flowingwaterpb`;
CREATE TABLE `t_dzgoods_flowingwaterpb`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzstatus` int NULL DEFAULT NULL COMMENT '状态',
  `flowing_water_date` datetime NULL DEFAULT NULL COMMENT '流水时间',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `relevels` int NULL DEFAULT NULL COMMENT '相对层级',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `source_user_account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源用户账号',
  `task_idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返佣任务编号',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `task_idw_que`(`task_idw` ASC, `create_time` ASC, `uid` ASC, `flowing_water_date` ASC) USING BTREE COMMENT '任务idw+创建时间 联合索引'
) ENGINE = InnoDB AUTO_INCREMENT = 47819 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充电宝返佣流水' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzgoods_powerbank
-- ----------------------------
DROP TABLE IF EXISTS `t_dzgoods_powerbank`;
CREATE TABLE `t_dzgoods_powerbank`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `banner_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品类型',
  `dzstatus` int NULL DEFAULT NULL COMMENT '状态',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编号',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `online_quantity` int NULL DEFAULT NULL COMMENT '在线数量',
  `price` decimal(30, 6) NULL DEFAULT NULL COMMENT '价格',
  `quantity_in_use` int NULL DEFAULT NULL COMMENT '使用中的数量',
  `tota_quantity` int NULL DEFAULT NULL COMMENT '总数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充电宝' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzgoods_powerbanktask
-- ----------------------------
DROP TABLE IF EXISTS `t_dzgoods_powerbanktask`;
CREATE TABLE `t_dzgoods_powerbanktask`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `banner_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品类型',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `last_time` datetime NULL DEFAULT NULL COMMENT '最后一次返佣时间',
  `name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `pay_price` decimal(30, 6) NULL DEFAULT NULL COMMENT '支付金额',
  `pbidw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品编号',
  `tota_quantity` int NULL DEFAULT NULL COMMENT '购买数量',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `hours` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行档次(几小时)',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `vip_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ViP类型',
  `is_refund` int NULL DEFAULT NULL COMMENT '到期是否已经退款 空或1为未退 2为已退',
  PRIMARY KEY (`id`, `idw`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58510 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充电宝返佣任务' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzgoods_recordpb
-- ----------------------------
DROP TABLE IF EXISTS `t_dzgoods_recordpb`;
CREATE TABLE `t_dzgoods_recordpb`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `former_credit_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '前余额',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `post_credit_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '后余额',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `relevels` int NULL DEFAULT NULL COMMENT '相对层级',
  `source_user_account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源用户账号',
  `rebate_time` datetime NULL DEFAULT NULL COMMENT '返佣时间',
  `fidw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '造假数据唯一值',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `money`(`money` ASC) USING BTREE,
  INDEX `source_invitation_code`(`source_invitation_code` ASC) USING BTREE,
  INDEX `create_time`(`create_time` ASC) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1395100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充电宝返佣记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzgoods_refundrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_dzgoods_refundrecord`;
CREATE TABLE `t_dzgoods_refundrecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `cancel_refund` decimal(30, 6) NULL DEFAULT NULL COMMENT '退款时充电宝到期退款比',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '实际退款金额',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_money` decimal(30, 6) NULL DEFAULT NULL COMMENT '累计购买vip金额',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `vip_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退款时vip等级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '充电宝到期退款记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzgoods_rowerreceiverecord
-- ----------------------------
DROP TABLE IF EXISTS `t_dzgoods_rowerreceiverecord`;
CREATE TABLE `t_dzgoods_rowerreceiverecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `image` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '预计收益',
  `name` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `pay_price` decimal(30, 6) NULL DEFAULT NULL COMMENT '小时价',
  `pbidw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充电宝编号',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `status` int NULL DEFAULT NULL COMMENT '领取状态',
  `taskidw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充电宝任务编号',
  `tota_quantity` int NULL DEFAULT NULL COMMENT '购买数量',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `vip_type` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ViP类型',
  `income_hour` bigint NULL DEFAULT NULL COMMENT '投放小时',
  `credit` int NULL DEFAULT NULL COMMENT '当前信誉分',
  `yield` decimal(30, 6) NULL DEFAULT NULL COMMENT '收益率',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `account`(`account` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 539314 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '充电宝收益手动领取记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzgoods_totalbonuspb
-- ----------------------------
DROP TABLE IF EXISTS `t_dzgoods_totalbonuspb`;
CREATE TABLE `t_dzgoods_totalbonuspb`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_bonus_income` decimal(30, 6) NULL DEFAULT NULL COMMENT '充电宝返佣总收入',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `source_user_account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源用户账号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_l1ntfqqb489klyw58hwrx4j0e`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充电宝返佣总收入' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzprize_expectwinninguser
-- ----------------------------
DROP TABLE IF EXISTS `t_dzprize_expectwinninguser`;
CREATE TABLE `t_dzprize_expectwinninguser`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `is_prize` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否中奖 yes/no',
  `prize_idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预期中奖ID',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `prize_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3280 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '预期中奖用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzprize_luckydraw
-- ----------------------------
DROP TABLE IF EXISTS `t_dzprize_luckydraw`;
CREATE TABLE `t_dzprize_luckydraw`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '抽奖活动名称',
  `status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动是否开启',
  `prize_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动',
  `end_time` datetime NULL DEFAULT NULL COMMENT '活动结束时间',
  `remark` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '活动说明',
  `start_time` datetime NULL DEFAULT NULL COMMENT '活动开始时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '抽奖奖品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzprize_prize
-- ----------------------------
DROP TABLE IF EXISTS `t_dzprize_prize`;
CREATE TABLE `t_dzprize_prize`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '虚拟货币金额 实物为0',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `prize_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品名称',
  `types` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品类型   1 余额  2 实物',
  `url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品图片',
  `winning_chance` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '中奖几率千分比',
  `prize_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动',
  `prize_nice` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '抽奖奖品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzprize_prizenum
-- ----------------------------
DROP TABLE IF EXISTS `t_dzprize_prizenum`;
CREATE TABLE `t_dzprize_prizenum`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `prize_num` int NULL DEFAULT NULL COMMENT '抽奖次数',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `prize_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2279 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '抽奖次数表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzprize_winningrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_dzprize_winningrecord`;
CREATE TABLE `t_dzprize_winningrecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `prize_idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品ID',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `prize_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品名称',
  `amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '中奖金额',
  `prize_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动',
  `surplus_number` int NULL DEFAULT NULL COMMENT '剩余抽奖次数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `account`(`account` ASC) USING BTREE,
  INDEX `prize_type`(`prize_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4626 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '中奖记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzscore_scoreprize
-- ----------------------------
DROP TABLE IF EXISTS `t_dzscore_scoreprize`;
CREATE TABLE `t_dzscore_scoreprize`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '虚拟货币金额 实物为0',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `max_vip` int NULL DEFAULT NULL COMMENT '最大VIP等级',
  `prize_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品名称',
  `score` decimal(30, 6) NULL DEFAULT NULL COMMENT '消耗积分',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `types` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品类型   1 余额  2 实物',
  `url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '积分奖品' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzscore_scoreprizerecord
-- ----------------------------
DROP TABLE IF EXISTS `t_dzscore_scoreprizerecord`;
CREATE TABLE `t_dzscore_scoreprizerecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '中奖金额',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `prize_idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品ID',
  `prize_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品名称',
  `score` decimal(30, 6) NULL DEFAULT NULL COMMENT '消耗积分',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `surplus_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '剩余积分',
  `types` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品类型   1 余额  2 实物',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '奖品图片',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `account`(`account` ASC) USING BTREE,
  INDEX `types`(`types` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '积分奖品记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzscore_signin
-- ----------------------------
DROP TABLE IF EXISTS `t_dzscore_signin`;
CREATE TABLE `t_dzscore_signin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `last_sign_time` datetime NULL DEFAULT NULL COMMENT '上次签到时间',
  `reward_amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '奖励积分',
  `sign_days` int NULL DEFAULT NULL COMMENT '签到天数',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `account`(`account` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户签到' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzscore_signinset
-- ----------------------------
DROP TABLE IF EXISTS `t_dzscore_signinset`;
CREATE TABLE `t_dzscore_signinset`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `day_index` int NULL DEFAULT NULL COMMENT '连续签到第几天',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `reward_amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '奖励积分',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '签到配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzscore_userscore
-- ----------------------------
DROP TABLE IF EXISTS `t_dzscore_userscore`;
CREATE TABLE `t_dzscore_userscore`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `user_score` decimal(30, 6) NOT NULL COMMENT '用户积分',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户积分总' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzscore_userscorehistory
-- ----------------------------
DROP TABLE IF EXISTS `t_dzscore_userscorehistory`;
CREATE TABLE `t_dzscore_userscorehistory`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `type` int NULL DEFAULT NULL COMMENT '类型 1签到 2邀请 3赠送',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `user_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '用户积分',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `account`(`account` ASC) USING BTREE,
  INDEX `type`(`type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户积分记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzsys_country
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_country`;
CREATE TABLE `t_dzsys_country`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `country_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家码',
  `country_code_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家代号',
  `country_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家名称',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `logo` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'logo',
  `queue_number` int NULL DEFAULT NULL COMMENT '排序号',
  `country_name_english` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家英文名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 215 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '国家区号' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzsys_dashboard
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_dashboard`;
CREATE TABLE `t_dzsys_dashboard`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzsys_dzbanner
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_dzbanner`;
CREATE TABLE `t_dzsys_dzbanner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `banner_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `jump_link` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转链接',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮播图' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzsys_homepagetotal
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_homepagetotal`;
CREATE TABLE `t_dzsys_homepagetotal`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `c_money` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充值金额',
  `c_num` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充值数量',
  `day` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日期',
  `dc_money` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注册奖励',
  `l1pb` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'L1任务返佣',
  `l1vip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'L1VIP返佣',
  `l2pb` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'L2任务返佣',
  `l2vip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'L2VIP返佣',
  `l3pb` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'L3任务返佣',
  `l3vip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'L3VIP返佣',
  `money` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '平台盈利',
  `pb_num` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '完成任务数量',
  `registration_num` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注册人数',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `t_money` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '提现金额',
  `t_num` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '提现数量',
  `total_pb` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发放总佣金',
  `vip_first_num` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '首充人数',
  `vip_num` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'VIP新增人数',
  `start_pb` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '总启用设备',
  `tystart_pb` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '体验会员启用设备',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1057 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '首页统计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzsys_mulutilinguallang
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_mulutilinguallang`;
CREATE TABLE `t_dzsys_mulutilinguallang`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `lang_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '语言',
  `lang_context` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '多语言内容',
  `lang_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '多语言key',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '多语言' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzsys_officialnews
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_officialnews`;
CREATE TABLE `t_dzsys_officialnews`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `dzcontent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `jump_link` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `official_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '广告类型',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `language` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '语言',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公告信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzsys_onlineserve
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_onlineserve`;
CREATE TABLE `t_dzsys_onlineserve`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `customer_service_link` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客服链接',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `onlines_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客服类型',
  `logo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客服logo',
  `onlines_flag` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客服状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '在线客服' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzsys_paymentchannel
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_paymentchannel`;
CREATE TABLE `t_dzsys_paymentchannel`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `channel_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通道名称',
  `currency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '币种',
  `currency_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '货币代码',
  `dzkey` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密钥',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `private_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密钥',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'dzuser' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzsys_sms
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_sms`;
CREATE TABLE `t_dzsys_sms`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `appid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'appid',
  `appkey` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'appkey',
  `appse` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'appse',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `platform_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台名称',
  `dzstatus` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
  `api_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'base_url',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短信信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzsys_smsnumrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_smsnumrecord`;
CREATE TABLE `t_dzsys_smsnumrecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `count` int NULL DEFAULT NULL COMMENT '次数',
  `country_code_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '国家代号',
  `day` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日期',
  `phone` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8516 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '日发送短信次数记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzsys_syslog
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_syslog`;
CREATE TABLE `t_dzsys_syslog`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `obj_id` bigint NULL DEFAULT NULL COMMENT '操作实体主键ID',
  `operation` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '具体操作',
  `operator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人',
  `operator_system` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作端  APP/PC',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `operation_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1646201 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzsys_tutorialcenter
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_tutorialcenter`;
CREATE TABLE `t_dzsys_tutorialcenter`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `text_content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '文本内容',
  `title` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '教程类型',
  `video_url` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '视频Url',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '教程中心' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzsys_userippermissions
-- ----------------------------
DROP TABLE IF EXISTS `t_dzsys_userippermissions`;
CREATE TABLE `t_dzsys_userippermissions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `black_or_white` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'blacklist/whitelist',
  `ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP',
  `types` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'PC/MV',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户IP权限' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dztask_missioninformation
-- ----------------------------
DROP TABLE IF EXISTS `t_dztask_missioninformation`;
CREATE TABLE `t_dztask_missioninformation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `commission` decimal(30, 6) NULL DEFAULT NULL COMMENT '佣金',
  `dzstatus` int NULL DEFAULT NULL COMMENT '状态',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `logo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'logo',
  `margin` int NULL DEFAULT NULL COMMENT '余量',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `order_request` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接单要求',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `task_link` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务链接',
  `task_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务类型',
  `total` int NULL DEFAULT NULL COMMENT '总量',
  `vip_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ViP类型',
  `author_avatar` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者头像',
  `author_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者名称',
  `author_needs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '作者需求',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_lky021nb65ghnwjnlmvsvyiq`(`idw` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dztask_numberoftasks
-- ----------------------------
DROP TABLE IF EXISTS `t_dztask_numberoftasks`;
CREATE TABLE `t_dztask_numberoftasks`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `number_of_tasks` int NULL DEFAULT NULL COMMENT '任务数量',
  `number_of_tasks_completed` int NULL DEFAULT NULL COMMENT '任务数量已完成',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务次数' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dztask_taskorder
-- ----------------------------
DROP TABLE IF EXISTS `t_dztask_taskorder`;
CREATE TABLE `t_dztask_taskorder`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `commission` decimal(30, 6) NULL DEFAULT NULL COMMENT '佣金',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `mission_idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务编号',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `task_order_status` int NULL DEFAULT NULL COMMENT '任务订单状态',
  `author_avatar` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者头像',
  `author_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者名称',
  `author_needs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '作者需求',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务接单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_balance
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_balance`;
CREATE TABLE `t_dzuser_balance`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `user_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '用户余额',
  `wallet_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '钱包地址',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_6sarsqj06miqemtr3ruat4v09`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50457 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户余额' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_compensation
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_compensation`;
CREATE TABLE `t_dzuser_compensation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `addition_and_subtraction` int NULL DEFAULT NULL COMMENT '加减',
  `former_credit_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '前余额',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `operator` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作员',
  `post_credit_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '后余额',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `create_time`(`create_time` ASC) USING BTREE,
  INDEX `addition_and_subtraction`(`addition_and_subtraction` ASC) USING BTREE,
  INDEX `money`(`money` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 148926 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '补分记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_credit
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_credit`;
CREATE TABLE `t_dzuser_credit`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `credit_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '信用分',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信用' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_falsedata
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_falsedata`;
CREATE TABLE `t_dzuser_falsedata`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `false_date` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '造假数据',
  `false_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '造假方式 1.提现记录 2.交易记录(CDB返佣) 3.用户下级',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `is_del` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否删除',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '造假记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzuser_falsetotal
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_falsetotal`;
CREATE TABLE `t_dzuser_falsetotal`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `balance` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收入明细',
  `profit_of_the_day` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当日收益',
  `team_report` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '团队报告',
  `team_size` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '团队规模',
  `total_revenue` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '总收入',
  `total_withdrawal_amount` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '可用金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '造假统计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzuser_history
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_history`;
CREATE TABLE `t_dzuser_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `addition_and_subtraction` int NULL DEFAULT NULL COMMENT '加减',
  `credit_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '信用分',
  `former_credit_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '前信用分',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `operation_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '操作原因',
  `post_credit_score` decimal(30, 6) NULL DEFAULT NULL COMMENT '后信用分',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '信用记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_missionincome
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_missionincome`;
CREATE TABLE `t_dzuser_missionincome`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `income_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收益类型',
  `mission_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `post_task_gross_revenue` decimal(30, 6) NULL DEFAULT NULL COMMENT '后任务总收入',
  `post_team_task_total_revenue` decimal(30, 6) NULL DEFAULT NULL COMMENT '后团队任务总收入',
  `source_account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源账号',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `task_idw` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务编号',
  `total_revenue_from_previous_team_tasks` decimal(30, 6) NULL DEFAULT NULL COMMENT '前团队任务总收入',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务收益记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_rechargehistory
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_rechargehistory`;
CREATE TABLE `t_dzuser_rechargehistory`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `after_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '后余额',
  `channel_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通道名称',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `order_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `previous_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '前余额',
  `recharge_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '充值状态',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `withdrawal_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提现地址',
  `fidw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '造假数据唯一值',
  `first_charge` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否首充',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `account`(`account` ASC) USING BTREE,
  INDEX `money`(`money` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56925 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_task
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_task`;
CREATE TABLE `t_dzuser_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_task_revenue` decimal(30, 6) NULL DEFAULT NULL COMMENT '任务总收入',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_tk7it06sma6vi8swj8pw4o1qv`(`uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务总收入' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_teammissionincome
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_teammissionincome`;
CREATE TABLE `t_dzuser_teammissionincome`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `income_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收益类型',
  `mission_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `post_team_task_total_revenue` decimal(30, 6) NULL DEFAULT NULL COMMENT '后团队任务总收入',
  `source_account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源账号',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `task_idw` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务编号',
  `total_revenue_from_previous_team_tasks` decimal(30, 6) NULL DEFAULT NULL COMMENT '前团队任务总收入',
  `total_team_task_revenue` decimal(30, 6) NULL DEFAULT NULL COMMENT '团队任务总收入',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团队任务收益记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_teamtask
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_teamtask`;
CREATE TABLE `t_dzuser_teamtask`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_team_task_revenue` decimal(30, 6) NULL DEFAULT NULL COMMENT '团队任务总收入',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_doulc498rqd7634tv2j1ypa22`(`uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团队任务总收入' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_totalbonus
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_totalbonus`;
CREATE TABLE `t_dzuser_totalbonus`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_bonus_income` decimal(30, 6) NULL DEFAULT NULL COMMENT '赠送彩金总收入',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_pbngb33fignruaeq80tk2x4rm`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50438 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '赠送彩金总收入' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_totalrecharge
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_totalrecharge`;
CREATE TABLE `t_dzuser_totalrecharge`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_recharge_amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '充值总金额',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_kvbxwlh0m8sxbr841hism9y8d`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18331 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值总金额' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_totalwithdrawal
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_totalwithdrawal`;
CREATE TABLE `t_dzuser_totalwithdrawal`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_withdrawal_amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '提现总金额',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_ou97s4wro8to76lpifth8y2fv`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22842 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '提现总金额' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_transaction
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_transaction`;
CREATE TABLE `t_dzuser_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `addition_and_subtraction` int NULL DEFAULT NULL COMMENT '加减',
  `after_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '后余额',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `order_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `previous_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '前余额',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `transaction_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易编号',
  `transaction_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易类型',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `fidw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '造假数据唯一值',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `transaction_type`(`transaction_type` ASC) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `addition_and_subtraction`(`addition_and_subtraction` ASC) USING BTREE,
  INDEX `money`(`money` ASC) USING BTREE,
  INDEX `create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1698020 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '交易记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_user
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_user`;
CREATE TABLE `t_dzuser_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `authenticator_password` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '验证器密码',
  `country_code_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家代号',
  `dzstatus` int NULL DEFAULT NULL COMMENT '状态',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邀请码',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `payment_password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付密码',
  `register_ip` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册ip',
  `registration_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `superior_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级邀请码',
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户类型',
  `vip_expire_date` datetime NULL DEFAULT NULL COMMENT 'ViP到期时间',
  `vip_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ViP类型',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `head_portrait_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `total_bonus_income` tinyblob NULL,
  `levels` int NULL DEFAULT NULL COMMENT '级别',
  `pinvitation_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '父级递归',
  `last_ip` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '最后一次登录ip',
  `last_time` datetime NULL DEFAULT NULL COMMENT '最后一次登录时间',
  `limit_buy_cdb` int NULL DEFAULT NULL COMMENT '限制购买CDB',
  `limit_drawing` int NULL DEFAULT NULL COMMENT '限制提款',
  `limit_profit` int NULL DEFAULT NULL COMMENT '限制收益',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `last_ip_city` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '最后一次登录ip区域',
  `register_ip_city` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册ip',
  `fidw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '造假数据唯一值',
  `limit_code` int NULL DEFAULT NULL COMMENT '限制邀请码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_boh4eue2ksvxxfm4bvqb4q166`(`invitation_code` ASC) USING BTREE,
  UNIQUE INDEX `UK_jsujdxrrbirjhubx0qxn7qmv3`(`account` ASC) USING BTREE,
  INDEX `vip_type`(`vip_type` ASC) USING BTREE,
  INDEX `levels`(`levels` ASC) USING BTREE,
  INDEX `dzstatus`(`dzstatus` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51062 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzuser_userbalancelocklog
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_userbalancelocklog`;
CREATE TABLE `t_dzuser_userbalancelocklog`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzstatus` int NULL DEFAULT NULL COMMENT '状态',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户余额日志锁记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzuser_withdrawals
-- ----------------------------
DROP TABLE IF EXISTS `t_dzuser_withdrawals`;
CREATE TABLE `t_dzuser_withdrawals`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `after_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '后余额',
  `channel_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付通道',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `order_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `previous_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '前余额',
  `recharge_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核状态',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `transaction_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易编号',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `amount_received` decimal(30, 6) NULL DEFAULT NULL COMMENT '到账金额',
  `handling_fee` decimal(30, 6) NULL DEFAULT NULL COMMENT '提现手续费',
  `withdrawal_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提现地址',
  `user_info` tinyblob NULL,
  `up_withdrawal_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上次提现地址',
  `operator` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作员',
  `fidw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '造假数据唯一值',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `account`(`account` ASC) USING BTREE,
  INDEX `money`(`money` ASC) USING BTREE,
  INDEX `create_time`(`create_time` ASC) USING BTREE,
  INDEX `modify_time`(`modify_time` ASC) USING BTREE,
  INDEX `recharge_status`(`recharge_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 87003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '提现记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzvip_byviptotalmoney
-- ----------------------------
DROP TABLE IF EXISTS `t_dzvip_byviptotalmoney`;
CREATE TABLE `t_dzvip_byviptotalmoney`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_money` decimal(30, 6) NULL DEFAULT NULL COMMENT '累计购买vip金额',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idw`(`idw` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48281 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户购买VIP累计金额' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_dzvip_teamvip
-- ----------------------------
DROP TABLE IF EXISTS `t_dzvip_teamvip`;
CREATE TABLE `t_dzvip_teamvip`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `team_vip_opening_total_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT '团队vip开通总返佣',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_7m7dcw3x5rjf5ogkffy9pd963`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3861 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团队vip开通总返佣' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzvip_vipmessage
-- ----------------------------
DROP TABLE IF EXISTS `t_dzvip_vipmessage`;
CREATE TABLE `t_dzvip_vipmessage`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `dzstatus` int NULL DEFAULT NULL COMMENT '状态',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `l1_recharge_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L1充值返佣',
  `l1_registration_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L1注册返佣',
  `l1_task_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L1任务返佣',
  `l2_recharge_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L2充值返佣',
  `l2_registration_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L2注册返佣',
  `l2_task_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L2任务返佣',
  `l3_recharge_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L3充值返佣',
  `l3_registration_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L3注册返佣',
  `l3_task_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L3任务返佣',
  `maximum_withdrawal` decimal(30, 6) NULL DEFAULT NULL COMMENT '最高提现',
  `minimum_withdrawal` decimal(30, 6) NULL DEFAULT NULL COMMENT '最低提现',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `number_of_tasks` int NULL DEFAULT NULL COMMENT '任务数量',
  `selling_price` decimal(30, 6) NULL DEFAULT NULL COMMENT '售价',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `valid_date` int NULL DEFAULT NULL COMMENT '有效天数',
  `vip_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ViP类型',
  `withdrawal_fee` decimal(30, 6) NULL DEFAULT NULL COMMENT '提现手续费',
  `vip_img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ViP图片',
  `daily_income` decimal(30, 6) NOT NULL COMMENT '每日收入',
  `l1_openvip_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L1开通VIP返佣',
  `l2_openvip_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L2开通VIP返佣',
  `l3_openvip_rebate` decimal(30, 6) NULL DEFAULT NULL COMMENT 'L3开通VIP返佣',
  `nick` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '别名/昵称',
  `vip_back_ground` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ViP背景',
  `limit_num` int NOT NULL COMMENT '限制提现次数',
  `power_bank_img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '充电中设备图片',
  `gear_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '档次code(字典)',
  `cancel_refund` decimal(30, 6) NULL DEFAULT NULL COMMENT '充电宝到期退款比',
  `operate_num` int NULL DEFAULT NULL COMMENT '最大运行次数',
  `lang_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会员详情多语言key',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_oenv1wd71tmfl3kw0nby5nutv`(`vip_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Vip信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzvip_vippurchase
-- ----------------------------
DROP TABLE IF EXISTS `t_dzvip_vippurchase`;
CREATE TABLE `t_dzvip_vippurchase`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `after_vip_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '之后ViP类型',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `number_of_tasks` int NULL DEFAULT NULL COMMENT '任务数量',
  `payment_amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '支付金额',
  `payment_method` int NULL DEFAULT NULL COMMENT '支付方式',
  `previous_vip_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '之前ViP类型',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `valid_date` int NULL DEFAULT NULL COMMENT '有效天数',
  `whether_to_pay` int NULL DEFAULT NULL COMMENT '是否支付',
  `daily_income` int NULL DEFAULT NULL COMMENT '每日收入',
  `deposit_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '充值地址',
  `channel_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通道名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_qnglapis2cfxq3k648ws3hnje`(`idw` ASC) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `account`(`account` ASC) USING BTREE,
  INDEX `whether_to_pay`(`whether_to_pay` ASC) USING BTREE,
  INDEX `payment_amount`(`payment_amount` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83450 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Vip购买记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dzvip_viprebaterecord
-- ----------------------------
DROP TABLE IF EXISTS `t_dzvip_viprebaterecord`;
CREATE TABLE `t_dzvip_viprebaterecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `amount_after` decimal(30, 6) NULL DEFAULT NULL COMMENT '后金额',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `previous_amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '前金额',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `source_user_account` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源用户账号',
  `relevels` int NULL DEFAULT NULL COMMENT '相对层级',
  `new_vip_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购买ViP类型',
  `old_vip_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原来ViP类型',
  `fidw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '造假数据唯一值',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `money`(`money` ASC) USING BTREE,
  INDEX `source_invitation_code`(`source_invitation_code` ASC) USING BTREE,
  INDEX `create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23700 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'vip返佣记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sw_purchasehistory
-- ----------------------------
DROP TABLE IF EXISTS `t_sw_purchasehistory`;
CREATE TABLE `t_sw_purchasehistory`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `cycle_product_purchases` decimal(30, 6) NULL DEFAULT NULL COMMENT '周期产品购买记录总支出',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_kpathdh5ol3i0m9bf5gitijyu`(`uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '周期产品购买记录总支出' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sw_sharedbalance
-- ----------------------------
DROP TABLE IF EXISTS `t_sw_sharedbalance`;
CREATE TABLE `t_sw_sharedbalance`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_shared_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '总共享余额',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_6574xr6glnhibiiqrakr3bb7l`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '共享余额' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sw_totalreturnon
-- ----------------------------
DROP TABLE IF EXISTS `t_sw_totalreturnon`;
CREATE TABLE `t_sw_totalreturnon`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `total_return_on` decimal(30, 6) NULL DEFAULT NULL COMMENT '周期产品总收益',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_borl5ci89p68n3itbtgqwj8uc`(`uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '周期产品总收益' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sw_totalreturnonrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_sw_totalreturnonrecord`;
CREATE TABLE `t_sw_totalreturnonrecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `cycle_product_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '周期产品编号',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `profit_percentage` decimal(30, 6) NULL DEFAULT NULL COMMENT '收益百分比',
  `shared_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '共享余额',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `amount_after` decimal(30, 6) NULL DEFAULT NULL COMMENT '后余额',
  `previous_amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '前余额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '周期产品收益记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sw_transferrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_sw_transferrecord`;
CREATE TABLE `t_sw_transferrecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `addition_and_subtraction` int NULL DEFAULT NULL COMMENT '加减',
  `after_money` decimal(30, 6) NULL DEFAULT NULL COMMENT '转账后余额',
  `former_money` decimal(30, 6) NULL DEFAULT NULL COMMENT '转账前余额',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `order_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '划转记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sw_transferrecordhistory
-- ----------------------------
DROP TABLE IF EXISTS `t_sw_transferrecordhistory`;
CREATE TABLE `t_sw_transferrecordhistory`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `cycle_product_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '周期产品编号',
  `expire_date` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  `profit_percentage` decimal(30, 6) NULL DEFAULT NULL COMMENT '收益百分比',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `amount_after` decimal(30, 6) NULL DEFAULT NULL COMMENT '后金额',
  `previous_amount` decimal(30, 6) NULL DEFAULT NULL COMMENT '前金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '周期产品购买记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sw_transfertotalbalance
-- ----------------------------
DROP TABLE IF EXISTS `t_sw_transfertotalbalance`;
CREATE TABLE `t_sw_transfertotalbalance`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `source_invitation_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源邀请码',
  `transfer_total_balance` decimal(30, 6) NULL DEFAULT NULL COMMENT '划转总余额',
  `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
  `dzversion` int NULL DEFAULT NULL COMMENT 'version',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_stoduwkc2ta7m56rn7xhte2f8`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '划转总余额' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_swcp_cycleproduct
-- ----------------------------
DROP TABLE IF EXISTS `t_swcp_cycleproduct`;
CREATE TABLE `t_swcp_cycleproduct`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `dzstatus` int NULL DEFAULT NULL COMMENT '状态',
  `idw` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一值',
  `lock_days` int NULL DEFAULT NULL COMMENT '锁定天数',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `profit_percentage` decimal(30, 6) NULL DEFAULT NULL COMMENT '收益百分比',
  `money` decimal(30, 6) NULL DEFAULT NULL COMMENT '金额',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_dki3u4pew0mm36wav6ruhrtyr`(`idw` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '周期产品' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_cfg
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_cfg`;
CREATE TABLE `t_sys_cfg`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `cfg_desc` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注',
  `cfg_name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数名',
  `cfg_value` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统参数' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dept`;
CREATE TABLE `t_sys_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `fullname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `num` int NULL DEFAULT NULL,
  `pid` bigint NULL DEFAULT NULL,
  `pids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `simplename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `tips` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `version` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '部门' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dict`;
CREATE TABLE `t_sys_dict`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pid` bigint NULL DEFAULT NULL,
  `tips` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 844 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '字典' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_file_info
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_file_info`;
CREATE TABLE `t_sys_file_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `original_file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `real_file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11418 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_login_log`;
CREATE TABLE `t_sys_login_log`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `logname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `succeed` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `userid` int NULL DEFAULT NULL,
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 480311 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '登录日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_menu`;
CREATE TABLE `t_sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `code` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `component` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面组件',
  `hidden` tinyint NULL DEFAULT NULL COMMENT '是否隐藏',
  `icon` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `ismenu` int NOT NULL COMMENT '是否是菜单1:菜单,0:按钮',
  `isopen` int NULL DEFAULT NULL COMMENT '是否默认打开1:是,0:否',
  `levels` int NOT NULL COMMENT '级别',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `num` int NOT NULL COMMENT '顺序',
  `pcode` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父菜单编号',
  `pcodes` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '递归父级菜单编号',
  `status` int NOT NULL COMMENT '状态1:启用,0:禁用',
  `tips` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '鼠标悬停提示信息',
  `url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_s37unj3gh67ujhk83lqva8i1t`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 218 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_operation_log`;
CREATE TABLE `t_sys_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `classname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `logname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `logtype` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `message` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '详细信息',
  `method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `succeed` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `userid` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41411 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '操作日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_relation`;
CREATE TABLE `t_sys_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menuid` bigint NULL DEFAULT NULL,
  `roleid` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15226 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单角色关系' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `deptid` bigint NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `num` int NULL DEFAULT NULL,
  `pid` bigint NULL DEFAULT NULL,
  `tips` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `version` int NULL DEFAULT NULL,
  `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_task
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_task`;
CREATE TABLE `t_sys_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `concurrent` tinyint NULL DEFAULT NULL COMMENT '是否允许并发',
  `cron` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '定时规则',
  `data` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '执行参数',
  `disabled` tinyint NULL DEFAULT NULL COMMENT '是否禁用',
  `exec_at` datetime NULL DEFAULT NULL COMMENT '执行时间',
  `exec_result` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '执行结果',
  `job_class` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行类',
  `job_group` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务组名',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务名',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '定时任务' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_task_log
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_task_log`;
CREATE TABLE `t_sys_task_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exec_at` datetime NULL DEFAULT NULL COMMENT '执行时间',
  `exec_success` int NULL DEFAULT NULL COMMENT '执行结果（成功:1、失败:0)',
  `id_task` bigint NULL DEFAULT NULL,
  `job_exception` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '抛出异常',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 307814 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '定时任务日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user`;
CREATE TABLE `t_sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间/注册时间',
  `modify_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `account` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账户',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `birthday` datetime NULL DEFAULT NULL,
  `deptid` bigint NULL DEFAULT NULL,
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'email',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `roleid` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色id列表，以逗号分隔',
  `salt` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码盐',
  `sex` int NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  `version` int NULL DEFAULT NULL,
  `ucode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邀请码',
  `authenticator_password` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '验证器密码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_br7fdjv2mbueuylbw1k5dmron`(`ucode` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 82 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '账号' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
