# Hướng Dẫn Sử Dụng Filter Specification

## 📋 Mục Lục
1. [Giới Thiệu](#giới-thiệu)
2. [Cú Pháp Cơ Bản](#cú-pháp-cơ-bản)
3. [Các Toán Tử](#các-toán-tử)
4. [Ký Tự Đặc Biệt](#ký-tự-đặc-biệt)
5. [Ví Dụ Thực Tế](#ví-dụ-thực-tế)
6. [Tìm Kiếm Nâng Cao](#tìm-kiếm-nâng-cao)
7. [Các Lỗi Thường Gặp](#các-lỗi-thường-gặp)

---

## Giới Thiệu

Hệ thống lọc (Specification) cho phép bạn lọc dữ liệu từ API một cách linh hoạt mà không cần phải viết mã mới. Bạn có thể kết hợp nhiều điều kiện lọc, tìm kiếm văn bản, so sánh số, v.v.

**Ví dụ URL đơn giản:**
```
http://localhost:8080/api/v1/users?page=0&size=5&sort=id,desc&filter=name~*Admin*
```

---

## Cú Pháp Cơ Bản

### Định Dạng Chung:
```
[orFlag] key operator value [wildcard]
```

**Thành phần:**
- `[orFlag]` (tùy chọn): `'` (dấu nháy đơn) = nếu có = OR, nếu không có = AND (mặc định)
- `key`: Tên trường trong cơ sở dữ liệu
- `operator`: Ký tự đại diện cho phép toán
- `value`: Giá trị cần tìm
- `[wildcard]` (tùy chọn): `*` dùng để tìm kiếm vị trí

### Ví Dụ:
```
name~*Admin*          // Tìm kiếm tên (AND - mặc định)
role.name:admin       // Tìm kiếm trong bảng liên kết (AND - mặc định)
'status:inactive      // OR - status là inactive
salary>5000           // So sánh lớn hơn (AND - mặc định)
```

### 📌 Quan Trọng - Cách Kệp Hợp:
```
condition1,condition2          = condition1 VÀ condition2 (AND)
condition1,condition2,'cond3   = condition1 VÀ condition2 VÀ (HOẶC condition3)
```

---

## ⚡ CHEAT SHEET - Tóm tắt nhanh

| Ký Hiệu | Ý Nghĩa | Ví Dụ |
|---------|---------|-------|
| `,` | AND (mặc định) | `name~*A*,age>20` = Tên chứa A **VÀ** age > 20 |
| `'` | OR (ở đầu) | `'status:active` = **HOẶC** status là active |
| `:` | So sánh bằng | `role:admin` = role chính xác là admin |
| `!` | So sánh khác | `role!user` = role không phải user |
| `>` | Lớn hơn hoặc bằng | `age>20` = age >= 20 |
| `<` | Nhỏ hơn hoặc bằng | `age<60` = age <= 60 |
| `~` | Giống (LIKE) | `name~admin` = tên chứa admin |
| `*` | Wildcard (với `~`) | `name~*A*` = tên chứa A ở giữa |
| `.` | Join bảng | `role.name:admin` = role table, name = admin |

---

---

## Các Toán Tử

| Toán Tử | Ký Hiệu | Mô Tả | Ví Dụ |
|---------|---------|-------|-------|
| **Bằng** | `:` | Tìm giá trị chính xác | `role.name:admin` |
| **Không bằng** | `!` | Tìm giá trị khác | `status!inactive` |
| **Lớn hơn** | `>` | So sánh lớn hơn hoặc bằng | `salary>50000` |
| **Nhỏ hơn** | `<` | So sánh nhỏ hơn hoặc bằng | `age<30` |
| **Giống** | `~` | Tìm kiếm như LIKE (hỗ trợ wildcard) | `name~*Admin*` |

---

## Ký Tự Đặc Biệt

### Wildcard `*` (Dấu Hoa Thị)

**Dùng để xác định vị trí tìm kiếm trong chuỗi:**

| Ký Tự | Vị Trí | Ví Dụ | Kết Quả |
|-------|--------|-------|---------|
| `*value*` | Ở giữa | `name~*Ad*` | Tìm chứa "Ad" | 
| `value*` | Ở cuối | `name~Admin*` | Tìm bắt đầu với "Admin" |
| `*value` | Ở đầu | `name~*min` | Tìm kết thúc bằng "min" |
| (không có) | Chính xác | `name~Admin` | Tìm chứa "Admin" (mặc định CONTAINS) |

### Dấu Phẩy `,` (AND Operator - Mặc Định)

**Dấu phẩy là toán tử AND - tất cả điều kiện phải đúng:**

```
name~*Admin*,role.name:admin,age>20
```
Kết quả: Tên chứa "Admin" **VÀ** role là "admin" **VÀ** age > 20

### Dấu Nháy Đơn `'` (OR Operator)

**Để tạo điều kiện OR, thêm dấu nháy đơn ở đầu:**

```
'status:active
'role.name:admin
```

**Ý nghĩa:** Các điều kiện bắt đầu bằng `'` sẽ dùng toán tử OR thay vì AND

---

## Ví Dụ Thực Tế

### 1️⃣ Tìm Kiếm Tên Người Dùng

**Tìm tất cả người dùng có tên chứa "Admin":**
```
http://localhost:8080/api/v1/users?filter=name~*Admin*
```

**Tìm tên bắt đầu bằng "Admin":**
```
http://localhost:8080/api/v1/users?filter=name~Admin*
```

**Tìm tên kết thúc bằng "min":**
```
http://localhost:8080/api/v1/users?filter=name~*min
```

---

### 2️⃣ Tìm Kiếm Theo Role (Bảng Liên Kết)

**Tìm tất cả người dùng có role là "admin":**
```
http://localhost:8080/api/v1/users?filter=role.name:admin
```

**Kết hợp: Tìm người dùng có role "admin" VÀ tên chứa "Manager":**
```
http://localhost:8080/api/v1/users?filter=role.name:admin,name~*Manager*
```

---

### 3️⃣ So Sánh Số

**Tìm người dùng có id >= 5:**
```
http://localhost:8080/api/v1/users?filter=id>5
```

**Tìm người dùng có id <= 100:**
```
http://localhost:8080/api/v1/users?filter=id<100
```

**Kết hợp: id >= 5 VÀ id <= 100:**
```
http://localhost:8080/api/v1/users?filter=id>5,id<100
```

---

### 4️⃣ Tìm Kiếm Loại Trừ (NOT)

**Tìm tất cả người dùng KHÔNG có role "user":**
```
http://localhost:8080/api/v1/users?filter=role.name!user
```

---

### 5️⃣ Điều Kiện AND (Dấu Phẩy - Mặc Định)

**Tìm người dùng có role "admin" VÀ tên chứa "Admin":**
```
http://localhost:8080/api/v1/users?filter=role.name:admin,name~*Admin*
```

**Giải thích:**
- `role.name:admin` - Điều kiện thứ 1
- `,` - Dấu phẩy = AND (mặc định)
- `name~*Admin*` - Điều kiện thứ  2
- Kết quả: role PHẢI là admin VÀ tên PHẢI chứa "Admin"

**Kết hợp nhiều AND:**
```
http://localhost:8080/api/v1/users?filter=role.name:admin,name~*Admin*,age>20
```
Kết quả: role là admin VÀ tên chứa "Admin" VÀ age > 20

---

### 6️⃣ Điều Kiện OR (Dấu Nháy ở Đầu)

**Tìm người dùng có role là "admin" HOẶC "manager":**
```
http://localhost:8080/api/v1/users?filter=role.name:admin,'role.name:manager
```

**Giải thích:**
- `role.name:admin` → Điều kiện thứ 1 (AND - mặc định, không có dấu nháy)
- `'role.name:manager` → Điều kiện thứ 2 (OR - có dấu nháy ở đầu)
- Kết quả: role PHẢI là admin HOẶC role PHẢI là manager

**Ví dụ khác - Tìm người dùng có tên "Admin" hoặc email chứa "test":**
```
http://localhost:8080/api/v1/users?filter=name:Admin,'email~*test*
```

Kết quả: (tên chính xác là "Admin") HOẶC (email chứa "test")
### 7️⃣ Kết Hợp Nhiều Điều Kiện (AND + OR)

**Tìm admin hoặc manager có tên chứa "Admin":**
```
http://localhost:8080/api/v1/users?filter=name~*Admin*,'role.name:admin,'role.name:manager
```

**Giải thích:**
- `name~*Admin*` - Tên phải chứa "Admin" (AND)
- `'role.name:admin` - HOẶC role là admin
- `'role.name:manager` - HOẶC role là manager
- Kết quả: (Tên chứa "Admin") VÀ (role là admin HOẶC role là manager)

---

## Tìm Kiếm Nâng Cao

### Phân Tích Từng Bước

**URL phức tạp:**
```
http://localhost:8080/api/v1/users?page=0&size=10&sort=id,desc&filter=email~*@gmail.com,role.name:admin,'status:inactive
```

**Phân tích:**
| Phần | Ý Nghĩa |
|------|---------|
| `page=0` | Trang thứ 0 (trang đầu tiên) |
| `size=10` | Lấy 10 bản ghi |
| `sort=id,desc` | Sắp xếp theo id giảm dần |
| `email~*@gmail.com,` | Email chứa "@gmail.com" (AND) |
| `role.name:admin,` | VÀ role là "admin" (AND) |
| `'status:inactive` | HOẶC status là "inactive" (OR) |

**Kết quả:** (Email chứa "@gmail.com" VÀ role là "admin") HOẶC (status là "inactive")

---

### Tìm Kiếm Kết Hợp Với Sắp Xếp

**Tìm người dùng, sắp xếp theo tên, lấy 20 bản ghi:**
```
http://localhost:8080/api/v1/users?page=0&size=20&sort=name,asc&filter=name~*A*
```

**Sắp xếp bởi nhiều trường:**
```
http://localhost:8080/api/v1/users?sort=role.name,asc&sort=name,desc&filter=email~*@gmail.com
```

---

## Các Lỗi Thường Gặp

### ❌ Lỗi 1: Quên Dấu Toán Tử

**Sai:**
```
http://localhost:8080/api/v1/users?filter=name*Admin*
```

**Đúng:**
```
http://localhost:8080/api/v1/users?filter=name~*Admin*
```

---

### ❌ Lỗi 2: Dùng Toán Tử Sai Loại

**Sai (Tìm số với LIKE):**
```
http://localhost:8080/api/v1/users?filter=id~100
```

**Đúng (Dùng > hoặc < cho số):**
```
http://localhost:8080/api/v1/users?filter=id>100
```

---

### ❌ Lỗi 3: Tên Trường Sai

**Sai (userRole không tồn tại):**
```
http://localhost:8080/api/v1/users?filter=userRole:admin
```

**Đúng (role là tên trường đúng):**
```
http://localhost:8080/api/v1/users?filter=role.name:admin
```

---

### ❌ Lỗi 4: Không Encode URL Đặc Biệt

**Cảnh báo:** Nếu filter chứa ký tự đặc biệt, cần encode URL:
- Dấu `&` → `%26`
- Dấu `,` → `%2C`
- Dấu `:` → `%3A`

**Ví dụ:**
```
http://localhost:8080/api/v1/users?filter=name~*Admin*%2Crole.name%3Aadmin
```

---

### ❌ Lỗi 5: Quên Dấu Nháy cho OR

**Sai (Tất cả là AND):**
```
http://localhost:8080/api/v1/users?filter=role.name:admin,role.name:manager
```
Kết quả: Không có kết quả (vì role không thể vừa là admin vừa là manager)

**Đúng (Dùng OR):**
```
http://localhost:8080/api/v1/users?filter=role.name:admin,'role.name:manager
```
Kết quả: Admin HOẶC Manager

---

## 📝 Bảng Tham Khảo Nhanh

### Các Toán Tử So Sánh

```
name:admin              → Tên chính xác là "admin"
name~admin              → Tên chứa "admin"
name~*admin*            → Tên chứa "admin" (cách khác)
name~admin*             → Tên bắt đầu với "admin"
name~*admin             → Tên kết thúc bằng "admin"
name!admin              → Tên không phải "admin"
age>18                  → Tuổi >= 18
age<65                  → Tuổi <= 65
role.name:admin         → Role là "admin" (join bảng)
```

### Toán Tử Kết Hợp Điều Kiện

```
condition1,condition2   → condition1 VÀ condition2 (AND - mặc định)
condition1,'condition2  → condition1 VÀ (HOẶC condition2) (OR - có dấu nháy)
```

### Kết Hợp Điều Kiện

```
# AND (mặc định - cách nhau bằng dấu phẩy)
filter=name~*Admin*,age>20,role.name:admin
# Kết quả: Tên chứa Admin VÀ age > 20 VÀ role là admin

# OR (bắt đầu bằng dấu nháy)
filter=role.name:admin,'role.name:manager
# Kết quả: role là admin HOẶC role là manager

# Phối hợp AND + OR
filter=name~*Admin*,age>20,'role.name:manager,'role.name:admin
# Kết quả: (Tên chứa "Admin" VÀ age > 20) VÀ (role là manager HOẶC role là admin)
```

---

## 🔍 Các Trường Có Thể Tìm Kiếm

### User (Người Dùng)
- `id` - ID người dùng
- `name` - Tên người dùng
- `email` - Email
- `gender` - Giới tính
- `role.name` - Tên role (join bảng)

### Product (Sản Phẩm)
- `id` - ID sản phẩm
- `name` - Tên sản phẩm
- `category.name` - Tên danh mục (join bảng)
- `price` - Giá

### Pet Service (Dịch Vụ Thú Cưng)
- `id` - ID dịch vụ
- `name` - Tên dịch vụ
- `basePrice` - Giá cơ bản
- `category.name` - Tên danh mục (join bảng)

---

## 💡 Mẹo và Lưu Ý

1. **Dấu phẩy luôn là AND** - Điều kiện cách nhau bằng `,` sẽ được kết hợp với AND
2. **Dấu nháy chỉ là prefix** - `'` phải ở đầu tiên để tạo OR 
3. **Luôn kiểm tra tên cột trong database** - Sử dụng tên cơ sở dữ liệu, không phải tên Java
4. **Khi join bảng, dùng dấu chấm** - Ví dụ: `role.name`, `category.name`
5. **Wildcard chỉ dùng với `~`** - Dùng `*` chỉ có tác dụng với toán tử LIKE (`~`)
6. **Multiple sort lưu ý thứ tự** - Sort ưu tiên theo thứ tự trong URL

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề:
1. Kiểm tra tên trường có tồn tại không
2. Xác nhận toán tử phù hợp với kiểu dữ liệu
3. Kiểm tra xem có cần join bảng không (dùng dấu chấm)
4. Thử gọi API mà không có filter xem có lỗi không

---

**Cập nhật lần cuối:** 08/06/2026
**Phiên bản:** 1.0

