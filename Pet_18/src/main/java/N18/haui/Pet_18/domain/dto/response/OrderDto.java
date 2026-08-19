package N18.haui.Pet_18.domain.dto.response;


import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.constant.OrderType;
import N18.haui.Pet_18.constant.PaymentMethod;
import N18.haui.Pet_18.constant.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto {

    private Long id;                            // ID của đơn hàng (Mã đơn hàng)
    private String shippingName;                // Tên người nhận hàng tại thời điểm đặt
    private String shippingPhone;               // Số điện thoại người nhận tại thời điểm đặt
    private String shippingAddressFull;         // Địa chỉ giao hàng chi tiết (dạng chuỗi text đóng băng)
    private BigDecimal totalAmount;             // Tổng thành tiền của toàn bộ đơn hàng
    private OrderStatus status;                  // Trạng thái đơn
    private PaymentStatus paymentStatus;        // Trạng thái thanh toán (PENDING, SUCCESS, FAILED)
    private PaymentMethod  paymentMethod;        // Phương thức thanh toán (MOMO, VNPAY, CASH)
    private List<OrderDetailDto> orderDetails;  // Danh sách các mặt hàng chi tiết bên trong đơn
    private LocalDateTime createdDate;          // Ngày giờ đặt hàng thành công
    private OrderType orderType;
}
