package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.domain.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long>, JpaSpecificationExecutor<ProductReview> {
    // Kiểm tra đã đánh giá chưa
    boolean existsByUserIdAndProductIdAndDeleteFlagFalse(String userId, Long productId);

    Page<ProductReview> findByProductIdAndDeleteFlagFalse(Long productId, Pageable pageable);
}
