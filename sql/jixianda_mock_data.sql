SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- Safe demo records for local debugging
-- Notes:
-- 1. This file only contains fictional demonstration data.
-- 2. No real person, real address, real phone, or private bucket is kept.
-- 3. Demo employee password hash below corresponds to the demonstration password DemoPass_2026!.
-- =========================================================

USE `jixianda`;

INSERT IGNORE INTO `user` (`id`, `openid`, `name`, `phone`, `sex`, `id_number`, `avatar`, `create_time`) VALUES
(101, 'demo_openid_101', 'demo_user_01', 'demo0000041', '1', NULL, 'https://example.com/avatar/demo-user-01.png', '2026-03-10 09:00:00'),
(102, 'demo_openid_102', 'demo_user_02', 'demo0000042', '2', NULL, 'https://example.com/avatar/demo-user-02.png', '2026-03-10 09:05:00');

INSERT IGNORE INTO `address_book`
(`id`, `user_id`, `consignee`, `sex`, `phone`, `province_code`, `province_name`, `city_code`, `city_name`, `district_code`, `district_name`, `detail`, `label`, `is_default`) VALUES
(201, 101, 'demo_user_01', '1', 'demo0000041', '310000', 'DemoProvinceA', '310100', 'DemoCityA', '310101', 'DemoDistrictA', 'Demo Address 01', 'home', 1),
(202, 102, 'demo_user_02', '2', 'demo0000042', '110000', 'DemoProvinceB', '110100', 'DemoCityB', '110101', 'DemoDistrictB', 'Demo Address 02', 'office', 1);

INSERT IGNORE INTO `employee`
(`id`, `name`, `username`, `password`, `phone`, `sex`, `id_number`, `status`, `create_time`, `update_time`, `create_user`, `update_user`, `warehouse_id`) VALUES
(601, 'demo_employee_01', 'demo_emp_01', '74e310e4a9496b6c3f505c49c1893c03', 'demo0000031', '1', 'demo-id-000000031', 1, '2026-03-10 10:30:00', '2026-03-10 10:30:00', 1, 1, 1),
(602, 'demo_employee_02', 'demo_emp_02', '74e310e4a9496b6c3f505c49c1893c03', 'demo0000032', '2', 'demo-id-000000032', 1, '2026-03-10 10:30:00', '2026-03-10 10:30:00', 1, 1, 2);

INSERT IGNORE INTO `shopping_cart`
(`id`, `name`, `image`, `user_id`, `dish_id`, `setmeal_id`, `dish_flavor`, `number`, `amount`, `create_time`, `warehouse_id`) VALUES
(1201, 'demo_product_water', 'https://example.com/assets/demo-water.png', 101, 46, NULL, 'normal', 2, 6.00, '2026-03-28 09:20:00', 1),
(1202, 'demo_flash_sale_item', 'https://example.com/assets/demo-flash-sale.png', 102, 70, NULL, 'normal', 1, 1.00, '2026-03-28 09:25:00', 2);

INSERT IGNORE INTO `orders`
(`id`, `number`, `status`, `user_id`, `address_book_id`, `order_time`, `checkout_time`, `pay_method`, `pay_status`, `amount`, `remark`, `phone`, `address`, `user_name`, `consignee`, `cancel_reason`, `rejection_reason`, `cancel_time`, `estimated_delivery_time`, `delivery_status`, `delivery_time`, `pack_amount`, `tableware_number`, `tableware_status`, `warehouse_id`) VALUES
(801, 'JX202603280801', 1, 101, 201, '2026-03-28 10:05:00', NULL, 1, 0, 12.00, 'demo unpaid order', 'demo0000041', 'Demo Address 01', 'demo_user_01', 'demo_user_01', NULL, NULL, NULL, '2026-03-28 10:50:00', 1, NULL, 0, 0, 1, 1),
(802, 'JX202603280802', 5, 102, 202, '2026-03-28 10:20:00', '2026-03-28 10:22:00', 1, 1, 1.00, 'demo completed flash-sale order', 'demo0000042', 'Demo Address 02', 'demo_user_02', 'demo_user_02', NULL, NULL, NULL, '2026-03-28 11:05:00', 1, '2026-03-28 10:45:00', 0, 0, 1, 2);

INSERT IGNORE INTO `order_detail`
(`id`, `name`, `image`, `order_id`, `dish_id`, `setmeal_id`, `dish_flavor`, `number`, `amount`) VALUES
(901, 'demo_product_water', 'https://example.com/assets/demo-water.png', 801, 46, NULL, 'normal', 2, 6.00),
(902, 'demo_flash_sale_item', 'https://example.com/assets/demo-flash-sale.png', 802, 70, NULL, 'normal', 1, 1.00);

SET FOREIGN_KEY_CHECKS = 1;
