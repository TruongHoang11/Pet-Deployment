SET NAMES utf8mb4;
START TRANSACTION;

SET @now = NOW(6);
SET @user_role = (SELECT id FROM tbl_roles WHERE name = 'ROLE_USER' LIMIT 1);
SET @seed_password = (SELECT password FROM tbl_users WHERE role_id = @user_role LIMIT 1);

-- Xoa rieng du lieu seed de file co the chay lai ma khong dung vao du lieu that.
DELETE FROM menu_roles WHERE role_id=@user_role AND menu_id IN (SELECT id FROM (SELECT id FROM tbl_menus ORDER BY id LIMIT 8) seed_menus);
DELETE FROM tbl_booking_details WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_bookings WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_product_reviews WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_pet_service_reviews WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_order_details WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_orders WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_payments WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_cart_items WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_carts WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_shipping_addresses WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_pets WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_product_images WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_pet_service_images WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_inventories_transactions WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_inventories WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_products WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_services WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_categories WHERE id BETWEEN 1001 AND 1008;
DELETE FROM tbl_users WHERE id LIKE 'seed-user-%';

INSERT IGNORE INTO tbl_users
(id, created_date, last_modified_date, created_by, last_modified_by, active_flag, delete_flag, avatar_url, date_of_birth, email, gender, name, password, refresh_token, role_id)
VALUES
('seed-user-001',@now,@now,'seed','seed',1,0,NULL,'1998-03-12','minh.nguyen@example.com','MALE','Nguyễn Minh',@seed_password,NULL,@user_role),
('seed-user-002',@now,@now,'seed','seed',1,0,NULL,'1999-07-21','lan.tran@example.com','FEMALE','Trần Lan',@seed_password,NULL,@user_role),
('seed-user-003',@now,@now,'seed','seed',1,0,NULL,'2000-11-05','huy.le@example.com','MALE','Lê Huy',@seed_password,NULL,@user_role),
('seed-user-004',@now,@now,'seed','seed',1,0,NULL,'1997-01-30','mai.pham@example.com','FEMALE','Phạm Mai',@seed_password,NULL,@user_role),
('seed-user-005',@now,@now,'seed','seed',1,0,NULL,'2001-09-14','an.vo@example.com','OTHER','Võ An',@seed_password,NULL,@user_role),
('seed-user-006',@now,@now,'seed','seed',1,0,NULL,'1996-05-18','thao.do@example.com','FEMALE','Đỗ Thảo',@seed_password,NULL,@user_role);

INSERT IGNORE INTO tbl_categories
(id,created_date,last_modified_date,created_by,last_modified_by,active_flag,delete_flag,category_type,name)
VALUES
(1001,@now,@now,'seed','seed',1,0,'PRODUCT','Thức ăn cho chó'),
(1002,@now,@now,'seed','seed',1,0,'PRODUCT','Thức ăn cho mèo'),
(1003,@now,@now,'seed','seed',1,0,'PRODUCT','Đồ chơi thú cưng'),
(1004,@now,@now,'seed','seed',1,0,'PRODUCT','Phụ kiện thú cưng'),
(1005,@now,@now,'seed','seed',1,0,'SERVICE','Tắm và vệ sinh'),
(1006,@now,@now,'seed','seed',1,0,'SERVICE','Cắt tỉa lông'),
(1007,@now,@now,'seed','seed',1,0,'SERVICE','Chăm sóc sức khỏe'),
(1008,@now,@now,'seed','seed',1,0,'SERVICE','Khách sạn thú cưng');

INSERT IGNORE INTO tbl_products
(id,created_date,last_modified_date,created_by,last_modified_by,active_flag,delete_flag,avg_rating,description,name,price,total_reviews,category_id)
VALUES
(1001,@now,@now,'seed','seed',1,0,4.8,'Hạt dinh dưỡng cho chó trưởng thành, túi 1kg','Hạt Royal Canin Adult',185000,1,1001),
(1002,@now,@now,'seed','seed',1,0,4.7,'Pate bò thơm ngon dành cho chó, lon 400g','Pate Pedigree vị bò',52000,1,1001),
(1003,@now,@now,'seed','seed',1,0,4.9,'Hạt cân bằng dinh dưỡng cho mèo trưởng thành','Hạt Whiskas vị cá biển',145000,1,1002),
(1004,@now,@now,'seed','seed',1,0,4.6,'Pate cá ngừ mềm dành cho mèo, lon 85g','Pate mèo vị cá ngừ',35000,1,1002),
(1005,@now,@now,'seed','seed',1,0,4.8,'Bóng cao su mềm, an toàn khi cắn','Bóng cao su phát tiếng',65000,1,1003),
(1006,@now,@now,'seed','seed',1,0,4.5,'Đồ chơi cần câu giúp mèo vận động','Cần câu lông vũ cho mèo',45000,1,1003),
(1007,@now,@now,'seed','seed',1,0,4.7,'Dây dắt chắc chắn, điều chỉnh được chiều dài','Dây dắt phản quang',120000,1,1004),
(1008,@now,@now,'seed','seed',1,0,4.9,'Nệm êm có thể tháo vỏ để vệ sinh','Nệm ngủ thú cưng',280000,1,1004);

