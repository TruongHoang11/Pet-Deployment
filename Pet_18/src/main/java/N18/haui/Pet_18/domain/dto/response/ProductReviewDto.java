package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ProductReviewDto {
    private Long id;
    private Integer rating;
    private String comment;
    private String userName;    // tên người đánh giá
    private String avatarUrl; // ảnh đại diện người đánh giá
    private Long productId;
    private LocalDateTime createdDate;
}