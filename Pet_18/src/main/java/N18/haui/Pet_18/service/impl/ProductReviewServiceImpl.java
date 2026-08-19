package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.constant.RoleConstant;
import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateProductReview;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateProductReview;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ProductReviewDto;
import N18.haui.Pet_18.domain.dto.response.ProductReviewResponseDto;
import N18.haui.Pet_18.domain.entity.Product;
import N18.haui.Pet_18.domain.entity.ProductReview;
import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.domain.mapper.ProductReviewMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.ForbiddenException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.OrderDetailRepository;
import N18.haui.Pet_18.repository.ProductRepository;
import N18.haui.Pet_18.repository.ProductReviewRepository;
import N18.haui.Pet_18.service.ProductReviewService;
import N18.haui.Pet_18.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReviewServiceImpl implements ProductReviewService {
    private final ProductReviewRepository productReviewRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductReviewMapper productReviewMapper;


    @Override
    @Transactional
    public ProductReviewDto createReview(ReqCreateProductReview req) {
        log.info("[REVIEW] Thêm đánh giá sản phẩm ID: {}", req.getProductId());

        User currentUser = userService.getUserLogin();

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> {
                    log.warn("[NOT_FOUND] Không tìm thấy sản phẩm ID: {}", req.getProductId());
                    return new NotFoundException("Product with ID: " + req.getProductId() + " not found");
                });

        //  Kiểm tra đã mua + đơn DELIVERED chưa
        boolean hasPurchased = orderDetailRepository
                .existsByProductIdAndOrderUserIdAndOrderStatus(
                        req.getProductId(),
                        currentUser.getId(),
                        OrderStatus.DELIVERED
                );

        if (!hasPurchased) {
            throw new BadRequestException("You need to purchase and receive the product first to review it");
        }

        //  Kiểm tra đã đánh giá chưa
        if (productReviewRepository.existsByUserIdAndProductIdAndDeleteFlagFalse(
                currentUser.getId(), req.getProductId())) {
            throw new BadRequestException("You have already reviewed this product before");
        }

        //  Tạo ProductReview
        ProductReview review = ProductReview.builder()
                .user(currentUser)
                .product(product)
                .rating(req.getRating())
                .comment(req.getComment())
                .build();

        productReviewRepository.save(review);
        log.info("[REVIEW] Lưu review thành công | Product ID: {} | User ID: {}",
                req.getProductId(), currentUser.getId());





        int newTotalReviews = product.getTotalReviews() + 1;
        double newAvgRating = ((product.getAvgRating() * product.getTotalReviews())
                + req.getRating()) / newTotalReviews;

        product.setTotalReviews(newTotalReviews);
        product.setAvgRating(Math.round(newAvgRating * 10.0) / 10.0);
        productRepository.save(product);

        log.info("[REVIEW] Cập nhật avgRating: {} | totalReviews: {} | Product ID: {}",
                product.getAvgRating(), product.getTotalReviews(), req.getProductId());

        return productReviewMapper.toDto(review);
    }

    @Override
    @Transactional
    public ProductReviewDto updateReview(ReqUpdateProductReview req) {
        log.info("[REVIEW] Cập nhật đánh giá ID: {}", req.getReviewId());

        // Tìm review
        ProductReview review = productReviewRepository.findById(req.getReviewId())
                .orElseThrow(() -> {
                    log.warn("[NOT_FOUND] Không tìm thấy review ID: {}", req.getReviewId());
                    return new NotFoundException("Review with ID: " + req.getReviewId() + " not found");
                });

        // Kiểm tra đã bị xóa chưa
        if (Boolean.TRUE.equals(review.getDeleteFlag())) {
            throw new NotFoundException("Review with ID: " + req.getReviewId() + " not found");
        }

        //  Kiểm tra có thuộc về user hiện tại không
        User currentUser = userService.getUserLogin();
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to update this review");
        }



        // Nếu rating thay đổi → tính lại avgRating
        Product product = review.getProduct();
        int oldRating = review.getRating();
        int newRating = req.getRating();

        if (oldRating != newRating) {
            double newAvgRating = ((product.getAvgRating() * product.getTotalReviews())
                    - oldRating + newRating) / product.getTotalReviews();

            product.setAvgRating(Math.round(newAvgRating * 10.0) / 10.0);
            productRepository.save(product);

            log.info("[REVIEW] Cập nhật avgRating: {} | Product ID: {}",
                    product.getAvgRating(), product.getId());
        }

        // 5. Update review
        review.setRating(newRating);
        review.setComment(req.getComment());
        productReviewRepository.save(review);

        log.info("[REVIEW] Cập nhật thành công review ID: {}", req.getReviewId());
        return productReviewMapper.toDto(review);
    }

    @Override
    @Transactional
    public CommonResponseDto deleteReview(Long reviewId) {
        log.info("[REVIEW] Xóa đánh giá ID: {}", reviewId);

        // 1. Tìm review
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.warn("[NOT_FOUND] Không tìm thấy review ID: {}", reviewId);
                    return new NotFoundException("Review with ID: " + reviewId + " not found");
                });

        // 2. Kiểm tra đã bị xóa chưa
        if (Boolean.TRUE.equals(review.getDeleteFlag())) {
            throw new NotFoundException("Review with ID: " + reviewId + " not found");
        }

        // 3. Kiểm tra có thuộc về user hiện tại không
        // Security config chặn phân quyền → service chỉ check owner
        User currentUser = userService.getUserLogin();
        boolean isAdmin = currentUser.getRole().getName().equals(RoleConstant.ADMIN);
        if (!isAdmin && !review.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to delete this review");
        }

        // 4. Soft delete
        review.setDeleteFlag(Boolean.TRUE);
        productReviewRepository.save(review);
        log.info("[REVIEW] Soft delete thành công review ID: {}", reviewId);

        // 5. Tính lại avgRating + totalReviews trong Product
        Product product = review.getProduct();
        int newTotalReviews = product.getTotalReviews() - 1;
        double newAvgRating = newTotalReviews == 0
                ? 0.0
                : ((product.getAvgRating() * product.getTotalReviews())
                - review.getRating()) / newTotalReviews;

        product.setTotalReviews(newTotalReviews);
        product.setAvgRating(Math.round(newAvgRating * 10.0) / 10.0);
        productRepository.save(product);

        log.info("[REVIEW] Cập nhật avgRating: {} | totalReviews: {} | Product ID: {}",
                product.getAvgRating(), product.getTotalReviews(), product.getId());
        return new CommonResponseDto(true, "Delete review successfully");
    }

    @Override
    public ProductReviewResponseDto getReviewsByProduct(Long productId, Pageable pageable) {
        log.info("[REVIEW] Lấy danh sách đánh giá | Product ID: {}", productId);

        // 1. Kiểm tra product tồn tại
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("[NOT_FOUND] Không tìm thấy sản phẩm ID: {}", productId);
                    return new NotFoundException("Product with ID: " + productId + " not found");
                });

        // 2. Lấy danh sách review
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdDate").descending()
        );

        Page<ProductReview> page = productReviewRepository
                .findByProductIdAndDeleteFlagFalse(productId, pageable);

        // 3. Build ResultPaginationDto
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDto paginationDto = new ResultPaginationDto();
        paginationDto.setMeta(meta);
        paginationDto.setResult(productReviewMapper.toDtoList(page.getContent()));

        // 4. Build response
        ProductReviewResponseDto response = new ProductReviewResponseDto();
        response.setAvgRating(product.getAvgRating());
        response.setTotalReviews(product.getTotalReviews());
        response.setReviews(paginationDto);

        log.info("[REVIEW] Product ID: {} | avgRating: {} | totalReviews: {}",
                productId, product.getAvgRating(), product.getTotalReviews());

        return response;
    }

    @Override
    public ResultPaginationDto getAllReviews(List<String> filter, Pageable pageable) {
        log.info("[ADMIN-REVIEW] Lấy tất cả đánh giá");

        SpecificationBuilder<ProductReview> specification = new SpecificationBuilder<>();
        FilterProcessor.process(specification, filter);

        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdDate").descending()
        );
        Specification<ProductReview> spec = specification.build();
        Specification<ProductReview> softDeleteSpec = (root, query, cb) -> cb.equal(root.get("deleteFlag"), false);

        Specification<ProductReview> finalSpec = (spec == null) ? softDeleteSpec : spec.and(softDeleteSpec);

        // Thực thi query
        Page<ProductReview> page = productReviewRepository.findAll(finalSpec, pageable);

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDto result = new ResultPaginationDto();
        result.setMeta(meta);
        result.setResult(productReviewMapper.toDtoList(page.getContent()));

        log.info("[ADMIN-REVIEW] Tổng đánh giá: {}", page.getTotalElements());
        return result;
    }


}
