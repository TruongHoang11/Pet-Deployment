package N18.haui.Pet_18.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_pet_service_reviews")
public class PetServiceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người thực hiện đánh giá

    @Column(nullable = false)
    private Integer rating; // Điểm đánh giá (ví dụ: từ 1 đến 5 sao) , //sp dc danh gia khi da mua

    @Column(columnDefinition = "TEXT")
    private String comment; // Nội dung nhận xét của khách hàng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private PetService petService; // Dịch vụ được đánh giá
}
