package N18.haui.Pet_18.domain.dto.response;

import N18.haui.Pet_18.domain.dto.common.DateAuditing;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto extends DateAuditing {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private String categoryName;

    private Long categoryId;



    private Integer stockQuantity;

    private Double avgRating;
    private Integer totalReviews;
}
