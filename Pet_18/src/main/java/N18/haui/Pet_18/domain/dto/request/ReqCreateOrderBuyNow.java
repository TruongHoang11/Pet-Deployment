package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.PaymentMethod;
import N18.haui.Pet_18.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateOrderBuyNow {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @NotNull(message = "Address ID is required")
    private Long addressId;

    @NotNull(message = "Payment method is required")
    @EnumValue(name = "paymentMethod", enumClass = PaymentMethod.class)
    private PaymentMethod paymentMethod;
}
