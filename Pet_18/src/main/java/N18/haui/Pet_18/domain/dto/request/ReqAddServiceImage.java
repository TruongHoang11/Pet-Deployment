package N18.haui.Pet_18.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqAddServiceImage {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private Boolean isThumbnail = false;
}
