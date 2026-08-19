# Apriori Recommendation API Guide

## 1. Mục tiêu
Tài liệu này mô tả workflow và nguyên lý kết nối model Apriori vào API Java Spring Boot của dự án.
Mục tiêu là cung cấp endpoint đề xuất sản phẩm/dịch vụ cho khách hàng dựa trên dữ liệu association rule đã huấn luyện.

## 2. Dữ liệu model hiện tại
- Model Apriori được lưu dưới dạng JSON thô.
- File JSON nằm trong classpath:
  - `src/main/resources/recommendation/product/association_rules.json`
  - `src/main/resources/recommendation/service/association_rules.json`
- Cấu trúc mỗi phần tử rule:
  - `antecedent`: danh sách id đầu vào
  - `consequent`: danh sách id đề xuất
  - `confidence`, `support`, `lift` (thông tin thống kê)

Ví dụ:
```json
{
  "antecedent": [1, 3],
  "consequent": [2],
  "confidence": 1.0,
  "support": 0.4,
  "lift": 1.0
}
```

## 3. Luồng xử lý API
1. Client gọi tới endpoint đề xuất `recommendations`.
2. Controller nhận request và lấy `itemIds` từ body hoặc query parameter.
3. Controller gọi service chuyên trách recommendation.
4. Service đọc file JSON Apriori (hoặc dùng cache nếu đã load rồi).
5. Service tìm các rule có `antecedent` là tập con của danh sách `itemIds` gửi lên.
6. Service trả về `consequent` từ những rule khớp, loại bỏ id đã có trong input và loại trùng.
7. Response trả lại cho client danh sách `recommendedItemIds`.

## 4. Kiến trúc implementation
### 4.1 Controller
- `ProductController`
  - `POST /products/recommendations`
  - `GET /products/recommendations?itemIds=1&itemIds=2`
- `PetServiceController`
  - `POST /services/recommendations`
  - `GET /services/recommendations?itemIds=1&itemIds=2`

### 4.2 DTO
- Request: `ReqRecommendationDto`
  - `itemIds: List<Long>`
- Response: `ResRecommendationDto`
  - `recommendedItemIds: List<Long>`

### 4.3 Service
- Interface: `RecommendationService`
  - `recommendProducts(List<Long> productIds)`
  - `recommendServices(List<Long> serviceIds)`
- Implementation: `RecommendationServiceImpl`
  - load JSON từ `ClassPathResource`
  - chuyển `antecedent` / `consequent` thành `List<Long>`
  - lọc rule khớp theo input

## 5. Cách hoạt động của model
- Dữ liệu đầu vào là tập `itemIds` khách hàng đã chọn hoặc quan tâm.
- Model Apriori chứa các quy tắc kết hợp hàng hóa/dịch vụ thường xuất hiện cùng nhau.
- Khi tất cả id trong `antecedent` xuất hiện trong input, hệ thống sẽ đề xuất các id trong `consequent`.
- Đầu ra là tập id đề xuất không chứa các id đã gửi ở input.

## 6. Ví dụ sử dụng
### POST
```bash
curl -X POST http://localhost:8080/api/v1/products/recommendations \
  -H "Content-Type: application/json" \
  -d '{"itemIds": [1, 3]}'
```
### GET
```bash
curl "http://localhost:8080/api/v1/products/recommendations?itemIds=1&itemIds=3"
```

## 7. Lưu ý mở rộng
- Nếu cần đề xuất chi tiết hơn, có thể mở rộng service để trả về `ProductDto`/`ServiceDto` thay vì chỉ id.
- Có thể bổ sung bộ lọc theo `confidence` hoặc `support` để chọn rule chất lượng.
- Nếu muốn dùng file `.pkl`, cần thêm tầng chuyển đổi Python/Java hoặc xuất ra JSON/CSV để Java đọc.

## 8. Gợi ý nâng cao
- Cache nội dung JSON khi ứng dụng khởi động để tránh đọc nhiều lần.
- Thêm validation input: `itemIds` không được null, phải có ít nhất 1 phần tử.
- Thêm logging khi rule được chọn, để kiểm tra và debug chất lượng đề xuất.

---

Tài liệu này giúp team hiểu rõ workflow tích hợp Apriori model vào API, từ lưu trữ file model đến dòng chảy request → controller → service → response.