INSERT IGNORE INTO tbl_services
(id,created_date,last_modified_date,created_by,last_modified_by,active_flag,delete_flag,base_price,description,duration_min,name,category_id)
VALUES
(1001,@now,@now,'seed','seed',1,0,120000,'Tắm sạch và sấy khô cơ bản',60,'Tắm spa cơ bản',1005),
(1002,@now,@now,'seed','seed',1,0,180000,'Tắm, vệ sinh tai và cắt móng',75,'Tắm spa toàn diện',1005),
(1003,@now,@now,'seed','seed',1,0,220000,'Cắt tỉa tạo kiểu theo giống',90,'Cắt tỉa tạo kiểu',1006),
(1004,@now,@now,'seed','seed',1,0,150000,'Cạo lông vệ sinh vùng bụng và chân',60,'Tỉa lông vệ sinh',1006),
(1005,@now,@now,'seed','seed',1,0,100000,'Kiểm tra da, tai và thể trạng tổng quát',30,'Kiểm tra sức khỏe',1007),
(1006,@now,@now,'seed','seed',1,0,130000,'Vệ sinh răng miệng và khử mùi',40,'Chăm sóc răng miệng',1007),
(1007,@now,@now,'seed','seed',1,0,250000,'Lưu trú ban ngày kèm hai bữa ăn',720,'Khách sạn ban ngày',1008),
(1008,@now,@now,'seed','seed',1,0,400000,'Lưu trú qua đêm và theo dõi 24/7',1440,'Khách sạn qua đêm',1008);

INSERT IGNORE INTO tbl_inventories
(id,created_date,last_modified_date,created_by,last_modified_by,quantity,product_id)
VALUES
(1001,@now,@now,'seed','seed',50,1001),(1002,@now,@now,'seed','seed',45,1002),
(1003,@now,@now,'seed','seed',60,1003),(1004,@now,@now,'seed','seed',55,1004),
(1005,@now,@now,'seed','seed',30,1005),(1006,@now,@now,'seed','seed',35,1006),
(1007,@now,@now,'seed','seed',25,1007),(1008,@now,@now,'seed','seed',20,1008);

INSERT IGNORE INTO tbl_inventories_transactions
(id,created_date,last_modified_date,created_by,last_modified_by,note,quantity,type,inventory_id)
VALUES
(1001,@now,@now,'seed','seed','Nhập kho ban đầu',50,'IMPORT',1001),
(1002,@now,@now,'seed','seed','Nhập kho ban đầu',45,'IMPORT',1002),
(1003,@now,@now,'seed','seed','Nhập kho ban đầu',60,'IMPORT',1003),
(1004,@now,@now,'seed','seed','Nhập kho ban đầu',55,'IMPORT',1004),
(1005,@now,@now,'seed','seed','Nhập kho ban đầu',30,'IMPORT',1005),
(1006,@now,@now,'seed','seed','Nhập kho ban đầu',35,'IMPORT',1006),
(1007,@now,@now,'seed','seed','Nhập kho ban đầu',25,'IMPORT',1007),
(1008,@now,@now,'seed','seed','Nhập kho ban đầu',20,'IMPORT',1008);

INSERT IGNORE INTO tbl_product_images
(id,created_date,last_modified_date,image_url,is_main,product_id)
VALUES
(1001,@now,@now,'seed-dog-kibble.jpg',1,1001),
(1002,@now,@now,'seed-dog-pate.jpg',1,1002),
(1003,@now,@now,'seed-cat-kibble.jpg',1,1003),
(1004,@now,@now,'seed-cat-pate.webp',1,1004),
(1005,@now,@now,'seed-rubber-ball.jpg',1,1005),
(1006,@now,@now,'seed-feather-wand.jpg',1,1006),
(1007,@now,@now,'seed-reflective-leash.png',1,1007),
(1008,@now,@now,'seed-pet-bed.jpg',1,1008);

