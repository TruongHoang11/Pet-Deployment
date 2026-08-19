package N18.haui.Pet_18.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReqSetThumbnailProduct {
    @NotNull(message = "Product ID is required")
    private Long productId;
    @NotNull(message = "Image ID is required")
    private Long imageId;
}
