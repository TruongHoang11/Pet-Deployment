package N18.haui.Pet_18.domain.dto.response;

import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.constant.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentStatusDto {
    private Long orderId;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private String transactionId;   // mã giao dịch VNPay
    private String paymentMethod;   // VD: BANKING_VNPAY
}