INSERT IGNORE INTO tbl_pet_service_images
(id,created_date,last_modified_date,image_url,is_main,service_id)
VALUES
(1001,@now,@now,'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?q=80&w=900',1,1001),
(1002,@now,@now,'https://images.unsplash.com/photo-1587300003388-59208cc962cb?q=80&w=900',1,1002),
(1003,@now,@now,'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?q=80&w=900',1,1003),
(1004,@now,@now,'https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=900',1,1004),
(1005,@now,@now,'https://images.unsplash.com/photo-1574158622682-e40e69881006?q=80&w=900',1,1005),
(1006,@now,@now,'https://images.unsplash.com/photo-1517849845537-4d257902454a?q=80&w=900',1,1006),
(1007,@now,@now,'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?q=80&w=900',1,1007),
(1008,@now,@now,'https://images.unsplash.com/photo-1548767797-d8c844163c4c?q=80&w=900',1,1008);

INSERT IGNORE INTO tbl_pets
(id,created_date,last_modified_date,created_by,last_modified_by,active_flag,delete_flag,birthday,gender,health_status,name,specie,weight,user_id)
VALUES
(1001,@now,@now,'seed','seed',1,0,'2022-01-10','MALE','Khỏe mạnh','Milo','Chó Poodle',5.2,'seed-user-001'),
(1002,@now,@now,'seed','seed',1,0,'2021-06-15','FEMALE','Khỏe mạnh','Luna','Mèo Anh lông ngắn',4.1,'seed-user-002'),
(1003,@now,@now,'seed','seed',1,0,'2023-02-20','MALE','Dị ứng nhẹ','Bông','Chó Corgi',10.5,'seed-user-003'),
(1004,@now,@now,'seed','seed',1,0,'2020-09-05','FEMALE','Khỏe mạnh','Mimi','Mèo Ba Tư',3.8,'seed-user-004'),
(1005,@now,@now,'seed','seed',1,0,'2022-12-01','OTHER','Theo dõi cân nặng','Đậu','Mèo mướp',4.6,'seed-user-005'),
(1006,@now,@now,'seed','seed',1,0,'2021-04-17','FEMALE','Khỏe mạnh','Nâu','Chó Golden',24.0,'seed-user-006'),
(1007,@now,@now,'seed','seed',1,0,'2023-08-11','MALE','Khỏe mạnh','Kem','Chó Pomeranian',3.2,'seed-user-001'),
(1008,@now,@now,'seed','seed',1,0,'2022-03-22','FEMALE','Khỏe mạnh','Mun','Mèo Bombay',4.0,'seed-user-002');

INSERT IGNORE INTO tbl_shipping_addresses
(id,created_date,last_modified_date,created_by,last_modified_by,address_detail,district,full_name,is_default,phone,province,ward,user_id)
VALUES
(1001,@now,@now,'seed','seed','12 Nguyễn Trãi','Thanh Xuân','Nguyễn Minh',1,'0901000001','Hà Nội','Thượng Đình','seed-user-001'),
(1002,@now,@now,'seed','seed','25 Cầu Giấy','Cầu Giấy','Trần Lan',1,'0901000002','Hà Nội','Dịch Vọng','seed-user-002'),
(1003,@now,@now,'seed','seed','31 Lê Lợi','Hà Đông','Lê Huy',1,'0901000003','Hà Nội','Nguyễn Trãi','seed-user-003'),
(1004,@now,@now,'seed','seed','44 Trần Phú','Ba Đình','Phạm Mai',1,'0901000004','Hà Nội','Điện Biên','seed-user-004'),
(1005,@now,@now,'seed','seed','58 Hai Bà Trưng','Hoàn Kiếm','Võ An',1,'0901000005','Hà Nội','Trần Hưng Đạo','seed-user-005'),
(1006,@now,@now,'seed','seed','62 Hồ Tùng Mậu','Nam Từ Liêm','Đỗ Thảo',1,'0901000006','Hà Nội','Mai Dịch','seed-user-006'),
(1007,@now,@now,'seed','seed','70 Hoàng Quốc Việt','Cầu Giấy','Nguyễn Minh',0,'0901000007','Hà Nội','Nghĩa Tân','seed-user-001'),
(1008,@now,@now,'seed','seed','81 Tây Sơn','Đống Đa','Trần Lan',0,'0901000008','Hà Nội','Quang Trung','seed-user-002');

