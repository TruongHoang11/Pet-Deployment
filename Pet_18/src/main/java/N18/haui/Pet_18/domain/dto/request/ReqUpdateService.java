package N18.haui.Pet_18.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateService {

    @NotNull(message = "Service ID is required")
    private Long id;

    private String name;

    private String description;

    private java.math.BigDecimal basePrice;

    private Integer durationMin;

    private Long categoryId;
}
