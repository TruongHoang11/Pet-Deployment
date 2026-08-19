package N18.haui.Pet_18.domain.entity;

import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_product_reviews")
public class ProductReview extends FlagUserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người thực hiện đánh giá

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Sản phẩm được đánh giá

    @Column(nullable = false)
    private Integer rating; // Điểm đánh giá (ví dụ: từ 1 đến 5 sao) , //sp dc danh gia khi da mua

    @Column(columnDefinition = "TEXT")
    private String comment; // Nội dung nhận xét của khách hàng


}