INSERT IGNORE INTO tbl_carts (id,created_date,last_modified_date,user_id)
SELECT 1001,@now,@now,'seed-user-001' UNION ALL SELECT 1002,@now,@now,'seed-user-002'
UNION ALL SELECT 1003,@now,@now,'seed-user-003' UNION ALL SELECT 1004,@now,@now,'seed-user-004'
UNION ALL SELECT 1005,@now,@now,'seed-user-005' UNION ALL SELECT 1006,@now,@now,'seed-user-006'
UNION ALL SELECT 1007,@now,@now,'7ec15bb7-73d9-49c6-98f6-e9084b43058a'
UNION ALL SELECT 1008,@now,@now,'fce64f5e-16d8-477e-b495-9a66729fb8dd';

INSERT IGNORE INTO tbl_cart_items (id,created_date,last_modified_date,quantity,cart_id,product_id)
VALUES
(1001,@now,@now,1,1001,1001),(1002,@now,@now,2,1002,1002),
(1003,@now,@now,1,1003,1003),(1004,@now,@now,3,1004,1004),
(1005,@now,@now,1,1005,1005),(1006,@now,@now,2,1006,1006),
(1007,@now,@now,1,1007,1007),(1008,@now,@now,1,1008,1008);

INSERT IGNORE INTO tbl_payments
(id,created_date,last_modified_date,created_by,last_modified_by,amount,payment_method,status,transaction_id)
VALUES
(1001,@now,@now,'seed','seed',185000,'COD','PENDING','SEED-TXN-1001'),
(1002,@now,@now,'seed','seed',104000,'VNPAY','SUCCESS','SEED-TXN-1002'),
(1003,@now,@now,'seed','seed',145000,'COD','PROCESSING','SEED-TXN-1003'),
(1004,@now,@now,'seed','seed',105000,'VNPAY','SUCCESS','SEED-TXN-1004'),
(1005,@now,@now,'seed','seed',65000,'COD','PENDING','SEED-TXN-1005'),
(1006,@now,@now,'seed','seed',90000,'VNPAY','SUCCESS','SEED-TXN-1006'),
(1007,@now,@now,'seed','seed',120000,'COD','SUCCESS','SEED-TXN-1007'),
(1008,@now,@now,'seed','seed',280000,'VNPAY','REFUNDED','SEED-TXN-1008');

INSERT IGNORE INTO tbl_orders
(id,created_date,last_modified_date,created_by,last_modified_by,active_flag,delete_flag,order_type,shipping_address_full,shipping_name,shipping_phone,status,total_amount,payment_id,user_id)
VALUES
(1001,@now,@now,'seed','seed',1,0,'PRODUCT','12 Nguyễn Trãi, Hà Nội','Nguyễn Minh','0901000001','PENDING',185000,1001,'seed-user-001'),
(1002,@now,@now,'seed','seed',1,0,'PRODUCT','25 Cầu Giấy, Hà Nội','Trần Lan','0901000002','PROCESSING',104000,1002,'seed-user-002'),
(1003,@now,@now,'seed','seed',1,0,'PRODUCT','31 Lê Lợi, Hà Nội','Lê Huy','0901000003','SHIPPED',145000,1003,'seed-user-003'),
(1004,@now,@now,'seed','seed',1,0,'PRODUCT','44 Trần Phú, Hà Nội','Phạm Mai','0901000004','DELIVERED',105000,1004,'seed-user-004'),
(1005,@now,@now,'seed','seed',1,0,'PRODUCT','58 Hai Bà Trưng, Hà Nội','Võ An','0901000005','PENDING',65000,1005,'seed-user-005'),
(1006,@now,@now,'seed','seed',1,0,'PRODUCT','62 Hồ Tùng Mậu, Hà Nội','Đỗ Thảo','0901000006','DELIVERED',90000,1006,'seed-user-006'),
(1007,@now,@now,'seed','seed',1,0,'PRODUCT','70 Hoàng Quốc Việt, Hà Nội','Nguyễn Minh','0901000007','DELIVERED',120000,1007,'seed-user-001'),
(1008,@now,@now,'seed','seed',1,0,'PRODUCT','81 Tây Sơn, Hà Nội','Trần Lan','0901000008','CANCELLED',280000,1008,'seed-user-002');

INSERT IGNORE INTO tbl_order_details (id,created_date,last_modified_date,quantity,unit_price,order_id,product_id)
VALUES
(1001,@now,@now,1,185000,1001,1001),(1002,@now,@now,2,52000,1002,1002),
(1003,@now,@now,1,145000,1003,1003),(1004,@now,@now,3,35000,1004,1004),
(1005,@now,@now,1,65000,1005,1005),(1006,@now,@now,2,45000,1006,1006),
(1007,@now,@now,1,120000,1007,1007),(1008,@now,@now,1,280000,1008,1008);

