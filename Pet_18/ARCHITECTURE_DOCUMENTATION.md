# Pet-N18 Backend Architecture Documentation

## 📚 Mục Lục
1. [Kiến Trúc Tổng Quát](#kiến-trúc-tổng-quát)
2. [Các Tầng (Layers)](#các-tầng-layers)
3. [Những Gì Được Tạo](#những-gì-được-tạo)
4. [API Endpoints](#api-endpoints)
5. [Hướng Dẫn Test](#hướng-dẫn-test)

---

## 🏗️ Kiến Trúc Tổng Quát

Dự án Pet-N18 sử dụng **Layered Architecture (Kiến Trúc Phân Tầng)** được thiết kế tối ưu cho các ứng dụng Spring Boot:

```
┌─────────────────────────────────────┐
│        Controller (HTTP Layer)       │  ← Xử lý requests/responses
├─────────────────────────────────────┤
│        Service (Business Logic)      │  ← Xử lý business rules
├─────────────────────────────────────┤
│        Repository (Data Access)      │  ← Tương tác với DB
├─────────────────────────────────────┤
│        Entity (Domain Models)        │  ← JPA entities
├─────────────────────────────────────┤
│        DTO (Data Transfer Object)    │  ← Định nghĩa data format
└─────────────────────────────────────┘
```

### Mục Đích Của Mỗi Tầng:

| Tầng              | Trách Nhiệm                                         | Công Nghệ                      |
|-------------------|-----------------------------------------------------|--------------------------------|
| **Controller**    | Tiếp nhận HTTP requests, validate, trả về responses | Spring MVC Annotations         |
| **Service**       | Xử lý business logic, transaction, validation       | Spring Service, @Transactional |
| **Repository**    | Truy vấn database, CRUD operations                  | Spring Data JPA                |
| **Entity**        | Định nghĩa cấu trúc bảng database                   | JPA Annotations, Lombok        |
| **DTO**           | Định dạng data gửi/nhận từ client                   | Lombok, MapStruct Mapper       |
| **Mapper**        | Chuyển đổi Entity ↔ DTO                             | MapStruct                      |

---

## 📦 Các Tầng (Layers)

### 1️⃣ **Entity Layer** (Domain Models)
**Vị trí:** `domain/entity/`

```java
@Entity
@Table(name = "tbl_services")
public class PetService extends FlagUserDateAuditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToMany(mappedBy = "petService")
    private List<PetServiceImage> serviceImages;
}
```

**Đặc điểm:**
- Ánh xạ trực tiếp với các bảng database
- Sử dụng Lombok (@Getter, @Setter) để giảm boilerplate code
- Kế thừa `FlagUserDateAuditing` để track user, created/updated date
- Sử dụng `@JsonIgnore` để tránh serialization vòng lặp

---

### 2️⃣ **DTO Layer** (Data Transfer Objects)
**Vị trí:** `domain/dto/request/` và `domain/dto/response/`

**Request DTOs:**
```java
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ReqCreateService {
    @NotBlank(message = "Service name is required")
    private String name;
    
    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;
}
```

**Response DTOs:**
```java
@Getter @Setter
public class ServiceDto {
    private Long id;
    private String name;
    private BigDecimal basePrice;
    private Double averageRating;      // Tính toán từ reviews
    private Integer totalReviews;      // Aggregated data
    private List<ServiceImageDto> serviceImages;
}
```

**Lợi Ích:**
✅ Validation tại tầng input (@NotNull, @Positive, @Min, @Max)  
✅ Chỉ expose các field cần thiết (bảo mật)  
✅ Độc lập với Entity - không ảnh hưởng khi thay đổi DB schema  
✅ Response DTOs có thể chứa dữ liệu aggregated (avg rating, count)

---

### 3️⃣ **Mapper Layer** (Entity ↔ DTO Conversion)
**Vị trí:** `domain/mapper/`

```java
@Mapper(componentModel = "spring", uses = ServiceImageMapper.class)
public interface ServiceMapper {
    
    @Mapping(target = "categoryName", source = "category.name")
    ServiceDto toDto(PetService service);
    
    List<ServiceDto> toDtos(List<PetService> services);
}
```

**Công Nghệ:** MapStruct
- **Ưu điểm:** Code generation, tự động compile, không sử dụng reflection
- **Performance:** Cực nhanh (giống thủ công)
- **Type-safe:** Phát hiện lỗi compile time

**Ví dụ mapping phức tạp:**
```java
@Mapping(
    target = "totalAmount",
    expression = "java(orderDetail.getUnitPrice()
        .multiply(BigDecimal.valueOf(orderDetail.getQuantity())))"
)
ServiceDetailDto toDto(OrderDetail orderDetail);
```

---

### 4️⃣ **Repository Layer** (Data Access)
**Vị trí:** `repository/`

```java
@Repository
public interface PetServiceRepository extends JpaRepository<PetService, Long>, 
    JpaSpecificationExecutor<PetService> {
    
    Page<PetService> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<PetService> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
```

**Các Tính Năng:**
- ✅ **JpaRepository** - CRUD cơ bản (save, find, delete, etc)
- ✅ **JpaSpecificationExecutor** - Complex queries, filtering
- ✅ **Query Methods** - Viết query bằng method name
- ✅ **Pageable** - Pagination tự động
- ✅ **Custom @Query** - JPQL/SQL tùy chỉnh

**Ví dụ Query Custom:**
```java
@Query("SELECT AVG(r.rating) FROM PetServiceReview r 
        WHERE r.petService.id = :serviceId")
Double getAverageRating(@Param("serviceId") Long serviceId);
```

---

### 5️⃣ **Service Layer** (Business Logic)
**Vị trị:** `service/` và `service/impl/`

**Interface Pattern:**
```java
public interface PetServiceService {
    ServiceDto createService(ReqCreateService req);
    ServiceDto updateService(ReqUpdateService req);
    ServiceDto getServiceById(Long id);
    ResultPaginationDto getAllServices(Pageable pageable);
    ResultPaginationDto searchServices(String keyword, Pageable pageable);
}
```

**Implementation:**
```java
@Service
@RequiredArgsConstructor  // Constructor injection
@Slf4j                    // Logging
public class PetServiceServiceImpl implements PetServiceService {
    
    private final PetServiceRepository petServiceRepository;
    private final ServiceMapper serviceMapper;
    
    @Override
    @Transactional  // Atomic operation
    public ServiceDto createService(ReqCreateService req) {
        log.info("[SERVICE] Creating new service: {}", req.getName());
        
        // 1. Validation & Business Rule Check
        Category category = categoryRepository.findById(req.getCategoryId())
            .orElseThrow(() -> new NotFoundException("[SERVICE] Category not found"));
        
        // 2. Create Entity
        PetService service = new PetService();
        service.setName(req.getName());
        service.setBasePrice(req.getBasePrice());
        service.setCategory(category);
        
        // 3. Persist
        PetService saved = petServiceRepository.save(service);
        
        // 4. Convert to DTO with enriched data
        ServiceDto dto = serviceMapper.toDto(saved);
        enrichServiceDto(dto);  // Thêm avg rating, count reviews
        
        return dto;
    }
    
    @Override
    public ResultPaginationDto getAllServices(Pageable pageable) {
        Page<PetService> page = petServiceRepository.findAll(pageable);
        
        List<ServiceDto> dtos = page.getContent().stream()
            .map(service -> {
                ServiceDto dto = serviceMapper.toDto(service);
                enrichServiceDto(dto);
                return dto;
            })
            .toList();
        
        return buildPaginationResponse(dtos, page);
    }
    
    private <T> ResultPaginationDto buildPaginationResponse(List<T> data, Page<?> page) {
        ResultPaginationDto response = new ResultPaginationDto();
        response.setResult(data);
        
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(page.getNumber());
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        response.setMeta(meta);
        
        return response;
    }
}
```

**Key Patterns:**
- 🔐 **@Transactional** - Đảm bảo atomicity (tất cả thành công hoặc tất cả thất bại)
- 🎯 **Exception Handling** - Throw custom exceptions (NotFoundException, BadRequestException)
- 📊 **Enrichment** - Thêm aggregated data (average rating, total reviews)
- 📄 **Pagination** - Hỗ trợ lấy dữ liệu phân trang

---

### 6️⃣ **Controller Layer** (HTTP Endpoints)
**Vị trị:** `controller/`

```java
@RestApiV1  // Custom annotation: @RestController + @RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PetServiceController {
    
    private final PetServiceService petServiceService;
    
    @PostMapping(UrlConstant.PetService.CREATE_SERVICE)
    public ResponseEntity<?> createService(@Valid @RequestBody ReqCreateService req) {
        return VsResponseUtil.success(HttpStatus.CREATED, 
            petServiceService.createService(req));
    }
    
    @GetMapping(UrlConstant.PetService.GET_SERVICE)
    public ResponseEntity<?> getService(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, 
            petServiceService.getServiceById(id));
    }
    
    @GetMapping(UrlConstant.PetService.GET_ALL_SERVICES)
    public ResponseEntity<?> getAllServices(Pageable pageable) {
        return VsResponseUtil.success(HttpStatus.OK, 
            petServiceService.getAllServices(pageable));
    }
}
```

**Annotation Usage:**
- `@RestApiV1` - Custom base path `/api/v1`
- `@PostMapping` - HTTP POST
- `@GetMapping` - HTTP GET
- `@PatchMapping` - HTTP PATCH (partial update)
- `@DeleteMapping` - HTTP DELETE
- `@PathVariable` - Extract from URL path
- `@RequestParam` - Query parameters
- `@RequestBody` - JSON body
- `@Valid` - Trigger DTO validation

**Response Wrapper:**
```java
return VsResponseUtil.success(HttpStatus.OK, data);
// Response format:
// {
//     "meta": { "code": 200, "message": "Success" },
//     "data": { ... }
// }
```

---

## 🎯 Những Gì Được Tạo

### 📁 File Structure Tạo Mới:

```
src/main/java/N18/haui/Pet_18/
├── domain/
│   ├── dto/
│   │   ├── request/
│   │   │   ├── ReqCreateService.java
│   │   │   ├── ReqUpdateService.java
│   │   │   ├── ReqCreateServiceReview.java
│   │   │   └── ReqAddServiceImage.java
│   │   └── response/
│   │       ├── ServiceDto.java
│   │       ├── ServiceImageDto.java
│   │       └── ServiceReviewDto.java
│   └── mapper/
│       ├── ServiceMapper.java
│       ├── ServiceImageMapper.java
│       └── ServiceReviewMapper.java
│
├── repository/
│   ├── PetServiceRepository.java
│   ├── PetServiceImageRepository.java
│   └── PetServiceReviewRepository.java
│
├── service/
│   ├── PetServiceService.java
│   ├── PetServiceImageService.java
│   ├── PetServiceReviewService.java
│   └── impl/
│       ├── PetServiceServiceImpl.java
│       ├── PetServiceImageServiceImpl.java
│       └── PetServiceReviewServiceImpl.java
│
└── controller/
    ├── PetServiceController.java
    ├── PetServiceImageController.java
    └── PetServiceReviewController.java
```

### 📊 Tổng Quan Implementation:

| Component | Files | Chức Năng |
|-----------|-------|---------|
| **DTOs** | 7 files | Request validation, Response formatting |
| **Mappers** | 3 files | Entity ↔ DTO conversion |
| **Repositories** | 3 files | Database access |
| **Services** | 6 files (3 interface + 3 impl) | Business logic |
| **Controllers** | 3 files | HTTP endpoints |

### 🔧 Tính Năng Chính:

#### 1. **PetService Management**
- ✅ CRUD operations (Create, Read, Update, Delete)
- ✅ Search by name (case-insensitive)
- ✅ Filter by category
- ✅ Get top services (limit configurable)
- ✅ Average rating aggregation
- ✅ Review count aggregation

#### 2. **PetServiceImage Management**
- ✅ Add multiple images to service
- ✅ Delete image
- ✅ Get all service images
- ✅ Set main/thumbnail image
- ✅ Automatic thumbnail deactivation

#### 3. **PetServiceReview Management**
- ✅ Create review (rating 1-5 stars)
- ✅ One review per user per service (prevent duplicates)
- ✅ Delete own reviews only
- ✅ Get reviews by service (paginated)
- ✅ Calculate average rating
- ✅ Count total reviews

---

## 🔌 API Endpoints

### Base URL: `http://localhost:8080/api/v1`

### 📌 Service Management

#### 1. **Create Service** [POST]
```
POST /services
Content-Type: application/json

{
  "name": "Dog Grooming",
  "description": "Professional dog grooming service",
  "basePrice": 50.00,
  "durationMin": 60,
  "categoryId": 1
}

Response (201):
{
  "meta": { "code": 201, "message": "Created" },
  "data": {
    "id": 1,
    "name": "Dog Grooming",
    "description": "Professional dog grooming service",
    "basePrice": 50.00,
    "durationMin": 60,
    "categoryId": 1,
    "categoryName": "Grooming",
    "averageRating": null,
    "totalReviews": 0,
    "serviceImages": [],
    "createdDate": "2026-06-09T10:30:00"
  }
}
```

#### 2. **Update Service** [PATCH]
```
PATCH /services
Content-Type: application/json

{
  "id": 1,
  "name": "Premium Dog Grooming",
  "basePrice": 75.00,
  "durationMin": 90
}

Response (200):
{
  "meta": { "code": 200, "message": "Success" },
  "data": { ... updated service ... }
}
```

#### 3. **Get Service by ID** [GET]
```
GET /services/1

Response (200):
{
  "meta": { "code": 200, "message": "Success" },
  "data": { ... service details ... }
}
```

#### 4. **Get All Services** [GET]
```
GET /services?page=0&size=10&sort=id,desc

Response (200):
{
  "meta": {
    "page": 0,
    "pageSize": 10,
    "pages": 5,
    "total": 50
  },
  "result": [
    { ... service 1 ... },
    { ... service 2 ... }
  ]
}
```

#### 5. **Search Services** [GET]
```
GET /services/search?keyword=grooming&page=0&size=10

Response (200):
{
  "meta": { ... },
  "result": [ ... services matching "grooming" ... ]
}
```

#### 6. **Get Services by Category** [GET]
```
GET /services/category/1?page=0&size=10

Response (200):
{
  "meta": { ... },
  "result": [ ... services in category 1 ... ]
}
```

#### 7. **Get Top Services** [GET]
```
GET /services/top?limit=6

Response (200):
{
  "meta": { "code": 200, "message": "Success" },
  "data": [ ... top 6 services ... ]
}
```

#### 8. **Delete Service** [DELETE]
```
DELETE /services/1

Response (204):
No Content
```

---

### 🖼️ Service Image Management

#### 1. **Add Service Image** [POST]
```
POST /service-images
Content-Type: application/json

{
  "serviceId": 1,
  "imageUrl": "https://example.com/image1.jpg",
  "isThumbnail": true
}

Response (201):
{
  "meta": { "code": 201, "message": "Created" },
  "data": {
    "id": 1,
    "imageUrl": "https://example.com/image1.jpg",
    "isThumbnail": true,
    "serviceId": 1
  }
}
```

#### 2. **Delete Service Image** [DELETE]
```
DELETE /service-images/1

Response (204):
No Content
```

#### 3. **Get Service Images** [GET]
```
GET /service-images/service/1

Response (200):
{
  "meta": { "code": 200, "message": "Success" },
  "data": [
    { "id": 1, "imageUrl": "...", "isThumbnail": true },
    { "id": 2, "imageUrl": "...", "isThumbnail": false }
  ]
}
```

#### 4. **Set Main Image** [PATCH]
```
PATCH /service-images/set-main-image?imageId=1

Response (200):
{
  "meta": { "code": 200, "message": "Success" }
}
```

---

### ⭐ Service Review Management

#### 1. **Create Review** [POST]
```
POST /service-reviews
Content-Type: application/json

{
  "serviceId": 1,
  "rating": 5,
  "comment": "Excellent service! Very professional."
}

Response (201):
{
  "meta": { "code": 201, "message": "Created" },
  "data": {
    "id": 1,
    "serviceId": 1,
    "userId": 123,
    "userName": "John Doe",
    "rating": 5,
    "comment": "Excellent service! Very professional.",
    "createdDate": "2026-06-09T10:30:00"
  }
}
```

#### 2. **Delete Review** [DELETE]
```
DELETE /service-reviews/1

Response (204):
No Content
```

#### 3. **Get Service Reviews** [GET]
```
GET /service-reviews/service/1?page=0&size=10

Response (200):
{
  "meta": {
    "page": 0,
    "pageSize": 10,
    "pages": 2,
    "total": 15
  },
  "result": [
    {
      "id": 1,
      "serviceId": 1,
      "userId": 123,
      "userName": "John Doe",
      "rating": 5,
      "comment": "Excellent!",
      "createdDate": "2026-06-09T10:30:00"
    },
    ...
  ]
}
```

#### 4. **Get Average Rating** [GET]
```
GET /service-reviews/1/average-rating

Response (200):
{
  "meta": { "code": 200, "message": "Success" },
  "data": 4.5
}
```

#### 5. **Get Review Count** [GET]
```
GET /service-reviews/1/count

Response (200):
{
  "meta": { "code": 200, "message": "Success" },
  "data": 25
}
```

---

## 🧪 Hướng Dẫn Test

### Công Cụ Recommended:
- **Postman** - GUI
- **cURL** - Command line
- **Thunder Client** - VS Code extension
- **RestClient** - IntelliJ built-in

### Test Workflow:

#### Step 1: Tạo Service Mới
```bash
curl -X POST http://localhost:8080/api/v1/services \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pet Bathing",
    "description": "Professional pet bathing service",
    "basePrice": 35.00,
    "durationMin": 45,
    "categoryId": 1
  }'
```

#### Step 2: Lấy Service vừa tạo (note ID từ response)
```bash
curl -X GET http://localhost:8080/api/v1/services/1
```

#### Step 3: Thêm Ảnh cho Service
```bash
curl -X POST http://localhost:8080/api/v1/service-images \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId": 1,
    "imageUrl": "https://example.com/bath1.jpg",
    "isThumbnail": true
  }'
```

#### Step 4: Tạo Review cho Service
```bash
curl -X POST http://localhost:8080/api/v1/service-reviews \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId": 1,
    "rating": 5,
    "comment": "Great service!"
  }'
```

#### Step 5: Lấy Average Rating
```bash
curl -X GET http://localhost:8080/api/v1/service-reviews/1/average-rating
```

#### Step 6: Lấy danh sách Services với Pagination
```bash
curl -X GET "http://localhost:8080/api/v1/services?page=0&size=5&sort=id,desc"
```

#### Step 7: Search Services
```bash
curl -X GET "http://localhost:8080/api/v1/services/search?keyword=bath&page=0&size=10"
```

---

## 📝 Postman Collection Template

```json
{
  "info": {
    "name": "Pet-N18 Service API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Services",
      "item": [
        {
          "name": "Create Service",
          "request": {
            "method": "POST",
            "url": "{{base_url}}/services",
            "body": {
              "mode": "raw",
              "raw": "{\"name\": \"Dog Grooming\", \"description\": \"...\", \"basePrice\": 50, \"durationMin\": 60, \"categoryId\": 1}"
            }
          }
        },
        {
          "name": "Get All Services",
          "request": {
            "method": "GET",
            "url": "{{base_url}}/services?page=0&size=10"
          }
        }
      ]
    },
    {
      "name": "Service Images",
      "item": [
        {
          "name": "Add Image",
          "request": {
            "method": "POST",
            "url": "{{base_url}}/service-images",
            "body": {
              "mode": "raw",
              "raw": "{\"serviceId\": 1, \"imageUrl\": \"...\", \"isThumbnail\": true}"
            }
          }
        }
      ]
    },
    {
      "name": "Service Reviews",
      "item": [
        {
          "name": "Create Review",
          "request": {
            "method": "POST",
            "url": "{{base_url}}/service-reviews",
            "body": {
              "mode": "raw",
              "raw": "{\"serviceId\": 1, \"rating\": 5, \"comment\": \"Great!\"}"
            }
          }
        }
      ]
    }
  ]
}
```

---

## 🎓 Key Learning Points

### ✅ Architecture Patterns Áp Dụng:
1. **Dependency Injection** - @RequiredArgsConstructor, constructor injection
2. **Service Layer Pattern** - Interface + Implementation
3. **Repository Pattern** - Data access abstraction
4. **DTO Pattern** - Separate transfer objects from entities
5. **Mapper Pattern** - Conversion layer (MapStruct)
6. **Transaction Management** - @Transactional for atomicity
7. **Exception Handling** - Custom exceptions, proper HTTP status codes
8. **Pagination** - Spring Data Pageable support
9. **Validation** - Jakarta validation annotations
10. **Logging** - SLF4J structured logging

### ✅ Best Practices Được Thực Hiện:
- 🔒 Request validation at DTO level
- 🔐 Business logic in service layer (not controller)
- 📊 Aggregated data enrichment in service
- 🗂️ Clear separation of concerns
- 🔄 Stateless REST design
- 📝 Consistent error handling
- 🧪 Testable architecture
- 📄 Resource-oriented URLs

### ✅ Technologies Used:
- **Spring Boot** - Framework
- **Spring Data JPA** - ORM
- **MapStruct** - DTO Mapping
- **Lombok** - Boilerplate reduction
- **Hibernate** - JPA Implementation
- **Jakarta Validation** - Input validation

---

## 🚀 Next Steps (Future Improvements):

1. ✏️ Add service scheduling/booking functionality
2. 🔐 Implement role-based access control (Admin vs User)
3. 📸 Add image upload (file storage integration)
4. 🌟 Add service rating/recommendation algorithm
5. 💳 Integrate payment processing for services
6. 📧 Add email notifications
7. 🧪 Add comprehensive unit/integration tests
8. 📊 Add analytics/reporting endpoints

---

**Generated:** 2026-06-09  
**Author:** AI Assistant  
**Project:** Pet-N18 Backend
