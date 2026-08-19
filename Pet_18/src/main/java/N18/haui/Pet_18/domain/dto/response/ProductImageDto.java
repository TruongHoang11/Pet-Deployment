package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDto {

    private Long id;
    private String imageUrl;
    private Boolean isThumbnail;
    private Long productId;
}
