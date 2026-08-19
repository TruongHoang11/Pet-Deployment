package N18.haui.Pet_18.repository;

import N18.haui.Pet_18.domain.entity.ProductImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>, JpaSpecificationExecutor<ProductImage> {
    List<ProductImage> findByProductId(Long productId);

    // Use the actual entity property name 'isThumbnail'
    boolean existsByProductIdAndIsThumbnailTrue(Long productId);

    // Hủy trạng thái ảnh chính của toàn bộ ảnh thuộc sản phẩm này
    @Modifying
    @Transactional
    @Query("UPDATE ProductImage p SET p.isThumbnail = false WHERE p.product.id = :productId")
    void resetMainImageByProductId(Long productId);
}
