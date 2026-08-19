package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.constant.TypeInventory;
import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateProduct;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateProduct;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ProductDto;
import N18.haui.Pet_18.domain.entity.Category;
import N18.haui.Pet_18.domain.entity.Inventory;
import N18.haui.Pet_18.domain.entity.InventoryTransaction;
import N18.haui.Pet_18.domain.entity.Product;
import N18.haui.Pet_18.domain.mapper.ProductMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.ConflictException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.CategoryRepository;
import N18.haui.Pet_18.repository.InventoryRepository;
import N18.haui.Pet_18.repository.InventoryTransactionRepository;
import N18.haui.Pet_18.repository.ProductRepository;
import N18.haui.Pet_18.service.ProductService;
import N18.haui.Pet_18.service.RecommendationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final RecommendationService recommendationService;

    public void checkExistProductByName(String name) {
        log.info("[CHECK] Kiểm tra trùng lặp tên sản phẩm: '{}'", name);
        if (productRepository.existsByNameAndDeleteFlagFalse(name)) {
            log.warn("[CONFLICT] Tên sản phẩm '{}' đã tồn tại trong hệ thống", name);
            throw new ConflictException("Product already exists with name: " + name);
        }
    }

    @Override
    @Transactional
    public ProductDto createProduct(ReqCreateProduct reqCreateProduct) {
        log.info("[CREATE] Bắt đầu luồng tạo sản phẩm mới với tên: '{}'", reqCreateProduct.getName());

        // 1. Kiểm tra trùng tên
        checkExistProductByName(reqCreateProduct.getName());

        // 2. Map DTO sang Entity
        Product product = productMapper.toProduct(reqCreateProduct);
        log.info("[CREATE] Đã map dữ liệu request sang Entity Product");

        // 3. Xử lý Category
        if (reqCreateProduct.getCategoryId() != null) {
            log.info("[CREATE] Đang tìm kiếm Category liên kết với ID: {}", reqCreateProduct.getCategoryId());
            Category category = categoryRepository.findById(reqCreateProduct.getCategoryId())
                    .orElseThrow(() -> {
                        log.warn("[NOT_FOUND] Không tìm thấy Category ID: {}", reqCreateProduct.getCategoryId());
                        return new NotFoundException("Category not found with id: " + reqCreateProduct.getCategoryId());
                    });
            product.setCategory(category);
            log.info("[CREATE] Đã liên kết sản phẩm với Category: '{}'", category.getName());
        }

        // 4. Lưu Product TRƯỚC để sinh ra ID tự động
        product = productRepository.save(product);
        log.info("[CREATE] Lưu sản phẩm thành công. Đã sinh ra Product ID: {}", product.getId());

        // 5. Xử lý Inventory (Khởi tạo kho hàng)
            log.info("[CREATE] Đang khởi tạo bản ghi Inventory với số lượng ban đầu: {}", reqCreateProduct.getQuantity());
            Inventory inventory = new Inventory();
            inventory.setQuantity(reqCreateProduct.getQuantity() != null ? reqCreateProduct.getQuantity() : 0);
            inventory.setProduct(product);

            inventoryRepository.save(inventory);
            product.setInventory(inventory);
            log.info("[CREATE] Đã lưu Inventory thành công cho sản phẩm ID: {}", product.getId());

        if(reqCreateProduct.getQuantity() != null && reqCreateProduct.getQuantity() > 0){
            createInventoryTransaction(inventory, reqCreateProduct.getQuantity(), TypeInventory.IMPORT, "Nhập kho khi tạo sản phẩm");
        }

        log.info("[CREATE] Hoàn thành tạo sản phẩm mới thành công (ID: {})", product.getId());
        return productMapper.toProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ReqUpdateProduct reqUpdateProduct) {
        log.info("[UPDATE] Bắt đầu luồng cập nhật sản phẩm có ID: {}", reqUpdateProduct.getId());

        Product updateProduct = productRepository.findById(reqUpdateProduct.getId()).orElseThrow(() -> {
            log.warn("[NOT_FOUND] Thất bại khi cập nhật, không tồn tại sản phẩm ID: {}", reqUpdateProduct.getId());
            return new NotFoundException("Product not found with id: " + reqUpdateProduct.getId());
        });
        if (updateProduct.getActiveFlag() == false || updateProduct.getDeleteFlag() == true){
            log.warn("[NOT_FOUND] Thất bại khi cập nhật, không tồn tại sản phẩm ID: {}", reqUpdateProduct.getId());
            throw new NotFoundException("Product not found with id: " + reqUpdateProduct.getId());
        }

        // Kiểm tra trùng tên chỉ khi tên mới khác tên cũ hiện tại
        if (!updateProduct.getName().equals(reqUpdateProduct.getName())) {
            log.info("[UPDATE] Phát hiện tên sản phẩm thay đổi từ '{}' sang '{}'", updateProduct.getName(), reqUpdateProduct.getName());
            checkExistProductByName(reqUpdateProduct.getName());
        }

        updateProduct.setName(reqUpdateProduct.getName());
        updateProduct.setDescription(reqUpdateProduct.getDescription());
        updateProduct.setPrice(reqUpdateProduct.getPrice());

        // Cập nhật Danh mục (Category)
        if (reqUpdateProduct.getCategoryId() != null) {
            log.info("[UPDATE] Đang cập nhật liên kết sang Category ID: {}", reqUpdateProduct.getCategoryId());
            Category category = categoryRepository.findById(reqUpdateProduct.getCategoryId()).orElseThrow(() -> {
                log.warn("[NOT_FOUND] Không tìm thấy Category ID: {}", reqUpdateProduct.getCategoryId());
                return new NotFoundException("Category not found with id: " + reqUpdateProduct.getCategoryId());
            });
            updateProduct.setCategory(category);
        }

        // Cập nhật Số lượng kho (Inventory)
        if (reqUpdateProduct.getQuantity() != null) {
            Inventory inventory = updateProduct.getInventory();
            if(inventory == null){
                inventory = new Inventory();
                inventory.setQuantity(reqUpdateProduct.getQuantity());
                inventory.setProduct(updateProduct);
                inventoryRepository.save(inventory);
                updateProduct.setInventory(inventory);
                if(reqUpdateProduct.getQuantity() > 0){
                    createInventoryTransaction(inventory, reqUpdateProduct.getQuantity(), TypeInventory.IMPORT, "Nhập kho khi cập nhật sản phẩm");
                }
            } else{
                int oldQuantity = inventory.getQuantity();
                int newQuantity = reqUpdateProduct.getQuantity();
                int delta = newQuantity - oldQuantity;
                if(delta != 0){
                    log.info("[UPDATE] Số lượng kho thay đổi từ {} → {}, delta: {}", oldQuantity, newQuantity, delta);
                    inventory.setQuantity(newQuantity);
                    inventoryRepository.save(inventory);
                }
                TypeInventory type = delta > 0 ? TypeInventory.IMPORT : TypeInventory.EXPORT;
                InventoryTransaction inventoryTransaction = new InventoryTransaction();
                // lấy giá trị tuyệt đối của delta: thành số dương
                inventoryTransaction.setQuantity(Math.abs(delta));
                inventoryTransaction.setType(type);
                inventoryTransaction.setNote("Cập nhật kho khi cập nhật sản phẩm");
                inventoryTransaction.setInventory(inventory);
                inventoryTransactionRepository.save(inventoryTransaction);
                log.info("[UPDATE] Đã tạo bản ghi InventoryTransaction cho sản phẩm ID: {} với số lượng: {}, type: {}", updateProduct.getId(), Math.abs(delta), type);

            }
        }

        productRepository.save(updateProduct);
        log.info("[UPDATE] Cập nhật thành công mọi thông tin cho sản phẩm ID: {}", updateProduct.getId());
        return productMapper.toProductDto(updateProduct);
    }

    @Override
    @Transactional
    public CommonResponseDto deleteProduct(Long id) {
        log.info("[DELETE] Bắt đầu yêu cầu xóa mềm sản phẩm có ID: {}", id);

        Product deleteProduct = productRepository.findById(id).orElseThrow(() -> {
            log.warn("[NOT_FOUND] Thất bại khi xóa, không tồn tại sản phẩm ID: {}", id);
            return new NotFoundException("Product not found with id: " + id);
        });

        deleteProduct.setDeleteFlag(true);
        productRepository.save(deleteProduct);

        log.info("[DELETE] Đã bật deleteFlag = true thành công cho sản phẩm ID: {}", id);
        return new CommonResponseDto(true, "Delete product successfully");
    }

    @Override
    public ProductDto getProductById(Long id) {
        log.info("[GET_BY_ID] Đang truy vấn chi tiết sản phẩm ID: {}", id);

        Product product = productRepository.findById(id).orElseThrow(() -> {
            log.warn("[NOT_FOUND] Không tìm thấy sản phẩm có ID: {}", id);
            return new NotFoundException("Product not found with id: " + id);
        });
        if (product.getActiveFlag() == false && product.getDeleteFlag() == true){
            log.warn("[NOT_FOUND] Không tìm thấy sản phẩm có ID: {}", id);
            throw new NotFoundException("Product not found with id: " + id);
        }


        if (Boolean.TRUE.equals(product.getDeleteFlag())) {
            log.warn("[NOT_FOUND] Truy cập thất bại! Sản phẩm ID: {} đã bị xóa mềm trước đó", id);
            throw new NotFoundException("Product not found with id: " + id);
        }

        log.info("[GET_BY_ID] Truy vấn thành công sản phẩm: '{}'", product.getName());
        return productMapper.toProductDto(product);
    }

    @Override
    public ResultPaginationDto getAllProduct(List<String> filter, Pageable pageable) {
        log.info("[GET_ALL] Yêu cầu lấy danh sách sản phẩm. Page: {}, Size: {}, Sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        if (filter != null && !filter.isEmpty()) {
            log.info("[GET_ALL] Áp dụng các bộ lọc tìm kiếm (Filter): {}", filter);
        }

        // Khởi tạo cấu trúc query động từ bộ lọc dữ liệu
        SpecificationBuilder<Product> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);

        Specification<Product> spec = specificationBuilder.build();
        Specification<Product> softDeleteSpec = (root, query, cb) -> cb.equal(root.get("deleteFlag"), false);

        // Gom các cấu trúc query lại thành câu lệnh kiểm tra cuối cùng
        Specification<Product> finalSpec = (spec == null) ? softDeleteSpec : spec.and(softDeleteSpec);

        // Thực thi gọi Database lấy dữ liệu phân trang
        Page<Product> pageUser = productRepository.findAll(finalSpec, pageable);
        log.info("[GET_ALL] Kết quả truy vấn Database: Tìm thấy {} bản ghi phù hợp. Tổng số trang: {}",
                pageUser.getTotalElements(), pageUser.getTotalPages());

        // Đóng gói ánh xạ dữ liệu đầu ra trả về Client
        ResultPaginationDto resultPaginationDTO = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        List<ProductDto> result = productMapper.productDtos(pageUser.getContent());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(result);

        log.info("[GET_ALL] Hoàn thành phân trang dữ liệu sản phẩm thành công.");
        return resultPaginationDTO;
    }

    @Override
    public List<Long> getRecommendedProductIds(List<Long> productIds) {
        return recommendationService.recommendProducts(productIds);
    }

    private void createInventoryTransaction(Inventory inventory, Integer quantity, TypeInventory type, String note){
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setQuantity(quantity);
        inventoryTransaction.setType(type);
        inventoryTransaction.setNote(note);
        inventoryTransaction.setInventory(inventory);
        inventoryTransactionRepository.save(inventoryTransaction);
        log.info("[INVENTORY_TRANSACTION] Đã tạo bản ghi InventoryTransaction cho sản phẩm ID: {} với số lượng: {}, type: {}, note: {}", inventory.getProduct().getId(), quantity, type, note);
    }
}