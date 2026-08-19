package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ServiceDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer durationMin;
    private Long categoryId;
    private String categoryName;
    private List<ServiceImageDto> serviceImages;
    private Double averageRating;
    private Integer totalReviews;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
