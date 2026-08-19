
# PET_18 — Ứng dụng quản lý cửa hàng thú cưng

Phiên bản ngắn gọn bằng tiếng Việt hướng dẫn nhanh cách chạy, cấu trúc và các endpoint quan trọng.

---

## Tổng quan

PET_18 là một ứng dụng Backend xây dựng trên Spring Boot dùng để quản lý sản phẩm, dịch vụ, giỏ hàng, đơn hàng và người dùng cho một cửa hàng thú cưng.

Key points:
- Java 17
- Spring Boot 3.5.x
- MySQL (Hibernate)
- JWT authentication + OAuth2 (Google)

---

## Tính năng chính

- Quản lý người dùng (đăng ký, đăng nhập, phân quyền Admin/User/Staff)
- Quản lý sản phẩm, hình ảnh sản phẩm (Cloudinary)
- Giỏ hàng, tạo đơn từ giỏ hoặc mua ngay
- Quản lý đơn hàng, cập nhật trạng thái, hoàn kho khi hủy
- Quản lý dịch vụ, booking, đánh giá
- Filter API mạnh mẽ (wildcard, AND/OR, sort, pagination)
- Validator tùy chỉnh, Global exception handler

---

## Yêu cầu môi trường

- Java 17
- Maven (hoặc dùng wrapper `mvnw` có sẵn)
- MySQL 8+
- (Tuỳ chọn) Redis, Cloudinary, Gmail nếu muốn tính năng tương ứng

---

## Cấu hình nhanh

1. Sao chép project về máy.
2. Tạo database MySQL: `pet_18_db` và user nếu cần.
3. Chỉnh `src/main/resources/application.yaml`:
   - `spring.datasource.url` → URL tới database
   - `spring.datasource.username`/`password`
   - JWT secret, Cloudinary, mail nếu cần

Ví dụ nhanh:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pet_18_db
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update

app:
  security:
    jwt:
      secret: your_jwt_secret
```

---

## Chạy project

Từ thư mục project:

```powershell
cd C:\Users\Admin\Desktop\Pet_18
.\mvnw clean install -DskipTests
.\mvnw spring-boot:run
```

Hoặc chạy file jar trong `target` nếu đã build.

Server mặc định chạy ở: `http://localhost:8080`

---

## Một số endpoint quan trọng

- Authentication
  - POST /api/v1/auth/register
  - POST /api/v1/auth/login
  - POST /api/v1/auth/refresh

- Users
  - GET /api/v1/users (Admin)
  - GET /api/v1/users/me

- Products
  - GET /api/v1/products
  - GET /api/v1/products/{id}
  - POST /api/v1/products (Admin)

- Cart & Orders
  - GET /api/v1/cart
  - POST /api/v1/orders/from-cart
  - PATCH /api/v1/orders/{id}/status (Admin)

---

## Filter API (tóm tắt)

- Cú pháp: `?filter=field:VALUE` hoặc sử dụng toán tử `~` cho LIKE (ví dụ `name~*B*`).
- Dấu phẩy `,` giữa các điều kiện có nghĩa là AND (mặc định).
- Dấu nháy đơn `'` đứng đầu nhóm điều kiện được dùng để tạo OR trong một nhóm filter.

Ví dụ:

```
GET /api/v1/users?filter=role.name:role_admin,name~*B*&page=0&size=5
```

---

## Lỗi thường gặp & cách kiểm tra nhanh

- Lỗi `No enum constant`: Kiểm tra giá trị enum gửi lên phải viết hoa (VD: `PENDING`).
- Lỗi `Bạn chưa chọn sản phẩm nào`: Kiểm tra `cartItemIds` gửi lên là id của CartItem (không phải productId).
- Lỗi query method ở repository: Kiểm tra tên property trong entity đúng hay không (ví dụ `isMain` phải tồn tại trong `ProductImage`).

---

## Thông tin khác

- Xem `FILTER_SPECIFICATION_GUIDE.md` để biết cách sử dụng filter chi tiết.
- Xem `SETUP_GUIDE.md` và `QUICK_START.md` đã tạo để hướng dẫn cấu hình và chạy nhanh.

---

Nếu bạn muốn, tôi có thể cập nhật README này với hướng dẫn deploy Docker, ví dụ cấu hình môi trường production, hoặc bản tóm tắt API chi tiết (Swagger). Cho tôi biết bạn muốn bổ sung gì.

=======
# Pet-N18
