/*
 Navicat Premium Data Transfer

 Source Server         : docker-3307
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : example-host:3307
 Source Schema         : jixianda

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 07/03/2026 11:34:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `type` int NULL DEFAULT NULL COMMENT '绫诲瀷   1 鑿滃搧鍒嗙被 2 濂楅鍒嗙被',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '鍒嗙被鍚嶇О',
  `sort` int NOT NULL DEFAULT 0 COMMENT '椤哄簭',
  `status` int NULL DEFAULT NULL COMMENT '鍒嗙被鐘舵€?0:绂佺敤锛?:鍚敤',
  `create_time` datetime NULL DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NULL DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `create_user` bigint NULL DEFAULT NULL COMMENT '鍒涘缓浜?,
  `update_user` bigint NULL DEFAULT NULL COMMENT '淇敼浜?,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_category_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '鑿滃搧鍙婂椁愬垎绫? ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (11, 1, '閰掓按楗枡', 10, 1, '2022-06-09 22:09:18', '2026-03-05 15:22:40', 1, 1);
INSERT INTO `category` VALUES (12, 1, '浼犵粺涓婚', 9, 1, '2022-06-09 22:09:32', '2026-03-05 15:33:26', 1, 1);

-- ----------------------------
-- Table structure for dish
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '鑿滃搧鍚嶇О',
  `category_id` bigint NOT NULL COMMENT '鑿滃搧鍒嗙被id',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '鑿滃搧浠锋牸',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍥剧墖',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鎻忚堪淇℃伅',
  `status` int NULL DEFAULT 1 COMMENT '0 鍋滃敭 1 璧峰敭',
  `stock` int NULL DEFAULT 100 COMMENT '搴撳瓨(鏂板瀛楁,鐢ㄤ簬绉掓潃)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NULL DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `create_user` bigint NULL DEFAULT NULL COMMENT '鍒涘缓浜?,
  `update_user` bigint NULL DEFAULT NULL COMMENT '淇敼浜?,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_dish_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '鑿滃搧' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dish
-- ----------------------------
INSERT INTO `dish` VALUES (46, '鐜嬭€佸悏', 11, 6.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', '', 1, 100, '2022-06-09 22:40:47', '2022-06-09 22:40:47', 1, 1);
INSERT INTO `dish` VALUES (70, '鏋侀矞绉掓潃鐗逛緵', 11, 1.00, 'https://example-bucket.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png', '鎵嬫參鏃?, 1, 100, '2026-03-01 10:00:00', '2026-03-04 21:21:17', 1, 1);

-- ----------------------------
-- Table structure for dish_flavor
-- ----------------------------
DROP TABLE IF EXISTS `dish_flavor`;
CREATE TABLE `dish_flavor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `dish_id` bigint NOT NULL COMMENT '鑿滃搧',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍙ｅ懗鍚嶇О',
  `value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍙ｅ懗鏁版嵁list',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '鑿滃搧鍙ｅ懗鍏崇郴琛? ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dish_flavor
-- ----------------------------
INSERT INTO `dish_flavor` VALUES (105, 70, '鐢滃懗', '[\"鏃犵硸\",\"灏戠硸\",\"鍗婄硸\",\"澶氱硸\",\"鍏ㄧ硸\"]');

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '濮撳悕',
  `username` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '鐢ㄦ埛鍚?,
  `password` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '瀵嗙爜',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '鎵嬫満鍙?,
  `sex` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '鎬у埆',
  `id_number` varchar(18) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '韬唤璇佸彿',
  `status` int NOT NULL DEFAULT 1 COMMENT '鐘舵€?0:绂佺敤锛?:鍚敤',
  `create_time` datetime NULL DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NULL DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `create_user` bigint NULL DEFAULT NULL COMMENT '鍒涘缓浜?,
  `update_user` bigint NULL DEFAULT NULL COMMENT '淇敼浜?,
  `warehouse_id` bigint NULL DEFAULT NULL COMMENT '褰掑睘浠撳簱ID(0浠ｈ〃鎬婚儴)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '鍛樺伐淇℃伅' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
-- Demo account note: password hash below corresponds to the demonstration password DemoPass_2026!.
INSERT INTO employee VALUES (1, 'demo_admin_user', 'demo_admin', '74e310e4a9496b6c3f505c49c1893c03', 'demo0000001', '1', 'demo-id-000000001', 1, '2022-02-15 15:51:20', '2022-02-17 09:16:20', 10, 1, NULL);
INSERT INTO employee VALUES (2, 'demo_operator_user_a', 'demo_operator_a', '74e310e4a9496b6c3f505c49c1893c03', 'demo0000002', '1', 'demo-id-000000002', 1, '2026-03-05 15:54:35', '2026-03-05 15:54:35', 1, 1, 1);

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍚嶅瓧',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍥剧墖',
  `order_id` bigint NOT NULL COMMENT '璁㈠崟id',
  `dish_id` bigint NULL DEFAULT NULL COMMENT '鑿滃搧id',
  `setmeal_id` bigint NULL DEFAULT NULL COMMENT '濂楅id',
  `dish_flavor` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍙ｅ懗',
  `number` int NOT NULL DEFAULT 1 COMMENT '鏁伴噺',
  `amount` decimal(10, 2) NOT NULL COMMENT '閲戦',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '璁㈠崟鏄庣粏琛? ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_detail
-- ----------------------------
INSERT INTO `order_detail` VALUES (1, '鐜嬭€佸悏', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 1, 46, NULL, '', 1, 6.00);
INSERT INTO `order_detail` VALUES (2, '鐜嬭€佸悏', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 2, 46, NULL, '', 1, 6.00);
INSERT INTO `order_detail` VALUES (3, '鐜嬭€佸悏', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 3, 46, NULL, '', 1, 6.00);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `number` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '璁㈠崟鍙?,
  `status` int NOT NULL DEFAULT 1 COMMENT '璁㈠崟鐘舵€?1寰呬粯娆?2寰呮帴鍗?3宸叉帴鍗?4娲鹃€佷腑 5宸插畬鎴?6宸插彇娑?7閫€娆?,
  `user_id` bigint NOT NULL COMMENT '涓嬪崟鐢ㄦ埛',
  `address_book_id` bigint NOT NULL COMMENT '鍦板潃id',
  `order_time` datetime NOT NULL COMMENT '涓嬪崟鏃堕棿',
  `checkout_time` datetime NULL DEFAULT NULL COMMENT '缁撹处鏃堕棿',
  `pay_method` int NOT NULL DEFAULT 1 COMMENT '鏀粯鏂瑰紡 1寰俊,2鏀粯瀹?,
  `pay_status` tinyint NOT NULL DEFAULT 0 COMMENT '鏀粯鐘舵€?0鏈敮浠?1宸叉敮浠?2閫€娆?,
  `amount` decimal(10, 2) NOT NULL COMMENT '瀹炴敹閲戦',
  `remark` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '澶囨敞',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鎵嬫満鍙?,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍦板潃',
  `user_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鐢ㄦ埛鍚嶇О',
  `consignee` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鏀惰揣浜?,
  `cancel_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '璁㈠崟鍙栨秷鍘熷洜',
  `rejection_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '璁㈠崟鎷掔粷鍘熷洜',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '璁㈠崟鍙栨秷鏃堕棿',
  `estimated_delivery_time` datetime NULL DEFAULT NULL COMMENT '棰勮閫佽揪鏃堕棿',
  `delivery_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '閰嶉€佺姸鎬? 1绔嬪嵆閫佸嚭  0閫夋嫨鍏蜂綋鏃堕棿',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '閫佽揪鏃堕棿',
  `pack_amount` int NULL DEFAULT NULL COMMENT '鎵撳寘璐?,
  `tableware_number` int NULL DEFAULT NULL COMMENT '椁愬叿鏁伴噺',
  `tableware_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '椁愬叿鏁伴噺鐘舵€? 1鎸夐閲忔彁渚? 0閫夋嫨鍏蜂綋鏁伴噺',
  `warehouse_id` bigint NULL DEFAULT NULL COMMENT '浠撳簱ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '璁㈠崟琛? ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO orders VALUES (1, 'c9ed741889c44b8c8981fb8fdb3a84db', 6, 1, 2, '2026-03-03 23:24:04', NULL, 1, 0, 6.00, 'E2E demo order', 'demo0000010', 'Demo Campus Building A', 'demo_user_a', 'demo_receiver_a', 'timeout unpaid', NULL, '2026-03-03 23:25:04', NULL, 1, NULL, 0, 0, 0, 1);
INSERT INTO orders VALUES (2, '1772551957890171685', 6, 1, 2, '2026-03-03 23:32:38', NULL, 1, 0, 6.00, 'E2E demo order', 'demo0000010', 'Demo Campus Building A', 'demo_user_a', 'demo_receiver_a', 'timeout unpaid', NULL, '2026-03-03 23:33:38', NULL, 1, NULL, 0, 0, 0, 1);
INSERT INTO orders VALUES (3, '1772552075008248626', 5, 1, 2, '2026-03-03 23:34:35', '2026-03-03 23:34:35', 1, 1, 6.00, 'E2E demo order', 'demo0000010', 'Demo Campus Building A', 'demo_user_a', 'demo_receiver_a', NULL, NULL, NULL, NULL, 1, '2026-03-04 21:23:20', 0, 0, 0, 1);

-- ----------------------------
-- Table structure for setmeal
-- ----------------------------
DROP TABLE IF EXISTS `setmeal`;
CREATE TABLE `setmeal`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `category_id` bigint NOT NULL COMMENT '鑿滃搧鍒嗙被id',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '濂楅鍚嶇О',
  `price` decimal(10, 2) NOT NULL COMMENT '濂楅浠锋牸',
  `status` int NULL DEFAULT 1 COMMENT '鍞崠鐘舵€?0:鍋滃敭 1:璧峰敭',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鎻忚堪淇℃伅',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍥剧墖',
  `create_time` datetime NULL DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NULL DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `create_user` bigint NULL DEFAULT NULL COMMENT '鍒涘缓浜?,
  `update_user` bigint NULL DEFAULT NULL COMMENT '淇敼浜?,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_setmeal_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '濂楅' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of setmeal
-- ----------------------------

-- ----------------------------
-- Table structure for setmeal_dish
-- ----------------------------
DROP TABLE IF EXISTS `setmeal_dish`;
CREATE TABLE `setmeal_dish`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `setmeal_id` bigint NULL DEFAULT NULL COMMENT '濂楅id',
  `dish_id` bigint NULL DEFAULT NULL COMMENT '鑿滃搧id',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鑿滃搧鍚嶇О 锛堝啑浣欏瓧娈碉級',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '鑿滃搧鍗曚环锛堝啑浣欏瓧娈碉級',
  `copies` int NULL DEFAULT NULL COMMENT '鑿滃搧浠芥暟',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '濂楅鑿滃搧鍏崇郴' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of setmeal_dish
-- ----------------------------

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍟嗗搧鍚嶇О',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍥剧墖',
  `user_id` bigint NOT NULL COMMENT '涓婚敭',
  `dish_id` bigint NULL DEFAULT NULL COMMENT '鑿滃搧id',
  `setmeal_id` bigint NULL DEFAULT NULL COMMENT '濂楅id',
  `dish_flavor` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '鍙ｅ懗',
  `number` int NOT NULL DEFAULT 1 COMMENT '鏁伴噺',
  `amount` decimal(10, 2) NOT NULL COMMENT '閲戦',
  `create_time` datetime NULL DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `warehouse_id` bigint NULL DEFAULT NULL COMMENT '浠撳簱ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '璐墿杞? ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shopping_cart
-- ----------------------------

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `branch_id` bigint NOT NULL,
  `xid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime(6) NOT NULL,
  `log_modified` datetime(6) NOT NULL,
  UNIQUE INDEX `ux_undo_log`(`xid` ASC, `branch_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

-- ----------------------------
-- Table structure for warehouse
-- ----------------------------
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浠撳簱鍚?,
  `location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '缁忕含搴?,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍦板潃',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '鐘舵€?0绂佺敤 1鍚敤',
  `contact_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '璐熻矗浜?,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '鑱旂郴鐢佃瘽',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_warehouse_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鍓嶇疆浠? ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warehouse
-- ----------------------------
INSERT INTO `warehouse` VALUES (1, '鍖椾含鏈濋槼鍓嶇疆浠?, '39.9042,116.4074', '鍖椾含甯傛湞闃冲尯鍥借锤CBD', 1, NULL, NULL);
INSERT INTO `warehouse` VALUES (2, '涓婃捣涓績鍓嶇疆浠?, '31.2304,121.4737', '涓婃捣甯傛郸涓滄柊鍖?, 1, NULL, NULL);
INSERT INTO `warehouse` VALUES (3, '鏉窞钀у北鍓嶇疆浠?, '30.2833锛?20.4932', '鏉窞钀у北鍖?, 1, NULL, NULL);

-- ----------------------------
-- Table structure for warehouse_sku
-- ----------------------------
DROP TABLE IF EXISTS `warehouse_sku`;
CREATE TABLE `warehouse_sku`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `warehouse_id` bigint NOT NULL COMMENT '浠撳簱ID',
  `dish_id` bigint NOT NULL COMMENT '鍟嗗搧ID',
  `stock` int NOT NULL DEFAULT 0 COMMENT '鐗╃悊搴撳瓨',
  `lock_stock` int NOT NULL DEFAULT 0 COMMENT '閿佸畾搴撳瓨',
  `status` tinyint NULL DEFAULT 1 COMMENT '浠撳簱鍐呭敭鍗栫姸鎬?,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_warehouse_dish`(`warehouse_id` ASC, `dish_id` ASC) USING BTREE,
  INDEX `idx_warehouse_sku_dish`(`dish_id` ASC) USING BTREE,
  CONSTRAINT `fk_warehouse_sku_dish` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_warehouse_sku_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '浠撳簱搴撳瓨' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warehouse_sku
-- ----------------------------
INSERT INTO `warehouse_sku` VALUES (1, 1, 46, 99, 1, 0);
INSERT INTO `warehouse_sku` VALUES (2, 2, 46, 10, 0, 1);
INSERT INTO `warehouse_sku` VALUES (3, 1, 70, 10, 0, 0);
INSERT INTO `warehouse_sku` VALUES (4, 2, 70, 20, 0, 1);
INSERT INTO `warehouse_sku` VALUES (5, 3, 70, 4, 0, 1);

SET FOREIGN_KEY_CHECKS = 1;


