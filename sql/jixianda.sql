DROP TABLE IF EXISTS `address_book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address_book` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `consignee` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `sex` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `province_code` varchar(12) COLLATE utf8mb3_bin DEFAULT NULL,
  `province_name` varchar(32) COLLATE utf8mb3_bin DEFAULT NULL,
  `city_code` varchar(12) COLLATE utf8mb3_bin DEFAULT NULL,
  `city_name` varchar(32) COLLATE utf8mb3_bin DEFAULT NULL,
  `district_code` varchar(12) COLLATE utf8mb3_bin DEFAULT NULL,
  `district_name` varchar(32) COLLATE utf8mb3_bin DEFAULT NULL,
  `detail` varchar(200) COLLATE utf8mb3_bin DEFAULT NULL,
  `label` varchar(100) COLLATE utf8mb3_bin DEFAULT NULL,
  `is_default` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=217 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `address_book` WRITE;
/*!40000 ALTER TABLE `address_book` DISABLE KEYS */;
INSERT INTO `address_book` VALUES (2,1,'架构师小张','1','13812345678','110000','北京市','110100','北京市','110101','东城区','极鲜达总部大楼','2',1),(3,4,'程先生','0','18121695592','23','黑龙江省','2301','哈尔滨市','230103','南岗区','东林','1',1),(201,101,'林知夏','2','13810010001','310000','上海市','310100','上海市','310106','静安区','南京西路688号兴业太古汇2座1808室','家',1),(202,101,'林知夏','2','13810010001','310000','上海市','310100','上海市','310104','徐汇区','桂平路680号创客邦A座607室','公司',0),(203,102,'周明远','1','13810010002','310000','上海市','310100','上海市','310115','浦东新区','银城中路501号上海中心B1层','公司',1),(204,102,'周明远','1','13810010002','310000','上海市','310100','上海市','310115','浦东新区','商城路1900号金桃大厦1502室','家',0),(205,103,'陈语桐','2','13810010003','310000','上海市','310100','上海市','310104','徐汇区','虹梅路1905号远中科研楼1号楼801室','公司',1),(206,103,'陈语桐','2','13810010003','310000','上海市','310100','上海市','310106','静安区','万航渡路889号悦达889广场12楼','家',0),(207,104,'许安然','2','13810010004','110000','北京市','110100','北京市','110108','海淀区','中关村大街27号中关村大厦B座902室','公司',1),(208,104,'许安然','2','13810010004','110000','北京市','110100','北京市','110105','朝阳区','阜通东大街6号方恒国际中心B座1205室','家',0),(209,105,'宋嘉禾','1','13810010005','110000','北京市','110100','北京市','110105','朝阳区','广顺北大街33号凯德MALL望京写字楼905室','公司',1),(210,105,'宋嘉禾','1','13810010005','110000','北京市','110100','北京市','110108','海淀区','知春路7号致真大厦1508室','家',0),(211,106,'贺星野','1','13810010006','330000','浙江省','330100','杭州市','330109','萧山区','民和路800号宝盛世纪中心1幢1801室','家',1),(212,106,'贺星野','1','13810010006','330000','浙江省','330100','杭州市','330109','萧山区','金城路451号太古广场A座1006室','公司',0),(213,107,'唐可心','2','13810010007','310000','上海市','310100','上海市','310110','杨浦区','淞沪路98号万达广场A座1203室','家',1),(214,107,'唐可心','2','13810010007','310000','上海市','310100','上海市','310115','浦东新区','东方路800号宝安大厦808室','公司',0),(215,108,'叶舒宁','2','13810010008','110000','北京市','110100','北京市','110101','东城区','东直门外大街46号天恒大厦1005室','家',1),(216,108,'叶舒宁','2','13810010008','110000','北京市','110100','北京市','110105','朝阳区','酒仙桥路10号恒通国际创新园C6座501室','公司',0);
/*!40000 ALTER TABLE `address_book` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int DEFAULT NULL COMMENT '类型   1 菜品分类 2 套餐分类',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '顺序',
  `status` int DEFAULT NULL COMMENT '分类状态 0:禁用，1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_category_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='菜品及套餐分类';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (11,1,'酒水饮料',10,1,'2022-06-09 22:09:18','2026-03-05 15:22:40',1,1),(12,1,'传统主食',9,1,'2022-06-09 22:09:32','2026-03-12 23:31:17',1,1),(102,1,'休闲零食',30,1,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(103,1,'速食主食',40,1,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(104,1,'乳品早餐',50,1,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(105,1,'生鲜水果',60,1,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(106,1,'日用百货',70,1,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(107,1,'秒杀专区',80,1,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(108,1,'轻食简餐',90,1,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(109,1,'冷藏即食',100,1,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(110,1,'夜宵冻品',110,0,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(201,2,'套餐组合',10,1,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1),(202,2,'节日礼盒',20,0,'2026-03-10 10:00:00','2026-03-10 10:00:00',1,1);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `dish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '菜品名称',
  `category_id` bigint NOT NULL COMMENT '菜品分类id',
  `price` decimal(10,2) DEFAULT NULL COMMENT '菜品价格',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '描述信息',
  `status` int DEFAULT '1' COMMENT '0 停售 1 起售',
  `stock` int DEFAULT '100' COMMENT '库存(新增字段,用于秒杀)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_dish_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='菜品';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `dish` WRITE;
/*!40000 ALTER TABLE `dish` DISABLE KEYS */;
INSERT INTO `dish` VALUES (46,'王老吉',11,6.00,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png','',1,100,'2022-06-09 22:40:47','2026-03-12 16:41:40',1,1),(70,'极鲜秒杀特供',11,1.00,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png','手慢无',1,100,'2026-03-01 10:00:00','2026-03-04 21:21:17',1,1),(72,'馒头',12,2.00,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png','',1,100,'2026-03-12 21:25:27','2026-03-12 23:26:29',1,1),(201,'农夫山泉550ml',11,2.50,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/nongfu-550.jpg','即时补水爆款，多个前置仓常备库存。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(202,'王老吉310ml',11,5.50,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wanglaoji-310.jpg','凉茶类高频单品，适合午高峰和夜宵补货。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(203,'东方树叶青柑普洱500ml',11,6.80,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/dongfangshuye-qg.jpg','无糖茶饮，高客单白领用户偏好。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(204,'冰红茶500ml',11,4.00,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/binghongcha-500.jpg','常温饮料基础款，适合做组合加购。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(205,'蒙牛纯牛奶250ml',104,3.80,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/mengniu-milk-250.jpg','早餐配套基础乳品，多个仓作为常备品。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(206,'光明如实原味酸奶135g',104,6.50,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/guangming-yogurt-135.jpg','冷藏酸奶，适合早餐与下午茶补给。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(207,'全麦面包切片',104,9.90,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wholewheat-bread.jpg','早餐高频 SKU，可搭配牛奶和咖啡。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(208,'自热米饭红烧牛肉味',103,16.80,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/self-heating-rice-beef.jpg','适合即时零售场景的便捷主食。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(209,'经典桶装泡面',103,5.50,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/cup-noodle-classic.jpg','夜宵和加班场景高频复购商品。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(210,'鸡胸肉轻食沙拉',108,18.90,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/chicken-salad.jpg','白领午餐轻食 SKU，适合高客单测试。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(211,'缤纷水果杯',105,15.80,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/fruit-cup-mix.jpg','冷藏水果即食盒，适合下午茶场景。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(212,'三明治火腿芝士',109,12.80,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/sandwich-ham-cheese.jpg','便利即食三明治，适合早餐和通勤。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(213,'乐事原味薯片70g',102,7.50,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/lays-original-70.jpg','零食通用爆款，适合做多仓共有商品。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(214,'奥利奥夹心饼干',102,8.90,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/oreo-classic.jpg','适合家庭囤货和办公室零食补给。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(215,'维达抽纸3包装',106,11.90,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/vinda-tissue-3pack.jpg','日用品高频 SKU，适合测试大件轻抛货。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(216,'便携湿巾10片装',106,4.90,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wet-wipes-10.jpg','低客单引流商品，适合拼单和凑单。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(217,'Type-C快充数据线1米',106,19.90,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/typec-cable-1m.jpg','3C应急品，适合即时零售差异化测试。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(218,'一次性餐具包',106,2.00,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/disposable-cutlery-pack.jpg','当前全局停售，用于测试全局上下架。',0,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(219,'极鲜秒杀特供矿泉水',107,1.00,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/flash-sale-water.jpg','秒杀专区引流商品，适合高并发库存预占测试。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(220,'极鲜爆款早餐组合',108,22.80,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/breakfast-combo-hot.jpg','单品化组合 SKU，仍按 dish 交易，仓内可单独上下架。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(221,'冰美式咖啡330ml',109,12.90,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/iced-americano-330.jpg','办公室提神高频品，适合即时配送半小时达。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(222,'鲜切哈密瓜盒',105,13.90,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/hami-melon-box.jpg','当前全局停售，用于测试全局停售但保留历史库存记录。',0,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(223,'卤蛋双拼饭团',109,9.90,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/rice-ball-double-egg.jpg','适合早晚高峰的即食补能 SKU。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(224,'坚果混合装180g',102,23.50,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/mixed-nuts-180.jpg','高客单零食，适合测试商品搜索和库存管理。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1),(225,'无糖苏打水330ml',101,3.80,'https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/soda-water-330.jpg','低糖饮品补充，部分仓独有。',1,999,'2026-03-10 10:10:00','2026-03-10 10:10:00',1,1);
/*!40000 ALTER TABLE `dish` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `dish_flavor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish_flavor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dish_id` bigint NOT NULL COMMENT '菜品',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '口味名称',
  `value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '口味数据list',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='菜品口味关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `dish_flavor` WRITE;
/*!40000 ALTER TABLE `dish_flavor` DISABLE KEYS */;
INSERT INTO `dish_flavor` VALUES (105,70,'甜味','[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]'),(107,72,'','[]'),(301,202,'温度','[\"常温\",\"冷藏\"]'),(302,203,'温度','[\"常温\",\"冷藏\"]'),(303,204,'甜度','[\"无糖\",\"少糖\",\"标准\"]'),(304,206,'规格','[\"135g\",\"250g\"]'),(305,210,'酱料','[\"油醋汁\",\"黑胡椒汁\",\"凯撒汁\"]'),(306,211,'切块规格','[\"小份\",\"标准份\"]'),(307,212,'加热方式','[\"即食\",\"微波加热\"]'),(308,219,'活动规格','[\"单瓶\",\"3瓶装\",\"6瓶装\"]'),(309,221,'冰度','[\"常温\",\"少冰\",\"多冰\"]'),(310,223,'加热方式','[\"即食\",\"微波加热\"]');
/*!40000 ALTER TABLE `dish_flavor` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '姓名',
  `username` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '密码',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '性别',
  `id_number` varchar(18) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '身份证号',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态 0:禁用，1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `warehouse_id` bigint DEFAULT NULL COMMENT '归属仓库ID(0代表总部)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=608 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='员工信息';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'管理员','admin','e10adc3949ba59abbe56e057f20f883e','13812312312','1','110101199001010047',1,'2022-02-15 15:51:20','2022-02-17 09:16:20',10,1,NULL),(2,'仓库管理员A','xiaoyan','e10adc3949ba59abbe56e057f20f883e','15848190312','1','110105199001011234',1,'2026-03-05 15:54:35','2026-03-05 15:54:35',1,1,1),(601,'周砚北','zhouyanbei','e10adc3949ba59abbe56e057f20f883e','13990000001','1','110101198902021219',1,'2026-03-10 10:30:00','2026-03-10 10:30:00',1,1,0),(602,'韩若宁','hanruoning','e10adc3949ba59abbe56e057f20f883e','13990000002','2','310101199104163026',1,'2026-03-10 10:30:00','2026-03-10 10:30:00',1,1,401),(603,'邵景川','shaojingchuan','e10adc3949ba59abbe56e057f20f883e','13990000003','1','310115199211084516',1,'2026-03-10 10:30:00','2026-03-10 10:30:00',1,1,402),(604,'顾雨辰','guyuchen','e10adc3949ba59abbe56e057f20f883e','13990000004','1','310104199308125438',1,'2026-03-10 10:30:00','2026-03-10 10:30:00',1,1,403),(605,'程知南','chengzhinan','e10adc3949ba59abbe56e057f20f883e','13990000005','1','110105199012244252',1,'2026-03-10 10:30:00','2026-03-10 10:30:00',1,1,404),(606,'沈亦航','shenyihang','e10adc3949ba59abbe56e057f20f883e','13990000006','1','110108199305164677',0,'2026-03-10 10:30:00','2026-03-28 19:54:34',1,1,405),(607,'孙可心','sunkexin','e10adc3949ba59abbe56e057f20f883e','13990000007','2','310106199607086825',1,'2026-03-10 10:30:00','2026-03-10 10:30:00',1,1,401);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '名字',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '口味',
  `number` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=961 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `order_detail` WRITE;
/*!40000 ALTER TABLE `order_detail` DISABLE KEYS */;
INSERT INTO `order_detail` VALUES (1,'王老吉','https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png',1,46,NULL,'',1,6.00),(2,'王老吉','https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png',2,46,NULL,'',1,6.00),(3,'王老吉','https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png',3,46,NULL,'',1,6.00),(4,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',4,70,NULL,NULL,1,1.00),(5,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',5,70,NULL,NULL,1,1.00),(6,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',6,70,NULL,NULL,1,1.00),(7,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',7,70,NULL,NULL,1,1.00),(8,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',8,70,NULL,'无糖',1,1.00),(9,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',9,70,NULL,NULL,1,1.00),(10,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',10,46,NULL,NULL,3,6.00),(11,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',10,70,NULL,'无糖',1,1.00),(12,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',11,46,NULL,NULL,1,6.00),(13,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',11,70,NULL,'多糖',1,1.00),(14,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',11,70,NULL,'无糖',1,1.00),(15,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',12,46,NULL,NULL,1,6.00),(16,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',12,70,NULL,'无糖',2,1.00),(17,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',12,72,NULL,NULL,1,2.00),(18,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',13,72,NULL,NULL,1,2.00),(19,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',13,46,NULL,NULL,2,6.00),(20,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',14,46,NULL,NULL,1,6.00),(21,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',14,72,NULL,NULL,4,2.00),(22,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',15,46,NULL,NULL,2,6.00),(23,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',15,72,NULL,NULL,2,2.00),(24,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',16,46,NULL,NULL,2,6.00),(25,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',16,72,NULL,NULL,2,2.00),(26,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',17,46,NULL,NULL,2,6.00),(27,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',17,72,NULL,NULL,2,2.00),(28,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',18,46,NULL,NULL,4,6.00),(29,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',18,72,NULL,NULL,2,2.00),(30,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',19,46,NULL,NULL,2,6.00),(31,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',20,72,NULL,NULL,1,2.00),(32,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',20,46,NULL,NULL,2,6.00),(33,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',21,72,NULL,NULL,8,2.00),(34,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',22,72,NULL,NULL,2,2.00),(35,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',22,46,NULL,NULL,2,6.00),(36,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',23,72,NULL,NULL,2,2.00),(37,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',23,46,NULL,NULL,1,6.00),(38,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',24,72,NULL,NULL,2,2.00),(39,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',24,46,NULL,NULL,1,6.00),(901,'王老吉310ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wanglaoji-310.jpg',801,202,NULL,'常温',2,5.50),(902,'全麦面包切片','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wholewheat-bread.jpg',801,207,NULL,NULL,1,9.90),(903,'冰美式咖啡330ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/iced-americano-330.jpg',802,221,NULL,'少冰',1,12.90),(904,'维达抽纸3包装','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/vinda-tissue-3pack.jpg',802,215,NULL,NULL,1,11.90),(905,'缤纷水果杯','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/fruit-cup-mix.jpg',803,211,NULL,NULL,1,15.80),(906,'Type-C快充数据线1米','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/typec-cable-1m.jpg',803,217,NULL,NULL,1,19.90),(907,'自热米饭红烧牛肉味','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/self-heating-rice-beef.jpg',804,208,NULL,NULL,1,16.80),(908,'冰美式咖啡330ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/iced-americano-330.jpg',804,221,NULL,'常温',1,12.90),(909,'自热米饭红烧牛肉味','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/self-heating-rice-beef.jpg',805,208,NULL,NULL,1,16.80),(910,'经典桶装泡面','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/cup-noodle-classic.jpg',805,209,NULL,NULL,1,5.50),(911,'自热米饭红烧牛肉味','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/self-heating-rice-beef.jpg',806,208,NULL,NULL,1,16.80),(912,'鸡胸肉轻食沙拉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/chicken-salad.jpg',806,210,NULL,'黑胡椒汁',1,18.90),(913,'王老吉310ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wanglaoji-310.jpg',807,202,NULL,'常温',1,5.50),(914,'东方树叶青柑普洱500ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/dongfangshuye-qg.jpg',807,203,NULL,'常温',1,6.80),(915,'Type-C快充数据线1米','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/typec-cable-1m.jpg',808,217,NULL,NULL,1,19.90),(916,'奥利奥夹心饼干','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/oreo-classic.jpg',808,214,NULL,NULL,1,8.90),(917,'极鲜秒杀特供矿泉水','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/flash-sale-water.jpg',808,219,NULL,'3瓶装',3,1.00),(918,'三明治火腿芝士','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/sandwich-ham-cheese.jpg',809,212,NULL,'微波加热',1,12.80),(919,'缤纷水果杯','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/fruit-cup-mix.jpg',809,211,NULL,NULL,1,15.80),(920,'极鲜秒杀特供矿泉水','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/flash-sale-water.jpg',809,219,NULL,'3瓶装',3,1.00),(921,'王老吉310ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wanglaoji-310.jpg',810,202,NULL,'常温',1,5.50),(922,'鸡胸肉轻食沙拉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/chicken-salad.jpg',810,210,NULL,'油醋汁',1,18.90),(923,'坚果混合装180g','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/mixed-nuts-180.jpg',811,224,NULL,NULL,1,23.50),(924,'农夫山泉550ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/nongfu-550.jpg',811,201,NULL,NULL,1,2.50),(925,'王老吉310ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wanglaoji-310.jpg',812,202,NULL,'常温',1,5.50),(926,'蒙牛纯牛奶250ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/mengniu-milk-250.jpg',812,205,NULL,NULL,1,3.80),(927,'维达抽纸3包装','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/vinda-tissue-3pack.jpg',812,215,NULL,NULL,1,11.90),(928,'Type-C快充数据线1米','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/typec-cable-1m.jpg',813,217,NULL,NULL,1,19.90),(929,'缤纷水果杯','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/fruit-cup-mix.jpg',813,211,NULL,NULL,1,15.80),(930,'蒙牛纯牛奶250ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/mengniu-milk-250.jpg',813,205,NULL,NULL,1,3.80),(931,'自热米饭红烧牛肉味','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/self-heating-rice-beef.jpg',814,208,NULL,NULL,1,16.80),(932,'卤蛋双拼饭团','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/rice-ball-double-egg.jpg',814,223,NULL,'即食',1,9.90),(933,'极鲜秒杀特供矿泉水','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/flash-sale-water.jpg',814,219,NULL,'4瓶装',4,1.00),(934,'三明治火腿芝士','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/sandwich-ham-cheese.jpg',815,212,NULL,'微波加热',1,12.80),(935,'Type-C快充数据线1米','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/typec-cable-1m.jpg',815,217,NULL,NULL,1,19.90),(936,'冰红茶500ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/binghongcha-500.jpg',815,204,NULL,'标准',1,4.00),(937,'鸡胸肉轻食沙拉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/chicken-salad.jpg',816,210,NULL,'凯撒汁',1,18.90),(938,'Type-C快充数据线1米','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/typec-cable-1m.jpg',816,217,NULL,NULL,1,19.90),(939,'极鲜秒杀特供矿泉水','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/flash-sale-water.jpg',816,219,NULL,'4瓶装',4,1.00),(940,'全麦面包切片','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wholewheat-bread.jpg',817,207,NULL,NULL,1,9.90),(941,'光明如实原味酸奶135g','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/guangming-yogurt-135.jpg',817,206,NULL,'135g',1,6.50),(942,'农夫山泉550ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/nongfu-550.jpg',817,201,NULL,NULL,1,2.50),(943,'王老吉310ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wanglaoji-310.jpg',818,202,NULL,'常温',1,5.50),(944,'蒙牛纯牛奶250ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/mengniu-milk-250.jpg',818,205,NULL,NULL,1,3.80),(945,'极鲜秒杀特供矿泉水','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/flash-sale-water.jpg',818,219,NULL,'6瓶装',6,1.00),(946,'全麦面包切片','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wholewheat-bread.jpg',819,207,NULL,NULL,1,9.90),(947,'蒙牛纯牛奶250ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/mengniu-milk-250.jpg',819,205,NULL,NULL,1,3.80),(948,'极鲜秒杀特供矿泉水','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/flash-sale-water.jpg',819,219,NULL,'7瓶装',7,1.00),(949,'冰美式咖啡330ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/iced-americano-330.jpg',820,221,NULL,'常温',1,12.90),(950,'极鲜秒杀特供矿泉水','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/flash-sale-water.jpg',820,219,NULL,'5瓶装',5,1.00),(951,'Type-C快充数据线1米','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/typec-cable-1m.jpg',821,217,NULL,NULL,1,19.90),(952,'鸡胸肉轻食沙拉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/chicken-salad.jpg',821,210,NULL,'黑胡椒汁',1,18.90),(953,'缤纷水果杯','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/fruit-cup-mix.jpg',822,211,NULL,NULL,1,15.80),(954,'三明治火腿芝士','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/sandwich-ham-cheese.jpg',822,212,NULL,'即食',1,12.80),(955,'经典桶装泡面','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/cup-noodle-classic.jpg',823,209,NULL,NULL,1,5.50),(956,'奥利奥夹心饼干','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/oreo-classic.jpg',823,214,NULL,NULL,1,8.90),(957,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',824,70,NULL,'少糖',2,1.00),(958,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',825,70,NULL,'少糖',1,1.00),(959,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',825,72,NULL,NULL,2,2.00),(960,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',825,46,NULL,NULL,3,6.00);
/*!40000 ALTER TABLE `order_detail` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `number` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '订单号',
  `status` int NOT NULL DEFAULT '1' COMMENT '订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款',
  `user_id` bigint NOT NULL COMMENT '下单用户',
  `address_book_id` bigint NOT NULL COMMENT '地址id',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `checkout_time` datetime DEFAULT NULL COMMENT '结账时间',
  `pay_method` int NOT NULL DEFAULT '1' COMMENT '支付方式 1微信,2支付宝',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态 0未支付 1已支付 2退款',
  `amount` decimal(10,2) NOT NULL COMMENT '实收金额',
  `remark` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '备注',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '手机号',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '地址',
  `user_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '用户名称',
  `consignee` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '收货人',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '订单取消原因',
  `rejection_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '订单拒绝原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '订单取消时间',
  `estimated_delivery_time` datetime DEFAULT NULL COMMENT '预计送达时间',
  `delivery_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '配送状态  1立即送出  0选择具体时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
  `pack_amount` int DEFAULT NULL COMMENT '打包费',
  `tableware_number` int DEFAULT NULL COMMENT '餐具数量',
  `tableware_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '餐具数量状态  1按餐量提供  0选择具体数量',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=826 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'c9ed741889c44b8c8981fb8fdb3a84db',6,1,2,'2026-03-03 23:24:04',NULL,1,0,6.00,'E2E test order','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-03 23:25:04',NULL,1,NULL,0,0,0,1),(2,'1772551957890171685',6,1,2,'2026-03-03 23:32:38',NULL,1,0,6.00,'E2E test order','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-03 23:33:38',NULL,1,NULL,0,0,0,1),(3,'1772552075008248626',5,1,2,'2026-03-03 23:34:35','2026-03-03 23:34:35',1,1,6.00,'E2E test order','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,NULL,1,'2026-03-04 21:23:20',0,0,0,1),(4,'1773238562548805446',6,1,2,'2026-03-11 22:16:03',NULL,1,0,1.00,'SECKILL','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-11 22:17:06',NULL,1,NULL,0,1,1,2),(5,'1773238728520117292',6,1,2,'2026-03-11 22:18:49',NULL,1,0,1.00,'SECKILL','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-11 22:19:52',NULL,1,NULL,0,1,1,2),(6,'1773238883525198104',6,1,2,'2026-03-11 22:21:24',NULL,1,0,1.00,'SECKILL','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-11 22:22:27',NULL,1,NULL,0,1,1,2),(7,'1773295693385873626',6,1,2,'2026-03-12 14:08:13',NULL,1,0,1.00,'SECKILL','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-12 14:09:59',NULL,1,NULL,0,1,1,2),(8,'1773295532338257029',6,1,2,'2026-03-12 14:05:32',NULL,0,0,1.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-12 14:17:52',NULL,0,NULL,0,0,0,2),(9,'1773296260370505019',6,1,2,'2026-03-12 14:17:40',NULL,1,0,1.00,'SECKILL','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-12 14:18:40',NULL,1,NULL,0,1,1,2),(10,'1773317915247924697',3,1,2,'2026-03-12 20:18:35','2026-03-12 20:18:40',1,1,23.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-12 21:15:00',0,NULL,4,0,0,6),(11,'1773319343310298448',5,1,2,'2026-03-12 20:42:23','2026-03-12 20:42:26',1,1,11.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-12 21:42:00',0,'2026-03-28 20:04:27',3,0,0,6),(12,'1773324370692228123',6,1,2,'2026-03-12 22:06:11',NULL,1,0,14.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-12 22:07:11','2026-03-12 23:05:00',0,NULL,4,0,0,6),(13,'1773324912530503352',5,1,2,'2026-03-12 22:15:13','2026-03-12 22:15:15',1,1,17.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-12 23:14:00',0,'2026-03-12 22:23:59',3,0,0,6),(14,'1773326213338314956',3,1,2,'2026-03-12 22:36:53','2026-03-12 22:36:56',1,1,19.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-12 23:36:00',0,NULL,5,0,0,6),(15,'1773327208844833718',3,1,2,'2026-03-12 22:53:29','2026-03-12 22:53:31',1,1,20.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-12 23:53:00',0,NULL,4,0,0,6),(16,'1773327229689847680',3,1,2,'2026-03-12 22:53:50','2026-03-12 22:53:52',1,1,20.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-12 23:53:00',0,NULL,4,0,0,6),(17,'1773327303983387377',3,1,2,'2026-03-12 22:55:04','2026-03-12 22:55:05',1,1,20.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-12 23:55:00',0,NULL,4,0,0,6),(18,'1773327701819662512',3,1,2,'2026-03-12 23:01:42','2026-03-12 23:01:44',1,1,34.00,'11','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-12 00:01:00',0,NULL,6,0,0,6),(19,'1773409042157492281',3,1,2,'2026-03-13 21:37:22','2026-03-13 21:37:24',1,1,14.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-13 22:37:00',0,NULL,2,0,0,6),(20,'1773410283925411617',6,1,2,'2026-03-13 21:58:04',NULL,1,0,17.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-13 21:59:04','2026-03-13 22:57:00',0,NULL,3,0,0,6),(21,'1773410322438933754',3,1,2,'2026-03-13 21:58:42','2026-03-13 21:58:44',1,1,24.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-13 22:58:00',0,NULL,8,0,0,6),(22,'1773502373115637701',6,1,2,'2026-03-14 23:32:53',NULL,1,0,20.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-14 23:33:54','2026-03-14 00:28:00',0,NULL,4,0,0,6),(23,'1773503022739325730',4,1,2,'2026-03-14 23:43:43','2026-03-14 23:43:45',1,1,13.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-14 00:43:00',0,NULL,3,0,0,6),(24,'1773505029509840639',6,1,2,'2026-03-15 00:17:10',NULL,1,0,13.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张','超时未支付',NULL,'2026-03-15 00:18:10','2026-03-15 01:17:00',0,NULL,3,0,0,6),(801,'JX202603280801',6,101,201,'2026-03-28 10:05:00',NULL,1,0,20.90,'周五补货单，待付款保留库存','13810010001','上海市上海市静安区南京西路688号兴业太古汇2座1808室','林知夏','林知夏','修复本地测试数据：超时测试单已关闭',NULL,'2026-03-28 20:13:29','2026-03-28 10:50:00',1,NULL,0,0,1,401),(802,'JX202603280802',6,104,207,'2026-03-28 10:20:00',NULL,1,0,24.80,'会议前补给，用户尚未支付','13810010004','北京市北京市海淀区中关村大街27号中关村大厦B座902室','许安然','许安然','修复本地测试数据：超时测试单已关闭',NULL,'2026-03-28 20:13:29','2026-03-28 11:05:00',1,NULL,0,0,1,405),(803,'JX202603280803',2,102,203,'2026-03-28 09:18:00','2026-03-28 09:21:00',1,1,35.70,'给团队补充下午茶和数据线','13810010002','上海市上海市浦东新区银城中路501号上海中心B1层','周明远','周明远',NULL,NULL,NULL,'2026-03-28 10:10:00',1,NULL,0,1,0,402),(804,'JX202603280804',2,105,209,'2026-03-28 09:32:00','2026-03-28 09:35:00',1,1,29.70,'望京仓履约测试单','13810010005','北京市北京市朝阳区广顺北大街33号凯德MALL望京写字楼905室','宋嘉禾','宋嘉禾',NULL,NULL,NULL,'2026-03-28 10:20:00',1,NULL,0,2,0,404),(805,'JX202603280805',2,103,205,'2026-03-28 09:40:00','2026-03-28 09:42:00',1,1,22.30,'漕河泾办公室午餐速食单','13810010003','上海市上海市徐汇区虹梅路1905号远中科研楼1号楼801室','陈语桐','陈语桐',NULL,NULL,NULL,'2026-03-28 10:30:00',0,NULL,0,0,1,403),(806,'JX202603270806',3,101,202,'2026-03-27 18:10:00','2026-03-27 18:12:00',1,1,35.70,'晚餐轻食组合，仓已接单待配送','13810010001','上海市上海市徐汇区桂平路680号创客邦A座607室','林知夏','林知夏',NULL,NULL,NULL,'2026-03-27 19:00:00',0,NULL,0,1,0,403),(807,'JX202603270807',3,107,213,'2026-03-27 17:35:00','2026-03-27 17:38:00',1,1,13.30,'下班前饮料零食单','13810010007','上海市上海市杨浦区淞沪路98号万达广场A座1203室','唐可心','唐可心',NULL,NULL,NULL,'2026-03-27 18:20:00',1,NULL,0,0,1,402),(808,'JX202603270808',3,108,216,'2026-03-27 18:42:00','2026-03-27 18:45:00',1,1,31.80,'酒仙桥夜间补货单，已接单','13810010008','北京市北京市朝阳区酒仙桥路10号恒通国际创新园C6座501室','叶舒宁','叶舒宁',NULL,NULL,NULL,'2026-03-27 19:30:00',1,NULL,0,0,1,404),(809,'JX202603260809',4,102,204,'2026-03-26 11:15:00','2026-03-26 11:17:00',1,1,31.60,'陆家嘴午高峰配送中','13810010002','上海市上海市浦东新区商城路1900号金桃大厦1502室','周明远','周明远',NULL,NULL,NULL,'2026-03-26 12:00:00',1,NULL,0,1,0,402),(810,'JX202603260810',5,104,208,'2026-03-26 12:05:00','2026-03-26 12:07:00',1,1,24.40,'中关村会议配送中','13810010004','北京市北京市朝阳区阜通东大街6号方恒国际中心B座1205室','许安然','许安然',NULL,NULL,NULL,'2026-03-26 12:50:00',1,'2026-03-28 20:01:10',0,0,1,404),(811,'JX202603260811',5,103,206,'2026-03-26 15:20:00','2026-03-26 15:22:00',1,1,26.00,'坚果补货配送中','13810010003','上海市上海市静安区万航渡路889号悦达889广场12楼','陈语桐','陈语桐',NULL,NULL,NULL,'2026-03-26 16:05:00',1,'2026-03-28 20:01:09',0,0,1,401),(812,'JX202603250812',5,101,201,'2026-03-25 08:05:00','2026-03-25 08:06:00',1,1,21.20,'早餐补给已完成','13810010001','上海市上海市静安区南京西路688号兴业太古汇2座1808室','林知夏','林知夏',NULL,NULL,NULL,'2026-03-25 08:50:00',1,'2026-03-25 08:41:00',0,1,0,401),(813,'JX202603250813',5,102,203,'2026-03-25 09:12:00','2026-03-25 09:14:00',1,1,39.50,'陆家嘴高客单订单已完成','13810010002','上海市上海市浦东新区银城中路501号上海中心B1层','周明远','周明远',NULL,NULL,NULL,'2026-03-25 10:00:00',0,'2026-03-25 09:52:00',0,0,1,402),(814,'JX202603240814',5,103,205,'2026-03-24 12:25:00','2026-03-24 12:28:00',1,1,30.70,'漕河泾午间订单已完成','13810010003','上海市上海市徐汇区虹梅路1905号远中科研楼1号楼801室','陈语桐','陈语桐',NULL,NULL,NULL,'2026-03-24 13:10:00',1,'2026-03-24 13:03:00',0,0,1,403),(815,'JX202603240815',5,104,207,'2026-03-24 18:20:00','2026-03-24 18:23:00',1,1,36.70,'中关村加班补给已完成','13810010004','北京市北京市海淀区中关村大街27号中关村大厦B座902室','许安然','许安然',NULL,NULL,NULL,'2026-03-24 19:05:00',1,'2026-03-24 18:58:00',0,2,0,405),(816,'JX202603230816',5,105,210,'2026-03-23 20:05:00','2026-03-23 20:08:00',1,1,42.80,'望京夜间补货已完成','13810010005','北京市北京市海淀区知春路7号致真大厦1508室','宋嘉禾','宋嘉禾',NULL,NULL,NULL,'2026-03-23 21:00:00',1,'2026-03-23 20:52:00',0,0,1,404),(817,'JX202603230817',5,108,215,'2026-03-23 08:45:00','2026-03-23 08:47:00',1,1,22.90,'东直门早餐组合已完成','13810010008','北京市北京市东城区东直门外大街46号天恒大厦1005室','叶舒宁','叶舒宁',NULL,NULL,NULL,'2026-03-23 09:25:00',0,'2026-03-23 09:19:00',0,1,0,405),(818,'JX202603220818',6,101,202,'2026-03-22 21:05:00',NULL,1,0,15.30,'用户下单后未支付自动取消','13810010001','上海市上海市徐汇区桂平路680号创客邦A座607室','林知夏','林知夏','超时未支付',NULL,'2026-03-22 21:21:00','2026-03-22 21:50:00',1,NULL,0,0,1,401),(819,'JX202603220819',6,107,213,'2026-03-22 18:10:00',NULL,1,0,20.70,'下班前临时下单后放弃支付','13810010007','上海市上海市杨浦区淞沪路98号万达广场A座1203室','唐可心','唐可心','超时未支付',NULL,'2026-03-22 18:27:00','2026-03-22 18:55:00',1,NULL,0,0,1,402),(820,'JX202603210820',6,104,208,'2026-03-21 19:35:00',NULL,1,0,17.90,'晚间临时下单后未付款','13810010004','北京市北京市朝阳区阜通东大街6号方恒国际中心B座1205室','许安然','许安然','超时未支付',NULL,'2026-03-21 19:52:00','2026-03-21 20:20:00',1,NULL,0,0,1,404),(821,'JX202603210821',6,105,209,'2026-03-21 14:00:00','2026-03-21 14:03:00',1,2,38.80,'望京仓缺货退款单','13810010005','北京市北京市朝阳区广顺北大街33号凯德MALL望京写字楼905室','宋嘉禾','宋嘉禾','用户取消后退款',NULL,'2026-03-21 14:35:00','2026-03-21 14:45:00',1,NULL,0,0,1,404),(822,'JX202603200822',6,102,204,'2026-03-20 12:10:00','2026-03-20 12:12:00',1,2,28.60,'仓内水果临时缺货','13810010002','上海市上海市浦东新区商城路1900号金桃大厦1502室','周明远','周明远',NULL,'仓内缺货，改配失败','2026-03-20 12:40:00','2026-03-20 13:00:00',1,NULL,0,0,1,402),(823,'JX202603200823',6,103,206,'2026-03-20 21:20:00','2026-03-20 21:22:00',1,2,15.40,'系统自动退款测试单','13810010003','上海市上海市静安区万航渡路889号悦达889广场12楼','陈语桐','陈语桐','用户取消已退款',NULL,'2026-03-20 21:48:00','2026-03-20 22:10:00',1,NULL,0,0,1,403),(824,'JX202603190824',5,106,211,'2026-03-19 08:30:00','2026-03-19 08:31:00',1,1,2.00,'杭州用户秒杀单已完成','13810010006','浙江省杭州市萧山区民和路800号宝盛世纪中心1幢1801室','贺星野','贺星野',NULL,NULL,NULL,'2026-03-19 09:10:00',1,'2026-03-19 08:58:00',0,0,1,3),(825,'1774701971920147594',3,1,2,'2026-03-28 20:46:12','2026-03-28 20:46:14',1,1,29.00,'','13812345678','极鲜达总部大楼','极鲜达用户1','架构师小张',NULL,NULL,NULL,'2026-03-28 21:46:00',0,NULL,6,0,0,6);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `setmeal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setmeal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint NOT NULL COMMENT '菜品分类id',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '套餐名称',
  `price` decimal(10,2) NOT NULL COMMENT '套餐价格',
  `status` int DEFAULT '1' COMMENT '售卖状态 0:停售 1:起售',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '描述信息',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_setmeal_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=703 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='套餐';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `setmeal` WRITE;
/*!40000 ALTER TABLE `setmeal` DISABLE KEYS */;
INSERT INTO `setmeal` VALUES (701,201,'极速早餐补给包',26.80,1,'面包+牛奶+咖啡组合，适合管理端套餐页联调。','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/setmeal-breakfast-pack.jpg','2026-03-10 10:40:00','2026-03-10 10:40:00',1,1),(702,201,'深夜恢复能量包',32.90,0,'泡面+凉茶+湿巾组合，保留少量套餐能力测试。','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/setmeal-night-pack.jpg','2026-03-10 10:40:00','2026-03-10 10:40:00',1,1);
/*!40000 ALTER TABLE `setmeal` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `setmeal_dish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setmeal_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `setmeal_id` bigint DEFAULT NULL COMMENT '套餐id',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '菜品名称 （冗余字段）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '菜品单价（冗余字段）',
  `copies` int DEFAULT NULL COMMENT '菜品份数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=807 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='套餐菜品关系';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `setmeal_dish` WRITE;
/*!40000 ALTER TABLE `setmeal_dish` DISABLE KEYS */;
INSERT INTO `setmeal_dish` VALUES (801,701,205,'蒙牛纯牛奶250ml',3.80,1),(802,701,207,'全麦面包切片',9.90,1),(803,701,221,'冰美式咖啡330ml',12.90,1),(804,702,209,'经典桶装泡面',5.50,1),(805,702,202,'王老吉310ml',5.50,1),(806,702,216,'便携湿巾10片装',4.90,1);
/*!40000 ALTER TABLE `setmeal_dish` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `shopping_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopping_cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '商品名称',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
  `user_id` bigint NOT NULL COMMENT '主键',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '口味',
  `number` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1308 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='购物车';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `shopping_cart` WRITE;
/*!40000 ALTER TABLE `shopping_cart` DISABLE KEYS */;
INSERT INTO `shopping_cart` VALUES (1201,'东方树叶青柑普洱500ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/dongfangshuye-qg.jpg',101,203,NULL,'常温',2,6.80,'2026-03-28 09:20:00',401),(1202,'光明如实原味酸奶135g','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/guangming-yogurt-135.jpg',101,206,NULL,'135g',1,6.50,'2026-03-28 09:21:00',401),(1203,'缤纷水果杯','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/fruit-cup-mix.jpg',102,211,NULL,NULL,1,15.80,'2026-03-28 09:22:00',402),(1204,'Type-C快充数据线1米','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/typec-cable-1m.jpg',102,217,NULL,NULL,1,19.90,'2026-03-28 09:23:00',402),(1205,'三明治火腿芝士','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/sandwich-ham-cheese.jpg',104,212,NULL,'微波加热',2,12.80,'2026-03-28 09:24:00',405),(1206,'维达抽纸3包装','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/vinda-tissue-3pack.jpg',104,215,NULL,NULL,1,11.90,'2026-03-28 09:25:00',405),(1207,'冰美式咖啡330ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/iced-americano-330.jpg',105,221,NULL,'少冰',1,12.90,'2026-03-28 09:26:00',404),(1208,'自热米饭红烧牛肉味','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/self-heating-rice-beef.jpg',105,208,NULL,NULL,1,16.80,'2026-03-28 09:27:00',404),(1209,'极鲜秒杀特供','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/acebddbb-7aee-4d69-a4e2-273b18584744.png',106,70,NULL,'少糖',1,1.00,'2026-03-28 09:28:00',3),(1210,'王老吉310ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/wanglaoji-310.jpg',107,202,NULL,'常温',3,5.50,'2026-03-28 09:29:00',402),(1211,'农夫山泉550ml','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/nongfu-550.jpg',108,201,NULL,NULL,2,2.50,'2026-03-28 09:30:00',405),(1212,'奥利奥夹心饼干','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/mock/oreo-classic.jpg',108,214,NULL,NULL,1,8.90,'2026-03-28 09:31:00',405),(1303,'王老吉','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/0ea17d2b-6c7b-4e3c-bc1e-a1b93fefed49.png',4,46,NULL,'常温',1,6.00,'2026-03-28 20:13:29',6),(1304,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',4,72,NULL,NULL,2,1.00,'2026-03-28 20:13:29',6),(1307,'馒头','https://sky-itcast-huanzi.oss-cn-beijing.aliyuncs.com/141e5098-596b-4674-95a5-9552a88ecb17.png',1,72,NULL,NULL,3,2.00,'2026-03-28 20:46:20',6);
/*!40000 ALTER TABLE `shopping_cart` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `undo_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `undo_log` (
  `branch_id` bigint NOT NULL,
  `xid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime(6) NOT NULL,
  `log_modified` datetime(6) NOT NULL,
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `undo_log` WRITE;
/*!40000 ALTER TABLE `undo_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `undo_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `openid` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `sex` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `id_number` varchar(18) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `avatar` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'o0-4V5W-8-0','极鲜达用户1','13812345678','1',NULL,'https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132','2022-06-09 22:09:18'),(4,'ol41H11mUSXSf4U2yb0Tg2HpTYrY',NULL,NULL,NULL,NULL,NULL,'2026-03-09 16:11:20'),(101,'jx_mock_openid_101','林知夏','13810010001','2',NULL,'https://thirdwx.qlogo.cn/mmopen/vi_32/mock-user-101/132','2026-03-10 09:00:00'),(102,'jx_mock_openid_102','周明远','13810010002','1',NULL,'https://thirdwx.qlogo.cn/mmopen/vi_32/mock-user-102/132','2026-03-10 09:05:00'),(103,'jx_mock_openid_103','陈语桐','13810010003','2',NULL,'https://thirdwx.qlogo.cn/mmopen/vi_32/mock-user-103/132','2026-03-10 09:10:00'),(104,'jx_mock_openid_104','许安然','13810010004','2',NULL,'https://thirdwx.qlogo.cn/mmopen/vi_32/mock-user-104/132','2026-03-10 09:15:00'),(105,'jx_mock_openid_105','宋嘉禾','13810010005','1',NULL,'https://thirdwx.qlogo.cn/mmopen/vi_32/mock-user-105/132','2026-03-10 09:20:00'),(106,'jx_mock_openid_106','贺星野','13810010006','1',NULL,'https://thirdwx.qlogo.cn/mmopen/vi_32/mock-user-106/132','2026-03-10 09:25:00'),(107,'jx_mock_openid_107','唐可心','13810010007','2',NULL,'https://thirdwx.qlogo.cn/mmopen/vi_32/mock-user-107/132','2026-03-10 09:30:00'),(108,'jx_mock_openid_108','叶舒宁','13810010008','2',NULL,'https://thirdwx.qlogo.cn/mmopen/vi_32/mock-user-108/132','2026-03-10 09:35:00');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouse` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '仓库名',
  `location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '经纬度',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '地址',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0禁用 1启用',
  `contact_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_warehouse_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=407 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='前置仓';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `warehouse` WRITE;
/*!40000 ALTER TABLE `warehouse` DISABLE KEYS */;
INSERT INTO `warehouse` VALUES (1,'北京朝阳前置仓','39.9042,116.4074','北京市朝阳区国贸CBD',1,NULL,NULL),(2,'上海中心前置仓','31.2304,121.4737','上海市浦东新区',1,NULL,NULL),(3,'杭州萧山前置仓','30.2833，120.4932','杭州萧山区',1,'欢子','18121695592'),(6,'哈尔滨南岗前置仓','126.6556，45.7400','哈尔滨市南岗区',1,'',''),(401,'极鲜达静安前置仓','31.2309,121.4467','上海市静安区南京西路688号后场冷链仓',1,'韩若宁','021-56560011'),(402,'极鲜达浦东陆家嘴前置仓','31.2397,121.4998','上海市浦东新区银城中路501号地下配送中心',1,'邵景川','021-56560012'),(403,'极鲜达徐汇漕河泾前置仓','31.1703,121.3970','上海市徐汇区桂平路680号B1即时履约仓',1,'顾雨辰','021-56560013'),(404,'极鲜达朝阳望京前置仓','40.0049,116.4730','北京市朝阳区广顺北大街33号社区即时仓',1,'程知南','010-88440021'),(405,'极鲜达海淀中关村前置仓','39.9836,116.3165','北京市海淀区中关村大街27号地下一层前置仓',1,'沈亦航','010-88440022'),(406,'极鲜达广州天河体育西前置仓','23.1325,113.3246','广州市天河区体育西路101号冷链试运营仓',0,'罗书亦','020-66550031');
/*!40000 ALTER TABLE `warehouse` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `warehouse_sku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouse_sku` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `dish_id` bigint NOT NULL COMMENT '商品ID',
  `stock` int NOT NULL DEFAULT '0' COMMENT '物理库存',
  `lock_stock` int NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `status` tinyint DEFAULT '1' COMMENT '仓库内售卖状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_warehouse_dish` (`warehouse_id`,`dish_id`) USING BTREE,
  KEY `idx_warehouse_sku_dish` (`dish_id`) USING BTREE,
  CONSTRAINT `fk_warehouse_sku_dish` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_warehouse_sku_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=1064 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='仓库库存';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `warehouse_sku` WRITE;
/*!40000 ALTER TABLE `warehouse_sku` DISABLE KEYS */;
INSERT INTO `warehouse_sku` VALUES (1,1,46,99,1,1),(2,2,46,33,0,1),(3,1,70,12,0,1),(4,2,70,10,0,1),(5,3,70,4,0,1),(6,6,70,96,0,1),(7,6,46,96,0,1),(8,6,72,88,0,1),(9,1,72,100,0,1),(1001,401,201,120,2,1),(1002,401,202,80,3,1),(1003,401,203,36,1,1),(1004,401,205,54,2,1),(1005,401,206,40,0,1),(1006,401,207,28,0,1),(1007,401,210,14,1,1),(1008,401,213,66,0,1),(1009,401,215,90,0,1),(1010,401,216,120,0,1),(1011,401,219,300,5,1),(1012,401,221,18,0,1),(1013,401,224,0,0,0),(1014,402,201,100,0,1),(1015,402,202,88,1,1),(1016,402,205,60,0,1),(1017,402,211,25,2,1),(1018,402,212,34,1,1),(1019,402,213,72,0,1),(1020,402,215,85,0,1),(1021,402,216,110,0,1),(1022,402,217,16,1,1),(1023,402,219,260,4,1),(1024,402,222,0,0,0),(1025,402,225,45,0,1),(1026,403,202,65,0,1),(1027,403,204,58,0,1),(1028,403,207,22,0,1),(1029,403,208,18,2,1),(1030,403,209,70,1,1),(1031,403,210,8,0,1),(1032,403,213,33,0,1),(1033,403,215,44,0,1),(1034,403,219,180,6,1),(1035,403,220,5,0,0),(1036,403,223,26,0,1),(1037,403,224,12,0,1),(1038,404,202,95,2,1),(1039,404,203,42,0,1),(1040,404,205,50,0,1),(1041,404,208,16,1,1),(1042,404,210,20,0,1),(1043,404,213,48,0,1),(1044,404,215,70,0,1),(1045,404,216,96,0,1),(1046,404,217,9,1,1),(1047,404,219,210,3,1),(1048,404,221,24,2,1),(1049,404,223,0,0,0),(1050,405,201,90,1,1),(1051,405,202,70,0,1),(1052,405,204,40,0,1),(1053,405,205,45,0,1),(1054,405,206,24,0,1),(1055,405,207,30,0,1),(1056,405,212,18,1,1),(1057,405,214,26,0,1),(1058,405,215,72,0,1),(1059,405,216,88,0,1),(1060,405,217,11,1,1),(1061,405,219,230,2,1),(1062,405,221,15,1,0),(1063,6,214,100,0,1);
/*!40000 ALTER TABLE `warehouse_sku` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

