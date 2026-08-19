package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.TypeInventory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReqInventoryTransaction {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Type is required")
    private TypeInventory type;

    private LocalDate fromDate;

    private LocalDate toDate;
}