INSERT IGNORE INTO tbl_bookings
(id,created_date,last_modified_date,created_by,last_modified_by,active_flag,delete_flag,actual_price,booking_date,end_time,start_time,status,order_id,pet_id,user_id)
VALUES
(1001,@now,@now,'seed','seed',1,0,120000,CURDATE()+INTERVAL 1 DAY,'10:00:00','09:00:00','CONFIRMED',NULL,1001,'seed-user-001'),
(1002,@now,@now,'seed','seed',1,0,180000,CURDATE()+INTERVAL 2 DAY,'11:15:00','10:00:00','PENDING',NULL,1002,'seed-user-002'),
(1003,@now,@now,'seed','seed',1,0,220000,CURDATE()+INTERVAL 3 DAY,'15:30:00','14:00:00','CONFIRMED',NULL,1003,'seed-user-003'),
(1004,@now,@now,'seed','seed',1,0,150000,CURDATE()-INTERVAL 1 DAY,'10:00:00','09:00:00','COMPLETED',NULL,1004,'seed-user-004'),
(1005,@now,@now,'seed','seed',1,0,100000,CURDATE()+INTERVAL 4 DAY,'09:30:00','09:00:00','PENDING',NULL,1005,'seed-user-005'),
(1006,@now,@now,'seed','seed',1,0,130000,CURDATE()-INTERVAL 2 DAY,'14:40:00','14:00:00','COMPLETED',NULL,1006,'seed-user-006'),
(1007,@now,@now,'seed','seed',1,0,250000,CURDATE()+INTERVAL 5 DAY,'20:00:00','08:00:00','CONFIRMED',NULL,1007,'seed-user-001'),
(1008,@now,@now,'seed','seed',1,0,400000,CURDATE()+INTERVAL 6 DAY,'18:00:00','18:00:00','CANCELLED',NULL,1008,'seed-user-002');

INSERT IGNORE INTO tbl_booking_details (id,price,booking_id,service_id)
VALUES
(1001,120000,1001,1001),(1002,180000,1002,1002),(1003,220000,1003,1003),(1004,150000,1004,1004),
(1005,100000,1005,1005),(1006,130000,1006,1006),(1007,250000,1007,1007),(1008,400000,1008,1008);

INSERT IGNORE INTO tbl_product_reviews
(id,created_date,last_modified_date,created_by,last_modified_by,active_flag,delete_flag,comment,rating,product_id,user_id)
VALUES
(1001,@now,@now,'seed','seed',1,0,'Sản phẩm tốt, thú cưng rất thích',5,1001,'seed-user-001'),
(1002,@now,@now,'seed','seed',1,0,'Đóng gói cẩn thận',5,1002,'seed-user-002'),
(1003,@now,@now,'seed','seed',1,0,'Chất lượng ổn định',5,1003,'seed-user-003'),
(1004,@now,@now,'seed','seed',1,0,'Mèo ăn ngon miệng',4,1004,'seed-user-004'),
(1005,@now,@now,'seed','seed',1,0,'Đồ chơi bền và an toàn',5,1005,'seed-user-005'),
(1006,@now,@now,'seed','seed',1,0,'Mèo rất thích chơi',4,1006,'seed-user-006'),
(1007,@now,@now,'seed','seed',1,0,'Dây dắt chắc chắn',5,1007,'seed-user-001'),
(1008,@now,@now,'seed','seed',1,0,'Nệm mềm và dễ vệ sinh',5,1008,'seed-user-002');

INSERT IGNORE INTO tbl_pet_service_reviews (id,comment,rating,service_id,user_id)
VALUES
(1001,'Nhân viên thân thiện, làm rất sạch',5,1001,'seed-user-001'),
(1002,'Dịch vụ chu đáo',5,1002,'seed-user-002'),
(1003,'Kiểu lông đẹp và phù hợp',5,1003,'seed-user-003'),
(1004,'Thực hiện nhanh chóng',4,1004,'seed-user-004'),
(1005,'Tư vấn sức khỏe kỹ',5,1005,'seed-user-005'),
(1006,'Hơi thở cải thiện rõ',4,1006,'seed-user-006'),
(1007,'Không gian sạch sẽ',5,1007,'seed-user-001'),
(1008,'Theo dõi thú cưng rất tốt',5,1008,'seed-user-002');

INSERT INTO menu_roles (menu_id,role_id)
SELECT m.id,@user_role FROM tbl_menus m
WHERE NOT EXISTS (SELECT 1 FROM menu_roles mr WHERE mr.menu_id=m.id AND mr.role_id=@user_role)
ORDER BY m.id LIMIT 8;

COMMIT;
