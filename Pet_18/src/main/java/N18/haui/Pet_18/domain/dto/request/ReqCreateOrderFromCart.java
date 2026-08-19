package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.PaymentMethod;
import N18.haui.Pet_18.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqCreateOrderFromCart {

    @NotNull(message = "Cart item IDs are required")
    private List<Long> cartItemIds; // chọn item nào mua

    @NotNull(message = "Address ID is required")
    private Long addressId;

    @NotNull(message = "Payment method is required")
    @EnumValue(name = "paymentMethod", enumClass = PaymentMethod.class)
    private PaymentMethod paymentMethod;
}
